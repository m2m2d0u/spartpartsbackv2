package sn.symmetry.spareparts.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.symmetry.spareparts.dto.request.CreateStoreRequest;
import sn.symmetry.spareparts.dto.request.UpdateStoreRequest;
import sn.symmetry.spareparts.dto.response.StoreResponse;
import sn.symmetry.spareparts.dto.response.UserResponse;
import sn.symmetry.spareparts.dto.response.common.ApiResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.service.StoreService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PagedResponse<StoreResponse>>> getAllStores(
            @RequestParam(required = false) Boolean isActive,
            @PageableDefault(size = 20) Pageable pageable) {
        PagedResponse<StoreResponse> response = storeService.getAllStores(isActive, pageable);
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
}
