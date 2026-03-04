package sn.symmetry.spareparts.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sn.symmetry.spareparts.dto.request.CreateInvoiceTemplateRequest;
import sn.symmetry.spareparts.dto.request.UpdateInvoiceTemplateRequest;
import sn.symmetry.spareparts.dto.response.ImageResponse;
import sn.symmetry.spareparts.dto.response.InvoiceTemplateResponse;
import sn.symmetry.spareparts.dto.response.common.ApiResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.service.FileStorageService;
import sn.symmetry.spareparts.service.InvoiceTemplateService;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@RestController
@RequestMapping("/api/invoice-templates")
@RequiredArgsConstructor
public class InvoiceTemplateController {

    private final InvoiceTemplateService invoiceTemplateService;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<InvoiceTemplateResponse>>> getAllInvoiceTemplates(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
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

    @PostMapping(value = "/with-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<InvoiceTemplateResponse>> createInvoiceTemplateWithFiles(
            @RequestPart("template") String templateJson,
            @RequestPart(value = "logo", required = false) MultipartFile logo,
            @RequestPart(value = "stamp", required = false) MultipartFile stamp,
            @RequestPart(value = "headerImage", required = false) MultipartFile headerImage,
            @RequestPart(value = "footerImage", required = false) MultipartFile footerImage,
            @RequestPart(value = "signature", required = false) MultipartFile signature,
            @RequestPart(value = "watermark", required = false) MultipartFile watermark) {
        try {
            CreateInvoiceTemplateRequest request = objectMapper.readValue(templateJson, CreateInvoiceTemplateRequest.class);

            // Upload logo if provided
            if (logo != null && !logo.isEmpty()) {
                String logoUrl = fileStorageService.uploadFile(logo, "templates/logos");
                request.setLogoUrl(logoUrl);
            }

            // Upload stamp if provided
            if (stamp != null && !stamp.isEmpty()) {
                String stampUrl = fileStorageService.uploadFile(stamp, "templates/stamps");
                request.setStampImageUrl(stampUrl);
            }

            // Upload header image if provided
            if (headerImage != null && !headerImage.isEmpty()) {
                String headerImageUrl = fileStorageService.uploadFile(headerImage, "templates/headers");
                request.setHeaderImageUrl(headerImageUrl);
            }

            // Upload footer image if provided
            if (footerImage != null && !footerImage.isEmpty()) {
                String footerImageUrl = fileStorageService.uploadFile(footerImage, "templates/footers");
                request.setFooterImageUrl(footerImageUrl);
            }

            // Upload signature if provided
            if (signature != null && !signature.isEmpty()) {
                String signatureUrl = fileStorageService.uploadFile(signature, "templates/signatures");
                request.setSignatureImageUrl(signatureUrl);
            }

            // Upload watermark if provided
            if (watermark != null && !watermark.isEmpty()) {
                String watermarkUrl = fileStorageService.uploadFile(watermark, "templates/watermarks");
                request.setWatermarkImageUrl(watermarkUrl);
            }

            InvoiceTemplateResponse response = invoiceTemplateService.createInvoiceTemplate(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Invoice template created successfully", response));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to create invoice template: " + e.getMessage()));
        }
    }

