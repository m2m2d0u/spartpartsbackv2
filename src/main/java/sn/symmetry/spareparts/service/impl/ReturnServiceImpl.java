package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.symmetry.spareparts.dto.request.CreateReturnRequest;
import sn.symmetry.spareparts.dto.request.ReturnItemRequest;
import sn.symmetry.spareparts.dto.request.UpdateReturnRequest;
import sn.symmetry.spareparts.dto.request.UpdateReturnStatusRequest;
import sn.symmetry.spareparts.dto.response.ReturnResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.entity.ClientOrder;
import sn.symmetry.spareparts.entity.Customer;
import sn.symmetry.spareparts.entity.Invoice;
import sn.symmetry.spareparts.entity.Part;
import sn.symmetry.spareparts.entity.Return;
import sn.symmetry.spareparts.entity.ReturnItem;
import sn.symmetry.spareparts.entity.Warehouse;
import sn.symmetry.spareparts.enums.ReturnStatus;
import sn.symmetry.spareparts.exception.BusinessRuleException;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.mapper.ReturnMapper;
import sn.symmetry.spareparts.repository.ClientOrderRepository;
import sn.symmetry.spareparts.repository.CustomerRepository;
import sn.symmetry.spareparts.repository.InvoiceRepository;
import sn.symmetry.spareparts.repository.PartRepository;
import sn.symmetry.spareparts.repository.ReturnRepository;
import sn.symmetry.spareparts.repository.WarehouseRepository;
import sn.symmetry.spareparts.service.ReturnService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReturnServiceImpl implements ReturnService {

    private final ReturnRepository returnRepository;
    private final CustomerRepository customerRepository;
    private final InvoiceRepository invoiceRepository;
    private final ClientOrderRepository clientOrderRepository;
    private final PartRepository partRepository;
    private final WarehouseRepository warehouseRepository;
    private final ReturnMapper returnMapper;

    @Override
    public PagedResponse<ReturnResponse> getAllReturns(Long customerId, ReturnStatus status, Pageable pageable) {
        Page<Return> page;

        if (customerId != null) {
            page = returnRepository.findByCustomerId(customerId, pageable);
        } else if (status != null) {
            page = returnRepository.findByStatus(status, pageable);
        } else {
            page = returnRepository.findAll(pageable);
        }

        return PagedResponse.of(page.map(returnMapper::toResponse));
    }

    @Override
    public ReturnResponse getReturnById(Long id) {
        Return returnEntity = returnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Return", "id", id));
        return returnMapper.toResponse(returnEntity);
    }

    @Override
    @Transactional
    public ReturnResponse createReturn(CreateReturnRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerId()));

        Return returnEntity = new Return();
        returnEntity.setReturnNumber(generateReturnNumber());
        returnEntity.setCustomer(customer);
        returnEntity.setStatus(ReturnStatus.REQUESTED);
        returnEntity.setReturnDate(request.getReturnDate());
        returnEntity.setNotes(request.getNotes());

        if (request.getInvoiceId() != null) {
            Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", request.getInvoiceId()));
            returnEntity.setInvoice(invoice);
        }

        if (request.getOrderId() != null) {
            ClientOrder order = clientOrderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("ClientOrder", "id", request.getOrderId()));
            returnEntity.setOrder(order);
        }

        List<ReturnItem> items = buildReturnItems(request.getItems(), returnEntity);
        returnEntity.setItems(items);

        Return saved = returnRepository.save(returnEntity);
        return returnMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ReturnResponse updateReturn(Long id, UpdateReturnRequest request) {
        Return returnEntity = returnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Return", "id", id));

        if (returnEntity.getStatus() != ReturnStatus.REQUESTED) {
            throw new BusinessRuleException("Only returns in REQUESTED status can be updated");
        }

        if (request.getReturnDate() != null) {
            returnEntity.setReturnDate(request.getReturnDate());
        }
        returnEntity.setNotes(request.getNotes());

        if (request.getInvoiceId() != null) {
            Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", request.getInvoiceId()));
            returnEntity.setInvoice(invoice);
        } else {
            returnEntity.setInvoice(null);
        }

        if (request.getOrderId() != null) {
            ClientOrder order = clientOrderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("ClientOrder", "id", request.getOrderId()));
            returnEntity.setOrder(order);
        } else {
            returnEntity.setOrder(null);
        }

        returnEntity.getItems().clear();
        List<ReturnItem> items = buildReturnItems(request.getItems(), returnEntity);
        returnEntity.getItems().addAll(items);

        Return saved = returnRepository.save(returnEntity);
        return returnMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ReturnResponse updateReturnStatus(Long id, UpdateReturnStatusRequest request) {
        Return returnEntity = returnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Return", "id", id));

        returnEntity.setStatus(request.getStatus());

        Return saved = returnRepository.save(returnEntity);
        return returnMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteReturn(Long id) {
        Return returnEntity = returnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Return", "id", id));

        if (returnEntity.getStatus() != ReturnStatus.REQUESTED) {
            throw new BusinessRuleException("Only returns in REQUESTED status can be deleted");
        }

        returnRepository.delete(returnEntity);
    }

    private List<ReturnItem> buildReturnItems(List<ReturnItemRequest> itemRequests, Return returnEntity) {
        List<ReturnItem> items = new ArrayList<>();

        for (ReturnItemRequest itemRequest : itemRequests) {
            Part part = partRepository.findById(itemRequest.getPartId())
                    .orElseThrow(() -> new ResourceNotFoundException("Part", "id", itemRequest.getPartId()));

            ReturnItem item = new ReturnItem();
            item.setReturnEntity(returnEntity);
            item.setPart(part);
            item.setQuantity(itemRequest.getQuantity());
            item.setReason(itemRequest.getReason());
            item.setRestockAction(itemRequest.getRestockAction());

            if (itemRequest.getWarehouseId() != null) {
                Warehouse warehouse = warehouseRepository.findById(itemRequest.getWarehouseId())
                        .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", itemRequest.getWarehouseId()));
                item.setWarehouse(warehouse);
            }

            items.add(item);
        }

        return items;
    }

    private String generateReturnNumber() {
        String number;
        do {
            number = "RET-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (returnRepository.existsByReturnNumber(number));
        return number;
    }
}
