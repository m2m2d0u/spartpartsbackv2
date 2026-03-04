package sn.symmetry.spareparts.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sn.symmetry.spareparts.dto.request.CreateStoreRequest;
import sn.symmetry.spareparts.dto.request.UpdateStoreRequest;
import sn.symmetry.spareparts.dto.response.ImageResponse;
import sn.symmetry.spareparts.dto.response.StoreResponse;
import sn.symmetry.spareparts.dto.response.UserResponse;
import sn.symmetry.spareparts.dto.response.common.ApiResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.service.FileStorageService;
import sn.symmetry.spareparts.service.StoreService;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PagedResponse<StoreResponse>>> getAllStores(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PagedResponse<StoreResponse> response = storeService.getAllStores(name, isActive, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<StoreResponse>> getStoreById(@PathVariable UUID id) {
        StoreResponse response = storeService.getStoreById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('STORE_CREATE')")
    public ResponseEntity<ApiResponse<StoreResponse>> createStore(
            @Valid @RequestBody CreateStoreRequest request) {
        StoreResponse response = storeService.createStore(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Store created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('STORE_UPDATE')")
    public ResponseEntity<ApiResponse<StoreResponse>> updateStore(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStoreRequest request) {
        StoreResponse response = storeService.updateStore(id, request);
        return ResponseEntity.ok(ApiResponse.success("Store updated successfully", response));
    }

    @PostMapping(value = "/with-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('STORE_CREATE')")
    public ResponseEntity<ApiResponse<StoreResponse>> createStoreWithFiles(
            @RequestPart("store") String storeJson,
            @RequestPart(value = "logo", required = false) MultipartFile logo,
            @RequestPart(value = "stamp", required = false) MultipartFile stamp) {
        try {
            CreateStoreRequest request = objectMapper.readValue(storeJson, CreateStoreRequest.class);

            // Upload logo if provided
            if (logo != null && !logo.isEmpty()) {
                String logoUrl = fileStorageService.uploadFile(logo, "stores/logos");
                request.setLogoUrl(logoUrl);
            }

            // Upload stamp if provided
            if (stamp != null && !stamp.isEmpty()) {
                String stampUrl = fileStorageService.uploadFile(stamp, "stores/stamps");
                request.setStampImageUrl(stampUrl);
            }

            StoreResponse response = storeService.createStore(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Store created successfully", response));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to create store: " + e.getMessage()));
        }
    }

    @PutMapping(value = "/{id}/with-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('STORE_UPDATE')")
    public ResponseEntity<ApiResponse<StoreResponse>> updateStoreWithFiles(
            @PathVariable UUID id,
            @RequestPart("store") String storeJson,
            @RequestPart(value = "logo", required = false) MultipartFile logo,
            @RequestPart(value = "stamp", required = false) MultipartFile stamp) {
        try {
            UpdateStoreRequest request = objectMapper.readValue(storeJson, UpdateStoreRequest.class);

            // Get current store to delete old files if needed
            StoreResponse currentStore = storeService.getStoreById(id);

            // Upload new logo if provided
            if (logo != null && !logo.isEmpty()) {
                // Delete old logo if exists
                if (currentStore.getLogoUrl() != null) {
                    try {
                        fileStorageService.deleteFile(currentStore.getLogoUrl());
                    } catch (Exception e) {
                        // Log but don't fail if delete fails
                    }
                }
                String logoUrl = fileStorageService.uploadFile(logo, "stores/logos");
                request.setLogoUrl(logoUrl);
            }

            // Upload new stamp if provided
            if (stamp != null && !stamp.isEmpty()) {
                // Delete old stamp if exists
                if (currentStore.getStampImageUrl() != null) {
                    try {
                        fileStorageService.deleteFile(currentStore.getStampImageUrl());
                    } catch (Exception e) {
                        // Log but don't fail if delete fails
                    }
                }
                String stampUrl = fileStorageService.uploadFile(stamp, "stores/stamps");
                request.setStampImageUrl(stampUrl);
            }

            StoreResponse response = storeService.updateStore(id, request);
            return ResponseEntity.ok(ApiResponse.success("Store updated successfully", response));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to update store: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('STORE_DELETE')")
    public ResponseEntity<ApiResponse<Void>> deleteStore(@PathVariable UUID id) {
        storeService.deleteStore(id);
        return ResponseEntity.ok(ApiResponse.success("Store deactivated successfully", null));
    }

    @GetMapping("/{id}/users")
    @PreAuthorize("hasAuthority('STORE_UPDATE')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getStoreUsers(@PathVariable UUID id) {
        List<UserResponse> users = storeService.getStoreUsers(id);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PostMapping("/{id}/users/{userId}")
    @PreAuthorize("hasAuthority('STORE_UPDATE')")
    public ResponseEntity<ApiResponse<Void>> assignUserToStore(
            @PathVariable UUID id, @PathVariable UUID userId) {
        storeService.assignUserToStore(id, userId);
        return ResponseEntity.ok(ApiResponse.success("User assigned to store successfully", null));
    }

    @DeleteMapping("/{id}/users/{userId}")
    @PreAuthorize("hasAuthority('STORE_UPDATE')")
    public ResponseEntity<ApiResponse<Void>> unassignUserFromStore(
            @PathVariable UUID id, @PathVariable UUID userId) {
        storeService.unassignUserFromStore(id, userId);
        return ResponseEntity.ok(ApiResponse.success("User unassigned from store successfully", null));
    }

    @GetMapping("/{id}/logo")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ImageResponse>> getStoreLogo(@PathVariable UUID id) {
        StoreResponse store = storeService.getStoreById(id);
        if (store.getLogoUrl() == null || store.getLogoUrl().isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success(null));
        }

        String base64 = fileStorageService.getFileAsBase64(store.getLogoUrl());
        ImageResponse response = ImageResponse.builder()
                .url(store.getLogoUrl())
                .base64(base64)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}/stamp")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ImageResponse>> getStoreStamp(@PathVariable UUID id) {
        StoreResponse store = storeService.getStoreById(id);
        if (store.getStampImageUrl() == null || store.getStampImageUrl().isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success(null));
        }

        String base64 = fileStorageService.getFileAsBase64(store.getStampImageUrl());
        ImageResponse response = ImageResponse.builder()
                .url(store.getStampImageUrl())
                .base64(base64)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
