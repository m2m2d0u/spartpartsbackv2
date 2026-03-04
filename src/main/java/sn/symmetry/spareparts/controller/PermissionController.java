package sn.symmetry.spareparts.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.symmetry.spareparts.dto.response.PermissionInfo;
import sn.symmetry.spareparts.dto.response.PermissionsResponse;
import sn.symmetry.spareparts.dto.response.common.ApiResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.service.PermissionService;

import java.util.List;
import java.util.UUID;

/**
 * Controller for warehouse permission metadata.
 * Provides endpoints for frontend to discover available permissions and their details.
 * Now reads from database instead of enum.
 */
@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * Get all warehouse permissions grouped by category.
     * This endpoint is useful for building permission selection UIs.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    public ResponseEntity<ApiResponse<PermissionsResponse>> getAllPermissions() {
        return ResponseEntity.ok(ApiResponse.success(permissionService.getAllPermissions()));
    }

    /**
     * Get all permissions with pagination.
     */
    @GetMapping("/paged")
    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    public ResponseEntity<ApiResponse<PagedResponse<PermissionInfo>>> getAllPermissionsPaged(
            @PageableDefault(size = 20, sort = "code", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(permissionService.getAllPermissions(pageable)));
    }

    /**
     * Get permission by ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    public ResponseEntity<ApiResponse<PermissionInfo>> getPermissionById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(permissionService.getPermissionById(id)));
    }

    /**
     * Get permissions for a specific category.
     */
    @GetMapping("/by-category")
    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    public ResponseEntity<ApiResponse<List<PermissionInfo>>> getPermissionsByCategory(
            @RequestParam String category) {
        List<PermissionInfo> permissions = permissionService.getPermissionsByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(permissions));
    }

    /**
     * Get permissions by level.
     */
    @GetMapping("/by-level")
    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    public ResponseEntity<ApiResponse<List<PermissionInfo>>> getPermissionsByLevel(
            @RequestParam String level) {
        List<PermissionInfo> permissions = permissionService.getPermissionsByLevel(level);
        return ResponseEntity.ok(ApiResponse.success(permissions));
    }

    /**
     * Get all read-only permissions.
     */
    @GetMapping("/read-only")
    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    public ResponseEntity<ApiResponse<List<PermissionInfo>>> getReadOnlyPermissions() {
        return ResponseEntity.ok(ApiResponse.success(permissionService.getPermissionsByLevel("READ")));
    }

    /**
     * Get all write permissions (WRITE, DELETE, APPROVE).
     */
    @GetMapping("/write")
    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    public ResponseEntity<ApiResponse<List<PermissionInfo>>> getWritePermissions() {
        List<PermissionInfo> writePerms = permissionService.getPermissionsByLevel("WRITE");
        List<PermissionInfo> deletePerms = permissionService.getPermissionsByLevel("DELETE");
        List<PermissionInfo> approvePerms = permissionService.getPermissionsByLevel("APPROVE");

        writePerms.addAll(deletePerms);
        writePerms.addAll(approvePerms);

        return ResponseEntity.ok(ApiResponse.success(writePerms));
    }

    /**
     * Get all permission categories.
     */
    @GetMapping("/categories")
    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    public ResponseEntity<ApiResponse<List<String>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(permissionService.getAllCategories()));
    }

    /**
     * Get all permission levels.
     */
    @GetMapping("/levels")
    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    public ResponseEntity<ApiResponse<List<String>>> getLevels() {
        return ResponseEntity.ok(ApiResponse.success(permissionService.getAllLevels()));
    }
}