    @PutMapping(value = "/{id}/with-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<InvoiceTemplateResponse>> updateInvoiceTemplateWithFiles(
            @PathVariable UUID id,
            @RequestPart("template") String templateJson,
            @RequestPart(value = "logo", required = false) MultipartFile logo,
            @RequestPart(value = "stamp", required = false) MultipartFile stamp,
            @RequestPart(value = "headerImage", required = false) MultipartFile headerImage,
            @RequestPart(value = "footerImage", required = false) MultipartFile footerImage,
            @RequestPart(value = "signature", required = false) MultipartFile signature,
            @RequestPart(value = "watermark", required = false) MultipartFile watermark) {
        try {
            UpdateInvoiceTemplateRequest request = objectMapper.readValue(templateJson, UpdateInvoiceTemplateRequest.class);

            // Get current template to delete old files if needed
            InvoiceTemplateResponse currentTemplate = invoiceTemplateService.getInvoiceTemplateById(id);

            // Upload new logo if provided
            if (logo != null && !logo.isEmpty()) {
                if (currentTemplate.getLogoUrl() != null) {
                    try {
                        fileStorageService.deleteFile(currentTemplate.getLogoUrl());
                    } catch (Exception e) {
                        // Log but don't fail if delete fails
                    }
                }
                String logoUrl = fileStorageService.uploadFile(logo, "templates/logos");
                request.setLogoUrl(logoUrl);
            }

            // Upload new stamp if provided
            if (stamp != null && !stamp.isEmpty()) {
                if (currentTemplate.getStampImageUrl() != null) {
                    try {
                        fileStorageService.deleteFile(currentTemplate.getStampImageUrl());
                    } catch (Exception e) {
                        // Log but don't fail if delete fails
                    }
                }
                String stampUrl = fileStorageService.uploadFile(stamp, "templates/stamps");
                request.setStampImageUrl(stampUrl);
            }

            // Upload new header image if provided
            if (headerImage != null && !headerImage.isEmpty()) {
                if (currentTemplate.getHeaderImageUrl() != null) {
                    try {
                        fileStorageService.deleteFile(currentTemplate.getHeaderImageUrl());
                    } catch (Exception e) {
                        // Log but don't fail if delete fails
                    }
                }
                String headerImageUrl = fileStorageService.uploadFile(headerImage, "templates/headers");
                request.setHeaderImageUrl(headerImageUrl);
            }

            // Upload new footer image if provided
            if (footerImage != null && !footerImage.isEmpty()) {
                if (currentTemplate.getFooterImageUrl() != null) {
                    try {
                        fileStorageService.deleteFile(currentTemplate.getFooterImageUrl());
                    } catch (Exception e) {
                        // Log but don't fail if delete fails
                    }
                }
                String footerImageUrl = fileStorageService.uploadFile(footerImage, "templates/footers");
                request.setFooterImageUrl(footerImageUrl);
            }

            // Upload new signature if provided
            if (signature != null && !signature.isEmpty()) {
                if (currentTemplate.getSignatureImageUrl() != null) {
                    try {
                        fileStorageService.deleteFile(currentTemplate.getSignatureImageUrl());
                    } catch (Exception e) {
                        // Log but don't fail if delete fails
                    }
                }
                String signatureUrl = fileStorageService.uploadFile(signature, "templates/signatures");
                request.setSignatureImageUrl(signatureUrl);
            }

            // Upload new watermark if provided
            if (watermark != null && !watermark.isEmpty()) {
                if (currentTemplate.getWatermarkImageUrl() != null) {
                    try {
                        fileStorageService.deleteFile(currentTemplate.getWatermarkImageUrl());
                    } catch (Exception e) {
                        // Log but don't fail if delete fails
                    }
                }
                String watermarkUrl = fileStorageService.uploadFile(watermark, "templates/watermarks");
                request.setWatermarkImageUrl(watermarkUrl);
            }

            InvoiceTemplateResponse response = invoiceTemplateService.updateInvoiceTemplate(id, request);
            return ResponseEntity.ok(ApiResponse.success("Invoice template updated successfully", response));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to update invoice template: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}/logo")
    public ResponseEntity<ApiResponse<ImageResponse>> getTemplateLogo(@PathVariable UUID id) {
        InvoiceTemplateResponse template = invoiceTemplateService.getInvoiceTemplateById(id);
        if (template.getLogoUrl() == null || template.getLogoUrl().isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success(null));
        }

        String base64 = fileStorageService.getFileAsBase64(template.getLogoUrl());
        ImageResponse response = ImageResponse.builder()
                .url(template.getLogoUrl())
                .base64(base64)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}/stamp")
    public ResponseEntity<ApiResponse<ImageResponse>> getTemplateStamp(@PathVariable UUID id) {
        InvoiceTemplateResponse template = invoiceTemplateService.getInvoiceTemplateById(id);
        if (template.getStampImageUrl() == null || template.getStampImageUrl().isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success(null));
        }

        String base64 = fileStorageService.getFileAsBase64(template.getStampImageUrl());
        ImageResponse response = ImageResponse.builder()
                .url(template.getStampImageUrl())
                .base64(base64)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}/header-image")
    public ResponseEntity<ApiResponse<ImageResponse>> getTemplateHeaderImage(@PathVariable UUID id) {
        InvoiceTemplateResponse template = invoiceTemplateService.getInvoiceTemplateById(id);
        if (template.getHeaderImageUrl() == null || template.getHeaderImageUrl().isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success(null));
        }

        String base64 = fileStorageService.getFileAsBase64(template.getHeaderImageUrl());
        ImageResponse response = ImageResponse.builder()
                .url(template.getHeaderImageUrl())
                .base64(base64)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}/footer-image")
    public ResponseEntity<ApiResponse<ImageResponse>> getTemplateFooterImage(@PathVariable UUID id) {
        InvoiceTemplateResponse template = invoiceTemplateService.getInvoiceTemplateById(id);
        if (template.getFooterImageUrl() == null || template.getFooterImageUrl().isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success(null));
        }

        String base64 = fileStorageService.getFileAsBase64(template.getFooterImageUrl());
        ImageResponse response = ImageResponse.builder()
                .url(template.getFooterImageUrl())
                .base64(base64)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}/signature")
    public ResponseEntity<ApiResponse<ImageResponse>> getTemplateSignature(@PathVariable UUID id) {
        InvoiceTemplateResponse template = invoiceTemplateService.getInvoiceTemplateById(id);
        if (template.getSignatureImageUrl() == null || template.getSignatureImageUrl().isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success(null));
        }

        String base64 = fileStorageService.getFileAsBase64(template.getSignatureImageUrl());
        ImageResponse response = ImageResponse.builder()
                .url(template.getSignatureImageUrl())
                .base64(base64)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}/watermark")
    public ResponseEntity<ApiResponse<ImageResponse>> getTemplateWatermark(@PathVariable UUID id) {
        InvoiceTemplateResponse template = invoiceTemplateService.getInvoiceTemplateById(id);
        if (template.getWatermarkImageUrl() == null || template.getWatermarkImageUrl().isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success(null));
        }

        String base64 = fileStorageService.getFileAsBase64(template.getWatermarkImageUrl());
        ImageResponse response = ImageResponse.builder()
                .url(template.getWatermarkImageUrl())
                .base64(base64)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
