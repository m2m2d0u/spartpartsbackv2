package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.request.CreateInvoiceTemplateRequest;
import sn.symmetry.spareparts.dto.request.UpdateInvoiceTemplateRequest;
import sn.symmetry.spareparts.dto.response.InvoiceTemplateResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;

import java.util.UUID;

public interface InvoiceTemplateService {

    PagedResponse<InvoiceTemplateResponse> getAllInvoiceTemplates(Pageable pageable);

    InvoiceTemplateResponse getInvoiceTemplateById(UUID id);

    InvoiceTemplateResponse createInvoiceTemplate(CreateInvoiceTemplateRequest request);

    InvoiceTemplateResponse updateInvoiceTemplate(UUID id, UpdateInvoiceTemplateRequest request);

    void deleteInvoiceTemplate(UUID id);
}
