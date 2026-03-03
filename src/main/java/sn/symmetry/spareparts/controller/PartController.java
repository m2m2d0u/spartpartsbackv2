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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sn.symmetry.spareparts.dto.request.CreatePartImageRequest;
import sn.symmetry.spareparts.dto.request.CreatePartRequest;
import sn.symmetry.spareparts.dto.request.UpdatePartRequest;
import sn.symmetry.spareparts.dto.response.PartImageResponse;
import sn.symmetry.spareparts.dto.response.PartResponse;
import sn.symmetry.spareparts.dto.response.common.ApiResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.service.PartService;

@RestController
@RequestMapping("/api/parts")
@RequiredArgsConstructor
public class PartController {

    private final PartService partService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<PartResponse>>> getAllParts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean published,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(partService.getAllParts(categoryId, published, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PartResponse>> getPartById(@PathVariable Long id) {
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
            @PathVariable Long id,
            @Valid @RequestBody UpdatePartRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Part updated successfully",
                partService.updatePart(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePart(@PathVariable Long id) {
        partService.deletePart(id);
        return ResponseEntity.ok(ApiResponse.success("Part deleted successfully", null));
    }

    @PostMapping("/{partId}/images")
    public ResponseEntity<ApiResponse<PartImageResponse>> addImageToPart(
            @PathVariable Long partId,
            @Valid @RequestBody CreatePartImageRequest request) {
        PartImageResponse response = partService.addImageToPart(partId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Image added successfully", response));
    }

    @DeleteMapping("/{partId}/images/{imageId}")
    public ResponseEntity<ApiResponse<Void>> removeImageFromPart(
            @PathVariable Long partId,
            @PathVariable Long imageId) {
        partService.removeImageFromPart(partId, imageId);
        return ResponseEntity.ok(ApiResponse.success("Image removed successfully", null));
    }
}
