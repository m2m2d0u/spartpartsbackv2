package sn.symmetry.spareparts.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sn.symmetry.spareparts.config.CacheConfig;
import sn.symmetry.spareparts.entity.User;
import sn.symmetry.spareparts.enums.WarehousePermission;
import sn.symmetry.spareparts.exception.UnauthorizedException;
import sn.symmetry.spareparts.repository.UserRepository;
import sn.symmetry.spareparts.repository.UserStoreRepository;
import sn.symmetry.spareparts.repository.UserWarehouseRepository;
import sn.symmetry.spareparts.repository.UserWarehouseRoleRepository;
import sn.symmetry.spareparts.repository.WarehouseRepository;

import java.util.List;
import java.util.UUID;

/**
 * Centralized service for handling authorization logic.
 * Provides methods to check user access to stores and warehouses,
 * and to verify granular warehouse permissions.
 */
@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final UserRepository userRepository;
    private final UserStoreRepository userStoreRepository;
    private final UserWarehouseRepository userWarehouseRepository;
    private final UserWarehouseRoleRepository userWarehouseRoleRepository;
    private final WarehouseRepository warehouseRepository;

    /**
     * Get the currently authenticated user.
     *
     * @return the current user
     * @throws UnauthorizedException if user is not authenticated or not found
     */
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new UnauthorizedException("No authenticated user found");
        }

        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found: " + email));
    }

    /**
     * Get the current user's role code.
     *
     * @return the user's role code
     */
    public String getCurrentUserRoleCode() {
        return getCurrentUser().getRole().getCode();
    }

    /**
     * Check if the current user is an ADMIN.
     *
     * @return true if user is ADMIN, false otherwise
     */
    public boolean isAdmin() {
        return "ADMINISTRATEUR".equals(getCurrentUserRoleCode());
    }

    /**
     * Check if the current user is a STORE_MANAGER.
     *
     * @return true if user is STORE_MANAGER, false otherwise
     */
    public boolean isStoreManager() {
        return "RESPONSABLE_MAGASIN".equals(getCurrentUserRoleCode());
    }

    /**
     * Check if the current user is a WAREHOUSE_OPERATOR.
     *
     * @return true if user is WAREHOUSE_OPERATOR, false otherwise
     */
    public boolean isWarehouseOperator() {
        return "OPERATEUR_ENTREPOT".equals(getCurrentUserRoleCode());
    }

    /**
     * Get list of store IDs accessible by the current user.
     * Returns null for ADMIN (all stores accessible).
     * Returns list of assigned stores for STORE_MANAGER.
     * Returns empty list for WAREHOUSE_OPERATOR (no store access).
     *
     * @return list of accessible store IDs, or null if all stores are accessible
     */
    public List<UUID> getAccessibleStoreIds() {
        User user = getCurrentUser();
        String roleCode = user.getRole().getCode();

        if ("ADMINISTRATEUR".equals(roleCode)) {
            return null; // null means all stores
        }

        if ("RESPONSABLE_MAGASIN".equals(roleCode)) {
            return userStoreRepository.findStoreIdsByUserId(user.getId());
        }

        return List.of(); // WAREHOUSE_OPERATOR has no store access
    }

    /**
     * Get list of warehouse IDs accessible by the current user.
     * Returns null for ADMIN (all warehouses accessible).
     * Returns warehouses in assigned stores for STORE_MANAGER.
     * Returns assigned warehouses for WAREHOUSE_OPERATOR.
     *
     * @return list of accessible warehouse IDs, or null if all warehouses are accessible
     */
    public List<UUID> getAccessibleWarehouseIds() {
        User user = getCurrentUser();
        String roleCode = user.getRole().getCode();

        if ("ADMINISTRATEUR".equals(roleCode)) {
            return null; // null means all warehouses
        }

        if ("RESPONSABLE_MAGASIN".equals(roleCode)) {
            List<UUID> storeIds = userStoreRepository.findStoreIdsByUserId(user.getId());
            if (storeIds.isEmpty()) {
                return List.of();
            }
            return warehouseRepository.findWarehouseIdsByStoreIds(storeIds);
        }

        if ("OPERATEUR_ENTREPOT".equals(roleCode)) {
            return userWarehouseRepository.findWarehouseIdsByUserId(user.getId());
        }

        return List.of();
    }

    /**
     * Check if the current user can access a specific warehouse.
     *
     * @param warehouseId the warehouse ID to check
     * @return true if user has access, false otherwise
     */
    public boolean canAccessWarehouse(UUID warehouseId) {
        if (warehouseId == null) {
            return false;
        }

        List<UUID> accessible = getAccessibleWarehouseIds();
        return accessible == null || accessible.contains(warehouseId);
    }

    /**
     * Check if the current user can access a specific store.
     *
     * @param storeId the store ID to check
     * @return true if user has access, false otherwise
     */
    public boolean canAccessStore(UUID storeId) {
        if (storeId == null) {
            return false;
        }

        List<UUID> accessible = getAccessibleStoreIds();
        return accessible == null || accessible.contains(storeId);
    }

    /**
     * Check if the current user has a specific permission for a warehouse.
     * ADMIN has all permissions.
     * STORE_MANAGER has all permissions for warehouses in their stores.
     * WAREHOUSE_OPERATOR needs explicit permission (via roles or individual assignments).
     *
     * @param warehouseId    the warehouse ID
     * @param permissionCode the permission code to check
     * @return true if user has the permission, false otherwise
     */
    @Cacheable(value = CacheConfig.USER_WAREHOUSE_PERMISSIONS_CACHE,
            key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName() + ':' + #warehouseId + ':' + #permissionCode",
            unless = "#result == false")
    public boolean hasWarehousePermission(UUID warehouseId, String permissionCode) {
        if (warehouseId == null || permissionCode == null) {
            return false;
        }

        User user = getCurrentUser();
        String roleCode = user.getRole().getCode();

        // ADMIN has all permissions
        if ("ADMINISTRATEUR".equals(roleCode)) {
            return true;
        }

        // STORE_MANAGER has all permissions for warehouses in their stores
        if ("RESPONSABLE_MAGASIN".equals(roleCode)) {
            return canAccessWarehouse(warehouseId);
        }

        // WAREHOUSE_OPERATOR needs specific permission
        if ("OPERATEUR_ENTREPOT".equals(roleCode)) {
            // Check permissions from roles
            List<String> rolePermissions = userWarehouseRoleRepository.findPermissionCodesByUserAndWarehouse(user.getId(), warehouseId);
            if (rolePermissions.contains(permissionCode)) {
                return true;
            }

            // Check individual permission overrides (existing VARCHAR-based permissions)
            // This supports backward compatibility with existing user_warehouse_permission records
            return userWarehouseRepository.hasPermission(user.getId(), warehouseId, WarehousePermission.valueOf(permissionCode));
        }

        return false;
    }

    /**
     * Require that the current user can access the specified warehouse.
     * Throws AccessDeniedException if access is denied.
     *
     * @param warehouseId the warehouse ID
     * @throws AccessDeniedException if user cannot access the warehouse
     */
    public void requireWarehouseAccess(UUID warehouseId) {
        if (!canAccessWarehouse(warehouseId)) {
            throw new AccessDeniedException("Access denied to warehouse: " + warehouseId);
        }
    }

    /**
     * Require that the current user has a specific permission for a warehouse.
     * Throws AccessDeniedException if permission is not granted.
     *
     * @param warehouseId    the warehouse ID
     * @param permissionCode the required permission code
     * @throws AccessDeniedException if user lacks the permission
     */
    public void requireWarehousePermission(UUID warehouseId, String permissionCode) {
        if (!hasWarehousePermission(warehouseId, permissionCode)) {
            throw new AccessDeniedException(
                    "Missing permission " + permissionCode + " for warehouse: " + warehouseId
            );
        }
    }

    /**
     * Require that the current user can access the specified store.
     * Throws AccessDeniedException if access is denied.
     *
     * @param storeId the store ID
     * @throws AccessDeniedException if user cannot access the store
     */
    public void requireStoreAccess(UUID storeId) {
        if (!canAccessStore(storeId)) {
            throw new AccessDeniedException("Access denied to store: " + storeId);
        }
    }

    /**
     * Require that the current user is an ADMIN.
     * Throws AccessDeniedException if user is not ADMIN.
     *
     * @throws AccessDeniedException if user is not ADMIN
     */
    public void requireAdmin() {
        if (!isAdmin()) {
            throw new AccessDeniedException("This operation requires ADMIN role");
        }
    }

    /**
     * Require that the current user is either ADMIN or STORE_MANAGER.
     * Throws AccessDeniedException if user is neither.
     *
     * @throws AccessDeniedException if user is neither ADMIN nor STORE_MANAGER
     */
    public void requireAdminOrStoreManager() {
        if (!isAdmin() && !isStoreManager()) {
            throw new AccessDeniedException("This operation requires ADMIN or STORE_MANAGER role");
        }
    }
}
