package sn.symmetry.spareparts.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.symmetry.spareparts.dto.request.AssignPermissionsToRoleRequest;
import sn.symmetry.spareparts.dto.request.CreateRoleRequest;
import sn.symmetry.spareparts.dto.request.UpdateRoleRequest;
import sn.symmetry.spareparts.dto.response.RoleResponse;
import sn.symmetry.spareparts.dto.response.common.ApiResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.service.RoleService;

import java.util.List;
import java.util.UUID;

/**
 * Controller for managing roles and their permissions.
 */
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * Get all roles with pagination.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<ApiResponse<PagedResponse<RoleResponse>>> getAllRoles(
            @PageableDefault(size = 20, sort = "displayName", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(roleService.getAllRoles(pageable)));
    }

    /**
     * Get all active roles (for dropdown selection).
     */
    @GetMapping("/active")
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllActiveRoles() {
        return ResponseEntity.ok(ApiResponse.success(roleService.getAllActiveRoles()));
    }

    /**
     * Get system roles only.
     */
    @GetMapping("/system")
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getSystemRoles() {
        return ResponseEntity.ok(ApiResponse.success(roleService.getSystemRoles()));
    }

    /**
     * Get custom roles only.
     */
    @GetMapping("/custom")
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getCustomRoles() {
        return ResponseEntity.ok(ApiResponse.success(roleService.getCustomRoles()));
    }

    /**
     * Get role by ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(roleService.getRoleById(id)));
    }

    /**
     * Get role by code.
     */
    @GetMapping("/code/{code}")
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleByCode(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.success(roleService.getRoleByCode(code)));
    }

    /**
     * Create a new role.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(@Valid @RequestBody CreateRoleRequest request) {
        RoleResponse response = roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Role created successfully", response));
    }

    /**
     * Update an existing role.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRoleRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Role updated successfully",
                roleService.updateRole(id, request)));
    }

    /**
     * Delete a role.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    public ResponseEntity<ApiResponse<String>> deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.success("Role deleted successfully"));
    }

    /**
     * Assign permissions to a role.
     */
    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    public ResponseEntity<ApiResponse<RoleResponse>> assignPermissionsToRole(
            @PathVariable UUID id,
            @Valid @RequestBody AssignPermissionsToRoleRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Permissions assigned successfully",
                roleService.assignPermissionsToRole(id, request)));
    }
}
