package sn.symmetry.spareparts.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sn.symmetry.spareparts.dto.response.PermissionInfo;
import sn.symmetry.spareparts.dto.response.PermissionsResponse;
import sn.symmetry.spareparts.dto.response.common.ApiResponse;
import sn.symmetry.spareparts.enums.WarehousePermission;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for warehouse permission metadata.
 * Provides endpoints for frontend to discover available permissions and their details.
 */
@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    /**
     * Get all warehouse permissions grouped by category.
     * This endpoint is useful for building permission selection UIs.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<ApiResponse<PermissionsResponse>> getAllPermissions() {

        List<WarehousePermission> permissions = WarehousePermission.getActivePermissions();

        // Group permissions by category
        List<PermissionsResponse.PermissionCategoryGroup> categories = Arrays.stream(WarehousePermission.PermissionCategory.values())
                .map(category -> {
                    List<PermissionInfo> categoryPermissions = WarehousePermission.getByCategory(category).stream()
                            .map(PermissionInfo::fromEnum)
                            .sorted(Comparator.comparing(PermissionInfo::getDisplayName))
                            .collect(Collectors.toList());

                    return PermissionsResponse.PermissionCategoryGroup.builder()
                            .code(category.name())
                            .displayName(category.getDisplayName())
                            .description(category.getDescription())
                            .permissions(categoryPermissions)
                            .count(categoryPermissions.size())
                            .build();
                })
                .filter(group -> group.getCount() > 0)
                .collect(Collectors.toList());

        // Convert all permissions to DTOs
        List<PermissionInfo> allPermissions = permissions.stream()
                .map(PermissionInfo::fromEnum)
                .sorted(Comparator.comparing(PermissionInfo::getDisplayName))
                .collect(Collectors.toList());

        int totalPermissions = WarehousePermission.values().length;

        PermissionsResponse response = PermissionsResponse.builder()
                .categories(categories)
                .allPermissions(allPermissions)
                .totalPermissions(totalPermissions)
                .activePermissions(totalPermissions)
                .legacyPermissions(0)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get permissions for a specific category.
     */
    @GetMapping("/by-category")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<ApiResponse<List<PermissionInfo>>> getPermissionsByCategory(
            @RequestParam String category) {

        try {
            WarehousePermission.PermissionCategory permCategory =
                    WarehousePermission.PermissionCategory.valueOf(category.toUpperCase());

            List<PermissionInfo> permissions = WarehousePermission.getByCategory(permCategory).stream()
                    .map(PermissionInfo::fromEnum)
                    .sorted(Comparator.comparing(PermissionInfo::getDisplayName))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(permissions));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid category: " + category));
        }
    }

    /**
     * Get all read-only permissions.
     */
    @GetMapping("/read-only")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<ApiResponse<List<PermissionInfo>>> getReadOnlyPermissions() {
        List<PermissionInfo> permissions = WarehousePermission.getReadPermissions().stream()
                .map(PermissionInfo::fromEnum)
                .sorted(Comparator.comparing(PermissionInfo::getDisplayName))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(permissions));
    }

    /**
     * Get all write permissions.
     */
    @GetMapping("/write")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<ApiResponse<List<PermissionInfo>>> getWritePermissions() {
        List<PermissionInfo> permissions = WarehousePermission.getWritePermissions().stream()
                .map(PermissionInfo::fromEnum)
                .sorted(Comparator.comparing(PermissionInfo::getDisplayName))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(permissions));
    }

    /**
     * Get permission categories with their metadata.
     */
    @GetMapping("/categories")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<ApiResponse<List<CategoryInfo>>> getCategories() {

        List<CategoryInfo> categories = Arrays.stream(WarehousePermission.PermissionCategory.values())
                .map(category -> CategoryInfo.builder()
                        .code(category.name())
                        .displayName(category.getDisplayName())
                        .description(category.getDescription())
                        .permissionCount(WarehousePermission.getByCategory(category).size())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    /**
     * DTO for category information.
     */
    @lombok.Getter
    @lombok.Setter
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class CategoryInfo {
        private String code;
        private String displayName;
        private String description;
        private int permissionCount;
    }
}
