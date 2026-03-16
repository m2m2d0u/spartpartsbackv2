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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;
import sn.symmetry.spareparts.dto.request.CreateInvoiceTemplateRequest;
import sn.symmetry.spareparts.dto.request.UpdateInvoiceTemplateRequest;
import sn.symmetry.spareparts.dto.response.ImageResponse;
import sn.symmetry.spareparts.dto.response.InvoiceTemplateResponse;
import sn.symmetry.spareparts.dto.response.common.ApiResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.enums.InvoiceDesign;
import sn.symmetry.spareparts.service.FileStorageService;
import sn.symmetry.spareparts.service.InvoicePdfService;
import sn.symmetry.spareparts.service.InvoiceTemplateService;
import tools.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/invoice-templates")
@RequiredArgsConstructor
public class InvoiceTemplateController {

    private final InvoiceTemplateService invoiceTemplateService;
    private final InvoicePdfService invoicePdfService;
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

    @PostMapping(value = "/designs/{design}/preview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ByteArrayResource> previewDesign(
            @PathVariable InvoiceDesign design,
            @RequestPart("template") String templateJson,
            @RequestPart(value = "logo", required = false) MultipartFile logo,
            @RequestPart(value = "stamp", required = false) MultipartFile stamp) {
        try {
            CreateInvoiceTemplateRequest request = objectMapper.readValue(templateJson, CreateInvoiceTemplateRequest.class);

            Map<String, byte[]> uploadedImages = new HashMap<>();
            if (logo != null && !logo.isEmpty()) {
                uploadedImages.put("logo", logo.getBytes());
            }
            if (stamp != null && !stamp.isEmpty()) {
                uploadedImages.put("stamp", stamp.getBytes());
            }

            ByteArrayOutputStream pdfStream = invoicePdfService.generateDesignPreviewPdf(design, request, uploadedImages);
            byte[] pdfBytes = pdfStream.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header("Content-Disposition", "inline; filename=\"preview-" + design.name().toLowerCase() + ".pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate design preview: " + e.getMessage(), e);
        }
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
                request.setLogoUrl(fileStorageService.uploadFileReturnReference(logo, "templates/logos"));
            }

            // Upload stamp if provided
            if (stamp != null && !stamp.isEmpty()) {
                request.setStampImageUrl(fileStorageService.uploadFileReturnReference(stamp, "templates/stamps"));
            }

            // Upload header image if provided
            if (headerImage != null && !headerImage.isEmpty()) {
                request.setHeaderImageUrl(fileStorageService.uploadFileReturnReference(headerImage, "templates/headers"));
            }

            // Upload footer image if provided
            if (footerImage != null && !footerImage.isEmpty()) {
                request.setFooterImageUrl(fileStorageService.uploadFileReturnReference(footerImage, "templates/footers"));
            }

            // Upload signature if provided
            if (signature != null && !signature.isEmpty()) {
                request.setSignatureImageUrl(fileStorageService.uploadFileReturnReference(signature, "templates/signatures"));
            }

            // Upload watermark if provided
            if (watermark != null && !watermark.isEmpty()) {
                request.setWatermarkImageUrl(fileStorageService.uploadFileReturnReference(watermark, "templates/watermarks"));
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
                        fileStorageService.deleteFileByReference(extractReference(currentTemplate.getLogoUrl()));
                    } catch (Exception ignored) {}
                }
                request.setLogoUrl(fileStorageService.uploadFileReturnReference(logo, "templates/logos"));
            }

            // Upload new stamp if provided
            if (stamp != null && !stamp.isEmpty()) {
                if (currentTemplate.getStampImageUrl() != null) {
                    try {
                        fileStorageService.deleteFileByReference(extractReference(currentTemplate.getStampImageUrl()));
                    } catch (Exception ignored) {}
                }
                request.setStampImageUrl(fileStorageService.uploadFileReturnReference(stamp, "templates/stamps"));
            }

            // Upload new header image if provided
            if (headerImage != null && !headerImage.isEmpty()) {
                if (currentTemplate.getHeaderImageUrl() != null) {
                    try {
                        fileStorageService.deleteFileByReference(extractReference(currentTemplate.getHeaderImageUrl()));
                    } catch (Exception ignored) {}
                }
                request.setHeaderImageUrl(fileStorageService.uploadFileReturnReference(headerImage, "templates/headers"));
            }

            // Upload new footer image if provided
            if (footerImage != null && !footerImage.isEmpty()) {
                if (currentTemplate.getFooterImageUrl() != null) {
                    try {
                        fileStorageService.deleteFileByReference(extractReference(currentTemplate.getFooterImageUrl()));
                    } catch (Exception ignored) {}
                }
                request.setFooterImageUrl(fileStorageService.uploadFileReturnReference(footerImage, "templates/footers"));
            }

            // Upload new signature if provided
            if (signature != null && !signature.isEmpty()) {
                if (currentTemplate.getSignatureImageUrl() != null) {
                    try {
                        fileStorageService.deleteFileByReference(extractReference(currentTemplate.getSignatureImageUrl()));
                    } catch (Exception ignored) {}
                }
                request.setSignatureImageUrl(fileStorageService.uploadFileReturnReference(signature, "templates/signatures"));
            }

            // Upload new watermark if provided
            if (watermark != null && !watermark.isEmpty()) {
                if (currentTemplate.getWatermarkImageUrl() != null) {
                    try {
                        fileStorageService.deleteFileByReference(extractReference(currentTemplate.getWatermarkImageUrl()));
                    } catch (Exception ignored) {}
                }
                request.setWatermarkImageUrl(fileStorageService.uploadFileReturnReference(watermark, "templates/watermarks"));
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

    /**
     * Extracts the MinIO object reference from a value that may be either a full URL
     * (legacy: http://host/bucket/ref) or already a plain object reference.
     */
    private String extractReference(String urlOrReference) {
        if (urlOrReference == null || !urlOrReference.startsWith("http")) {
            return urlOrReference;
        }
        int idx = urlOrReference.indexOf("/spareparts/");
        if (idx != -1) {
            return urlOrReference.substring(idx + "/spareparts/".length());
        }
        return urlOrReference;
    }
}
