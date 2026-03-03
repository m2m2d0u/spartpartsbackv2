package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.symmetry.spareparts.dto.request.CreateClientOrderRequest;
import sn.symmetry.spareparts.dto.request.OrderItemRequest;
import sn.symmetry.spareparts.dto.request.UpdateClientOrderRequest;
import sn.symmetry.spareparts.dto.request.UpdateOrderStatusRequest;
import sn.symmetry.spareparts.dto.response.ClientOrderResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.entity.ClientOrder;
import sn.symmetry.spareparts.entity.Customer;
import sn.symmetry.spareparts.entity.OrderItem;
import sn.symmetry.spareparts.entity.Part;
import sn.symmetry.spareparts.entity.Warehouse;
import sn.symmetry.spareparts.enums.OrderStatus;
import sn.symmetry.spareparts.exception.BusinessRuleException;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.mapper.ClientOrderMapper;
import sn.symmetry.spareparts.repository.ClientOrderRepository;
import sn.symmetry.spareparts.repository.CustomerRepository;
import sn.symmetry.spareparts.repository.PartRepository;
import sn.symmetry.spareparts.repository.WarehouseRepository;
import sn.symmetry.spareparts.service.ClientOrderService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientOrderServiceImpl implements ClientOrderService {

    private final ClientOrderRepository clientOrderRepository;
    private final CustomerRepository customerRepository;
    private final WarehouseRepository warehouseRepository;
    private final PartRepository partRepository;
    private final ClientOrderMapper clientOrderMapper;

    @Override
    public PagedResponse<ClientOrderResponse> getAllOrders(UUID customerId, OrderStatus status, Pageable pageable) {
        Page<ClientOrder> page;
        if (customerId != null && status != null) {
            page = clientOrderRepository.findByCustomerIdAndStatus(customerId, status, pageable);
        } else if (customerId != null) {
            page = clientOrderRepository.findByCustomerId(customerId, pageable);
        } else if (status != null) {
            page = clientOrderRepository.findByStatus(status, pageable);
        } else {
            page = clientOrderRepository.findAll(pageable);
        }
        return PagedResponse.of(page.map(clientOrderMapper::toResponse));
    }

    @Override
    public ClientOrderResponse getOrderById(UUID id) {
        ClientOrder order = clientOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClientOrder", "id", id));
        return clientOrderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public ClientOrderResponse createOrder(CreateClientOrderRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerId()));

        Warehouse warehouse = null;
        if (request.getWarehouseId() != null) {
            warehouse = warehouseRepository.findById(request.getWarehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", request.getWarehouseId()));
        }

        ClientOrder order = new ClientOrder();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomer(customer);
        order.setWarehouse(warehouse);
        order.setStatus(OrderStatus.PENDING);
        order.setShippingStreet(request.getShippingStreet());
        order.setShippingCity(request.getShippingCity());
        order.setShippingState(request.getShippingState());
        order.setShippingPostal(request.getShippingPostal());
        order.setShippingCountry(request.getShippingCountry());
        order.setNotes(request.getNotes());
        order.setOrderDate(LocalDateTime.now());

        List<OrderItem> items = buildOrderItems(request.getItems(), order);
        order.setItems(items);

        BigDecimal subtotal = calculateSubtotal(items);
        order.setSubtotal(subtotal);
        order.setTotalAmount(subtotal);

        ClientOrder saved = clientOrderRepository.save(order);
        return clientOrderMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ClientOrderResponse updateOrder(UUID id, UpdateClientOrderRequest request) {
        ClientOrder order = clientOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClientOrder", "id", id));

        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new BusinessRuleException(
                    "Order can only be updated when status is PENDING or CONFIRMED. Current status: " + order.getStatus());
        }

        Warehouse warehouse = null;
        if (request.getWarehouseId() != null) {
            warehouse = warehouseRepository.findById(request.getWarehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", request.getWarehouseId()));
        }
        order.setWarehouse(warehouse);

        clientOrderMapper.updateEntity(request, order);

        order.getItems().clear();
        List<OrderItem> newItems = buildOrderItems(request.getItems(), order);
        order.getItems().addAll(newItems);

        BigDecimal subtotal = calculateSubtotal(order.getItems());
        order.setSubtotal(subtotal);
        order.setTotalAmount(subtotal);

        ClientOrder saved = clientOrderRepository.save(order);
        return clientOrderMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ClientOrderResponse updateOrderStatus(UUID id, UpdateOrderStatusRequest request) {
        ClientOrder order = clientOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClientOrder", "id", id));

        validateStatusTransition(order.getStatus(), request.getStatus());

        order.setStatus(request.getStatus());
        if (request.getTrackingNumber() != null) {
            order.setTrackingNumber(request.getTrackingNumber());
        }

        ClientOrder saved = clientOrderRepository.save(order);
        return clientOrderMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteOrder(UUID id) {
        ClientOrder order = clientOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClientOrder", "id", id));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessRuleException(
                    "Order can only be deleted when status is PENDING. Current status: " + order.getStatus());
        }

        clientOrderRepository.delete(order);
    }

    private List<OrderItem> buildOrderItems(List<OrderItemRequest> itemRequests, ClientOrder order) {
        List<OrderItem> items = new ArrayList<>();
        for (OrderItemRequest itemRequest : itemRequests) {
            Part part = partRepository.findById(itemRequest.getPartId())
                    .orElseThrow(() -> new ResourceNotFoundException("Part", "id", itemRequest.getPartId()));

            OrderItem item = new OrderItem();
            item.setClientOrder(order);
            item.setPart(part);
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(itemRequest.getUnitPrice());
            item.setTotalPrice(itemRequest.getUnitPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
            items.add(item);
        }
        return items;
    }

    private BigDecimal calculateSubtotal(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String generateOrderNumber() {
        String orderNumber;
        do {
            orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (clientOrderRepository.existsByOrderNumber(orderNumber));
        return orderNumber;
    }

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        if (currentStatus == OrderStatus.CANCELLED) {
            throw new BusinessRuleException("Cannot change status of a cancelled order");
        }

        if (currentStatus == OrderStatus.COMPLETED) {
            throw new BusinessRuleException("Cannot change status of a completed order");
        }

        if (newStatus == OrderStatus.CANCELLED) {
            return;
        }

        if (newStatus.ordinal() < currentStatus.ordinal()) {
            throw new BusinessRuleException(
                    "Cannot transition from " + currentStatus + " to " + newStatus + ". Status cannot go backward.");
        }
    }
}
