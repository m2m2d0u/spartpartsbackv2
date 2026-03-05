package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.symmetry.spareparts.dto.request.CreateInvoiceRequest;
import sn.symmetry.spareparts.dto.request.InvoiceItemRequest;
import sn.symmetry.spareparts.dto.request.UpdateInvoiceRequest;
import sn.symmetry.spareparts.dto.request.UpdateInvoiceStatusRequest;
import sn.symmetry.spareparts.dto.response.InvoiceResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.entity.ClientOrder;
import sn.symmetry.spareparts.entity.Customer;
import sn.symmetry.spareparts.entity.Invoice;
import sn.symmetry.spareparts.entity.InvoiceItem;
import sn.symmetry.spareparts.entity.InvoiceTemplate;
import sn.symmetry.spareparts.entity.Part;
import sn.symmetry.spareparts.entity.Warehouse;
import sn.symmetry.spareparts.enums.InvoiceStatus;
import sn.symmetry.spareparts.enums.InvoiceType;
import sn.symmetry.spareparts.exception.BusinessRuleException;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.mapper.InvoiceMapper;
import sn.symmetry.spareparts.repository.ClientOrderRepository;
import sn.symmetry.spareparts.repository.CustomerRepository;
import sn.symmetry.spareparts.repository.InvoiceRepository;
import sn.symmetry.spareparts.repository.InvoiceTemplateRepository;
import sn.symmetry.spareparts.repository.PartRepository;
import sn.symmetry.spareparts.repository.WarehouseRepository;
import sn.symmetry.spareparts.service.InvoiceService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final ClientOrderRepository clientOrderRepository;
    private final InvoiceTemplateRepository invoiceTemplateRepository;
    private final WarehouseRepository warehouseRepository;
    private final PartRepository partRepository;
    private final InvoiceMapper invoiceMapper;

    @Override
    public PagedResponse<InvoiceResponse> getAllInvoices(UUID customerId, InvoiceStatus status, InvoiceType invoiceType, Pageable pageable) {
        Page<Invoice> page;

        if (customerId != null && status != null) {
            page = invoiceRepository.findByCustomerIdAndStatus(customerId, status, pageable);
        } else if (customerId != null) {
            page = invoiceRepository.findByCustomerId(customerId, pageable);
        } else if (status != null) {
            page = invoiceRepository.findByStatus(status, pageable);
        } else if (invoiceType != null) {
            page = invoiceRepository.findByInvoiceType(invoiceType, pageable);
        } else {
            page = invoiceRepository.findAll(pageable);
        }

        return PagedResponse.of(page.map(invoiceMapper::toResponse));
    }

    @Override
    public InvoiceResponse getInvoiceById(UUID id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));
        return invoiceMapper.toResponse(invoice);
    }

    @Override
    @Transactional
    public InvoiceResponse createInvoice(CreateInvoiceRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerId()));

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setInvoiceType(request.getInvoiceType());
        invoice.setCustomer(customer);
        invoice.setStatus(InvoiceStatus.DRAFT);
        invoice.setIssuedDate(request.getIssuedDate());
        invoice.setDueDate(request.getDueDate());
        invoice.setValidityDate(request.getValidityDate());
        invoice.setNotes(request.getNotes());
        invoice.setInternalNotes(request.getInternalNotes());

        if (request.getOrderId() != null) {
            ClientOrder order = clientOrderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("ClientOrder", "id", request.getOrderId()));
            invoice.setOrder(order);
        }

        if (request.getProformaId() != null) {
            Invoice proforma = invoiceRepository.findById(request.getProformaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invoice (proforma)", "id", request.getProformaId()));
            invoice.setProforma(proforma);
        }

        if (request.getDepositId() != null) {
            Invoice deposit = invoiceRepository.findById(request.getDepositId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invoice (deposit)", "id", request.getDepositId()));
            invoice.setDeposit(deposit);
        }

        if (request.getTemplateId() != null) {
            InvoiceTemplate template = invoiceTemplateRepository.findById(request.getTemplateId())
                    .orElseThrow(() -> new ResourceNotFoundException("InvoiceTemplate", "id", request.getTemplateId()));
            invoice.setTemplate(template);
        }

        if (request.getSourceWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findById(request.getSourceWarehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", request.getSourceWarehouseId()));
            invoice.setSourceWarehouse(warehouse);
        }

        List<InvoiceItem> items = buildInvoiceItems(request.getItems(), invoice);
        invoice.setItems(items);

        calculateInvoiceTotals(invoice);

        Invoice saved = invoiceRepository.save(invoice);
        return invoiceMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public InvoiceResponse updateInvoice(UUID id, UpdateInvoiceRequest request) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));

        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new BusinessRuleException("Only invoices in DRAFT status can be updated");
        }

        if (request.getIssuedDate() != null) {
            invoice.setIssuedDate(request.getIssuedDate());
        }
        invoice.setDueDate(request.getDueDate());
        invoice.setValidityDate(request.getValidityDate());
        invoice.setNotes(request.getNotes());
        invoice.setInternalNotes(request.getInternalNotes());
        invoice.setInvoiceType(request.getInvoiceType());

        if (request.getOrderId() != null) {
            ClientOrder order = clientOrderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("ClientOrder", "id", request.getOrderId()));
            invoice.setOrder(order);
        } else {
            invoice.setOrder(null);
        }

        if (request.getProformaId() != null) {
            Invoice proforma = invoiceRepository.findById(request.getProformaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invoice (proforma)", "id", request.getProformaId()));
            invoice.setProforma(proforma);
        } else {
            invoice.setProforma(null);
        }

        if (request.getDepositId() != null) {
            Invoice deposit = invoiceRepository.findById(request.getDepositId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invoice (deposit)", "id", request.getDepositId()));
            invoice.setDeposit(deposit);
        } else {
            invoice.setDeposit(null);
        }

        if (request.getTemplateId() != null) {
            InvoiceTemplate template = invoiceTemplateRepository.findById(request.getTemplateId())
                    .orElseThrow(() -> new ResourceNotFoundException("InvoiceTemplate", "id", request.getTemplateId()));
            invoice.setTemplate(template);
        } else {
            invoice.setTemplate(null);
        }

        if (request.getSourceWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findById(request.getSourceWarehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", request.getSourceWarehouseId()));
            invoice.setSourceWarehouse(warehouse);
        } else {
            invoice.setSourceWarehouse(null);
        }

        invoice.getItems().clear();
        List<InvoiceItem> items = buildInvoiceItems(request.getItems(), invoice);
        invoice.getItems().addAll(items);

        calculateInvoiceTotals(invoice);

        Invoice saved = invoiceRepository.save(invoice);
        return invoiceMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public InvoiceResponse updateInvoiceStatus(UUID id, UpdateInvoiceStatusRequest request) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));

        invoice.setStatus(request.getStatus());

        // Auto-convert PROFORMA to STANDARD when status moves to a payment-related state
        if (invoice.getInvoiceType() == InvoiceType.PROFORMA &&
                (request.getStatus() == InvoiceStatus.PAID ||
                 request.getStatus() == InvoiceStatus.PARTIALLY_PAID ||
                 request.getStatus() == InvoiceStatus.OVERDUE)) {
            invoice.setInvoiceType(InvoiceType.STANDARD);
        }

        if (request.getStatus() == InvoiceStatus.PAID) {
            invoice.setPaidDate(LocalDate.now());
        }

        Invoice saved = invoiceRepository.save(invoice);
        return invoiceMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteInvoice(UUID id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));

        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new BusinessRuleException("Only invoices in DRAFT status can be deleted");
        }

        invoiceRepository.delete(invoice);
    }

    private List<InvoiceItem> buildInvoiceItems(List<InvoiceItemRequest> itemRequests, Invoice invoice) {
        List<InvoiceItem> items = new ArrayList<>();

        for (InvoiceItemRequest itemRequest : itemRequests) {
            Part part = partRepository.findById(itemRequest.getPartId())
                    .orElseThrow(() -> new ResourceNotFoundException("Part", "id", itemRequest.getPartId()));

            InvoiceItem item = new InvoiceItem();
            item.setInvoice(invoice);
            item.setPart(part);
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(itemRequest.getUnitPrice());

            BigDecimal discountPercent = itemRequest.getDiscountPercent() != null
                    ? itemRequest.getDiscountPercent()
                    : BigDecimal.ZERO;
            item.setDiscountPercent(discountPercent);

            BigDecimal lineTotal = itemRequest.getUnitPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            BigDecimal discountAmount = lineTotal
                    .multiply(discountPercent)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal totalPrice = lineTotal.subtract(discountAmount);

            item.setDiscountAmount(discountAmount);
            item.setTotalPrice(totalPrice);

            items.add(item);
        }

        return items;
    }

    private void calculateInvoiceTotals(Invoice invoice) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (InvoiceItem item : invoice.getItems()) {
            subtotal = subtotal.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            totalDiscount = totalDiscount.add(item.getDiscountAmount());
        }

        invoice.setSubtotal(subtotal);
        invoice.setDiscountAmount(totalDiscount);
        invoice.setTotalAmount(subtotal.subtract(totalDiscount)
                .add(invoice.getTaxAmount())
                .subtract(invoice.getDepositDeduction()));
    }

    private String generateInvoiceNumber() {
        String number;
        do {
            number = "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (invoiceRepository.existsByInvoiceNumber(number));
        return number;
    }
}
