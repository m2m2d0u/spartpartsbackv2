package sn.symmetry.spareparts.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sn.symmetry.spareparts.dto.request.CreatePartImageRequest;
import sn.symmetry.spareparts.dto.request.CreatePartRequest;
import sn.symmetry.spareparts.dto.request.UpdatePartRequest;
import sn.symmetry.spareparts.dto.response.BulkImportResultResponse;
import sn.symmetry.spareparts.dto.response.PartImageResponse;
import sn.symmetry.spareparts.dto.response.PartResponse;
import sn.symmetry.spareparts.dto.response.common.ApiResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.service.PartImportService;
import sn.symmetry.spareparts.service.PartService;

import java.util.UUID;

@RestController
@RequestMapping("/api/parts")
@RequiredArgsConstructor
public class PartController {

    private final PartService partService;
    private final PartImportService partImportService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<PartResponse>>> getAllParts(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) Boolean published,
            @RequestParam(required = false) UUID carBrandId,
            @RequestParam(required = false) UUID carModelId,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(partService.getAllParts(categoryId, published, carBrandId, carModelId, pageable)));
    }

    @GetMapping("/not-in-warehouse")
    public ResponseEntity<ApiResponse<PagedResponse<PartResponse>>> getPartsNotInWarehouse(
            @RequestParam UUID warehouseId,
            @RequestParam(required = false) String name,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(partService.getPartsNotInWarehouse(warehouseId, name, pageable)));
    }

    @GetMapping("/in-warehouse")
    public ResponseEntity<ApiResponse<PagedResponse<PartResponse>>> getPartsInWarehouse(
            @RequestParam UUID warehouseId,
            @RequestParam(required = false) String name,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(partService.getPartsInWarehouse(warehouseId, name, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PartResponse>> getPartById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(partService.getPartById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PartResponse>> createPart(
            @Valid @RequestBody CreatePartRequest request) {
        PartResponse response = partService.createPart(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Part created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PartResponse>> updatePart(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePartRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Part updated successfully",
                partService.updatePart(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePart(@PathVariable UUID id) {
        partService.deletePart(id);
        return ResponseEntity.ok(ApiResponse.success("Part deleted successfully", null));
    }

    @GetMapping("/import-template")
    public ResponseEntity<byte[]> downloadImportTemplate() {
        try {
            byte[] template = partImportService.generateImportTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "parts_import_template.xlsx");
            headers.setContentLength(template.length);

            return new ResponseEntity<>(template, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(value = "/bulk-import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<BulkImportResultResponse>> bulkImportParts(
            @RequestPart("file") MultipartFile file) {

        // Validate file
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("File is empty"));
        }

        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.toLowerCase().endsWith(".csv") && !filename.toLowerCase().endsWith(".xlsx"))) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid file format. Only CSV and XLSX files are supported."));
        }

        BulkImportResultResponse result = partImportService.importParts(file);

        // Build response message
        String message;
        if (result.getSuccessCount() == 0 && result.getFailureCount() > 0) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Import failed. No parts were imported.", result));
        } else if (result.getFailureCount() > 0 || result.getDuplicateCount() > 0) {
            message = String.format("Import completed with issues. %d parts imported, %d failed, %d duplicates skipped.",
                    result.getSuccessCount(), result.getFailureCount(), result.getDuplicateCount());
        } else {
            message = String.format("Import completed successfully. %d parts imported.",
                    result.getSuccessCount());
        }

        return ResponseEntity.ok(ApiResponse.success(message, result));
    }

    @PostMapping("/{partId}/images")
    public ResponseEntity<ApiResponse<PartImageResponse>> addImageToPart(
            @PathVariable UUID partId,
            @Valid @RequestBody CreatePartImageRequest request) {
        PartImageResponse response = partService.addImageToPart(partId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Image added successfully", response));
    }

    @DeleteMapping("/{partId}/images/{imageId}")
    public ResponseEntity<ApiResponse<Void>> removeImageFromPart(
            @PathVariable UUID partId,
            @PathVariable UUID imageId) {
        partService.removeImageFromPart(partId, imageId);
        return ResponseEntity.ok(ApiResponse.success("Image removed successfully", null));
    }
}
