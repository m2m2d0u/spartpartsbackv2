package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.request.CreateInvoiceTemplateRequest;
import sn.symmetry.spareparts.dto.request.UpdateInvoiceTemplateRequest;
import sn.symmetry.spareparts.dto.response.InvoiceTemplateResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;

public interface InvoiceTemplateService {

    PagedResponse<InvoiceTemplateResponse> getAllInvoiceTemplates(Pageable pageable);

    InvoiceTemplateResponse getInvoiceTemplateById(Long id);

    InvoiceTemplateResponse createInvoiceTemplate(CreateInvoiceTemplateRequest request);

    InvoiceTemplateResponse updateInvoiceTemplate(Long id, UpdateInvoiceTemplateRequest request);

    void deleteInvoiceTemplate(Long id);
}
