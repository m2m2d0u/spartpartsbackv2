package sn.symmetry.spareparts.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.symmetry.spareparts.dto.request.CreateInvoiceTemplateRequest;
import sn.symmetry.spareparts.dto.request.UpdateInvoiceTemplateRequest;
import sn.symmetry.spareparts.dto.response.InvoiceTemplateResponse;
import sn.symmetry.spareparts.dto.response.common.ApiResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.service.InvoiceTemplateService;

import java.util.UUID;

@RestController
@RequestMapping("/api/invoice-templates")
@RequiredArgsConstructor
public class InvoiceTemplateController {

    private final InvoiceTemplateService invoiceTemplateService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<InvoiceTemplateResponse>>> getAllInvoiceTemplates(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(invoiceTemplateService.getAllInvoiceTemplates(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InvoiceTemplateResponse>> getInvoiceTemplateById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(invoiceTemplateService.getInvoiceTemplateById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InvoiceTemplateResponse>> createInvoiceTemplate(
            @Valid @RequestBody CreateInvoiceTemplateRequest request) {
        InvoiceTemplateResponse response = invoiceTemplateService.createInvoiceTemplate(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Invoice template created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InvoiceTemplateResponse>> updateInvoiceTemplate(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateInvoiceTemplateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Invoice template updated successfully",
                invoiceTemplateService.updateInvoiceTemplate(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteInvoiceTemplate(@PathVariable UUID id) {
        invoiceTemplateService.deleteInvoiceTemplate(id);
        return ResponseEntity.ok(ApiResponse.success("Invoice template deleted successfully", null));
    }
}
