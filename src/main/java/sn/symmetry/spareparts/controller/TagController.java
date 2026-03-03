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
import sn.symmetry.spareparts.dto.request.CreateTagRequest;
import sn.symmetry.spareparts.dto.request.UpdateTagRequest;
import sn.symmetry.spareparts.dto.response.TagResponse;
import sn.symmetry.spareparts.dto.response.common.ApiResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.service.TagService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<TagResponse>>> getAllTags(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(tagService.getAllTags(pageable)));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<TagResponse>>> getAllTagsList() {
        return ResponseEntity.ok(ApiResponse.success(tagService.getAllTagsList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TagResponse>> getTagById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(tagService.getTagById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TagResponse>> createTag(
            @Valid @RequestBody CreateTagRequest request) {
        TagResponse response = tagService.createTag(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tag created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TagResponse>> updateTag(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTagRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Tag updated successfully",
                tagService.updateTag(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTag(@PathVariable UUID id) {
        tagService.deleteTag(id);
        return ResponseEntity.ok(ApiResponse.success("Tag deleted successfully", null));
    }
}
