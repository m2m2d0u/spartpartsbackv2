package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.request.CreateInvoiceRequest;
import sn.symmetry.spareparts.dto.request.UpdateInvoiceRequest;
import sn.symmetry.spareparts.dto.request.UpdateInvoiceStatusRequest;
import sn.symmetry.spareparts.dto.response.InvoiceResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.enums.InvoiceStatus;
import sn.symmetry.spareparts.enums.InvoiceType;

public interface InvoiceService {

    PagedResponse<InvoiceResponse> getAllInvoices(Long customerId, InvoiceStatus status, InvoiceType invoiceType, Pageable pageable);

    InvoiceResponse getInvoiceById(Long id);

    InvoiceResponse createInvoice(CreateInvoiceRequest request);

    InvoiceResponse updateInvoice(Long id, UpdateInvoiceRequest request);

    InvoiceResponse updateInvoiceStatus(Long id, UpdateInvoiceStatusRequest request);

    void deleteInvoice(Long id);
}
