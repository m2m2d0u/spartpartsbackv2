package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.symmetry.spareparts.dto.request.CreatePurchaseOrderRequest;
import sn.symmetry.spareparts.dto.request.PurchaseOrderItemRequest;
import sn.symmetry.spareparts.dto.request.UpdatePurchaseOrderRequest;
import sn.symmetry.spareparts.dto.response.PurchaseOrderResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.entity.Part;
import sn.symmetry.spareparts.entity.PurchaseOrder;
import sn.symmetry.spareparts.entity.PurchaseOrderItem;
import sn.symmetry.spareparts.entity.Supplier;
import sn.symmetry.spareparts.entity.Warehouse;
import sn.symmetry.spareparts.enums.PurchaseOrderStatus;
import sn.symmetry.spareparts.exception.BusinessRuleException;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.mapper.PurchaseOrderMapper;
import sn.symmetry.spareparts.repository.PartRepository;
import sn.symmetry.spareparts.repository.PurchaseOrderRepository;
import sn.symmetry.spareparts.repository.SupplierRepository;
import sn.symmetry.spareparts.repository.WarehouseRepository;
import sn.symmetry.spareparts.service.PurchaseOrderService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final WarehouseRepository warehouseRepository;
    private final PartRepository partRepository;
    private final PurchaseOrderMapper purchaseOrderMapper;

    @Override
    public PagedResponse<PurchaseOrderResponse> getAllPurchaseOrders(Long supplierId, PurchaseOrderStatus status, Pageable pageable) {
        Page<PurchaseOrder> page;
        if (supplierId != null && status != null) {
            page = purchaseOrderRepository.findBySupplierIdAndStatus(supplierId, status, pageable);
        } else if (supplierId != null) {
            page = purchaseOrderRepository.findBySupplierId(supplierId, pageable);
        } else if (status != null) {
            page = purchaseOrderRepository.findByStatus(status, pageable);
        } else {
            page = purchaseOrderRepository.findAll(pageable);
        }
        return PagedResponse.of(page.map(purchaseOrderMapper::toResponse));
    }

    @Override
    public PurchaseOrderResponse getPurchaseOrderById(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", id));
        return purchaseOrderMapper.toResponse(purchaseOrder);
    }

    @Override
    @Transactional
    public PurchaseOrderResponse createPurchaseOrder(CreatePurchaseOrderRequest request) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", request.getSupplierId()));

        Warehouse destinationWarehouse = null;
        if (request.getDestinationWarehouseId() != null) {
            destinationWarehouse = warehouseRepository.findById(request.getDestinationWarehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", request.getDestinationWarehouseId()));
        }

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setPoNumber(generatePoNumber());
        purchaseOrder.setSupplier(supplier);
        purchaseOrder.setStatus(request.getStatus() != null ? request.getStatus() : PurchaseOrderStatus.DRAFT);
        purchaseOrder.setOrderDate(request.getOrderDate());
        purchaseOrder.setExpectedDeliveryDate(request.getExpectedDeliveryDate());
        purchaseOrder.setDestinationWarehouse(destinationWarehouse);
        purchaseOrder.setNotes(request.getNotes());

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (PurchaseOrderItemRequest itemRequest : request.getItems()) {
            Part part = partRepository.findById(itemRequest.getPartId())
                    .orElseThrow(() -> new ResourceNotFoundException("Part", "id", itemRequest.getPartId()));

            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setPurchaseOrder(purchaseOrder);
            item.setPart(part);
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(itemRequest.getUnitPrice());
            item.setReceivedQuantity(0);
            purchaseOrder.getItems().add(item);

            totalAmount = totalAmount.add(itemRequest.getUnitPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
        }
        purchaseOrder.setTotalAmount(totalAmount);

        PurchaseOrder saved = purchaseOrderRepository.save(purchaseOrder);
        return purchaseOrderMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PurchaseOrderResponse updatePurchaseOrder(Long id, UpdatePurchaseOrderRequest request) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", id));

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", request.getSupplierId()));

        Warehouse destinationWarehouse = null;
        if (request.getDestinationWarehouseId() != null) {
            destinationWarehouse = warehouseRepository.findById(request.getDestinationWarehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", request.getDestinationWarehouseId()));
        }

        purchaseOrder.setSupplier(supplier);
        if (request.getStatus() != null) {
            purchaseOrder.setStatus(request.getStatus());
        }
        purchaseOrder.setOrderDate(request.getOrderDate());
        purchaseOrder.setExpectedDeliveryDate(request.getExpectedDeliveryDate());
        purchaseOrder.setDestinationWarehouse(destinationWarehouse);
        purchaseOrder.setNotes(request.getNotes());

        purchaseOrder.getItems().clear();

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (PurchaseOrderItemRequest itemRequest : request.getItems()) {
            Part part = partRepository.findById(itemRequest.getPartId())
                    .orElseThrow(() -> new ResourceNotFoundException("Part", "id", itemRequest.getPartId()));

            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setPurchaseOrder(purchaseOrder);
            item.setPart(part);
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(itemRequest.getUnitPrice());
            item.setReceivedQuantity(0);
            purchaseOrder.getItems().add(item);

            totalAmount = totalAmount.add(itemRequest.getUnitPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
        }
        purchaseOrder.setTotalAmount(totalAmount);

        PurchaseOrder saved = purchaseOrderRepository.save(purchaseOrder);
        return purchaseOrderMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deletePurchaseOrder(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", id));

        if (purchaseOrder.getStatus() != PurchaseOrderStatus.DRAFT) {
            throw new BusinessRuleException("Only purchase orders with DRAFT status can be deleted");
        }

        purchaseOrderRepository.delete(purchaseOrder);
    }

    private String generatePoNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uniquePart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String poNumber = "PO-" + datePart + "-" + uniquePart;

        while (purchaseOrderRepository.existsByPoNumber(poNumber)) {
            uniquePart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            poNumber = "PO-" + datePart + "-" + uniquePart;
        }

        return poNumber;
    }
}
