package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.symmetry.spareparts.config.CacheConfig;
import sn.symmetry.spareparts.dto.response.PermissionInfo;
import sn.symmetry.spareparts.dto.response.PermissionsResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.entity.Permission;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.repository.PermissionRepository;
import sn.symmetry.spareparts.service.PermissionService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    @Cacheable(value = CacheConfig.PERMISSIONS_ALL_CACHE, key = "'grouped'")
    public PermissionsResponse getAllPermissions() {
        List<Permission> permissions = permissionRepository.findAllActiveOrderByCategoryAndDisplayName();

        // Group by category
        Map<String, List<PermissionInfo>> groupedByCategory = permissions.stream()
                .map(this::toPermissionInfo)
                .collect(Collectors.groupingBy(PermissionInfo::getCategory));

        // Create category groups
        List<PermissionsResponse.PermissionCategoryGroup> categories = groupedByCategory.entrySet().stream()
                .map(entry -> PermissionsResponse.PermissionCategoryGroup.builder()
                        .code(entry.getKey())
                        .displayName(formatCategoryName(entry.getKey()))
                        .description(getCategoryDescription(entry.getKey()))
                        .permissions(entry.getValue())
                        .count(entry.getValue().size())
                        .build())
                .sorted(Comparator.comparing(PermissionsResponse.PermissionCategoryGroup::getCode))
                .collect(Collectors.toList());

        List<PermissionInfo> allPermissions = permissions.stream()
                .map(this::toPermissionInfo)
                .collect(Collectors.toList());

        return PermissionsResponse.builder()
                .categories(categories)
                .allPermissions(allPermissions)
                .totalPermissions(permissions.size())
                .activePermissions(permissions.size())
                .legacyPermissions(0)
                .build();
    }

    @Override
    public PagedResponse<PermissionInfo> getAllPermissions(Pageable pageable) {
        Page<Permission> page = permissionRepository.findByIsActive(true, pageable);
        return PagedResponse.of(page.map(this::toPermissionInfo));
    }

    @Override
    @Cacheable(value = CacheConfig.PERMISSIONS_CACHE, key = "#id")
    public PermissionInfo getPermissionById(UUID id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", "id", id));
        return toPermissionInfo(permission);
    }

    @Override
    @Cacheable(value = CacheConfig.PERMISSIONS_BY_CATEGORY_CACHE, key = "#category")
    public List<PermissionInfo> getPermissionsByCategory(String category) {
        return permissionRepository.findActiveByCategoryOrderByDisplayName(category).stream()
                .map(this::toPermissionInfo)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = CacheConfig.PERMISSIONS_BY_LEVEL_CACHE, key = "#level")
    public List<PermissionInfo> getPermissionsByLevel(String level) {
        return permissionRepository.findActiveByLevelOrderByDisplayName(level).stream()
                .map(this::toPermissionInfo)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = CacheConfig.PERMISSIONS_ALL_CACHE, key = "'categories'")
    public List<String> getAllCategories() {
        return new ArrayList<>(permissionRepository.findAllActiveCategories());
    }

    @Override
    @Cacheable(value = CacheConfig.PERMISSIONS_ALL_CACHE, key = "'levels'")
    public List<String> getAllLevels() {
        return new ArrayList<>(permissionRepository.findAllActiveLevels());
    }

    private PermissionInfo toPermissionInfo(Permission permission) {
        return PermissionInfo.builder()
                .id(permission.getId())
                .code(permission.getCode())
                .displayName(permission.getDisplayName())
                .description(permission.getDescription())
                .category(permission.getCategory())
                .level(permission.getLevel())
                .isActive(permission.getIsActive())
                .isLegacy(false)
                .isReadOnly("READ".equals(permission.getLevel()))
                .build();
    }

    private String formatCategoryName(String category) {
        // Simple formatting: STOCK -> Stock Management
        return switch (category.toUpperCase()) {
            case "STOCK" -> "Stock Management";
            case "ORDER" -> "Order Management";
            case "INVOICE" -> "Invoice Management";
            case "PROCUREMENT" -> "Procurement";
            case "TRANSFER" -> "Transfer Management";
            case "RETURN" -> "Return Management";
            case "REPORT" -> "Reports & Analytics";
            case "CUSTOMER" -> "Customer Management";
            case "PART" -> "Parts Management";
            case "SETTINGS" -> "Warehouse Settings";
            default -> category;
        };
    }

    private String getCategoryDescription(String category) {
        return switch (category.toUpperCase()) {
            case "STOCK" -> "Permissions related to inventory and stock management";
            case "ORDER" -> "Permissions related to customer orders";
            case "INVOICE" -> "Permissions related to invoicing and billing";
            case "PROCUREMENT" -> "Permissions related to purchasing and receiving goods";
            case "TRANSFER" -> "Permissions related to stock transfers between warehouses";
            case "RETURN" -> "Permissions related to product returns and refunds";
            case "REPORT" -> "Permissions related to viewing and exporting reports";
            case "CUSTOMER" -> "Permissions related to customer data";
            case "PART" -> "Permissions related to parts catalog and pricing";
            case "SETTINGS" -> "Permissions related to warehouse configuration";
            default -> "";
        };
    }
}
