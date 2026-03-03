package sn.symmetry.spareparts.service;

import sn.symmetry.spareparts.dto.response.PermissionInfo;
import sn.symmetry.spareparts.dto.response.PermissionsResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for permission management.
 */
public interface PermissionService {

    /**
     * Get all permissions grouped by category.
     *
     * @return permissions response with categories
     */
    PermissionsResponse getAllPermissions();

    /**
     * Get all active permissions.
     *
     * @param pageable pagination parameters
     * @return paged list of permissions
     */
    PagedResponse<PermissionInfo> getAllPermissions(Pageable pageable);

    /**
     * Get permission by ID.
     *
     * @param id the permission ID
     * @return permission info
     */
    PermissionInfo getPermissionById(UUID id);

    /**
     * Get permissions by category.
     *
     * @param category the permission category
     * @return list of permissions
     */
    List<PermissionInfo> getPermissionsByCategory(String category);

    /**
     * Get permissions by level.
     *
     * @param level the permission level
     * @return list of permissions
     */
    List<PermissionInfo> getPermissionsByLevel(String level);

    /**
     * Get all permission categories.
     *
     * @return list of category codes
     */
    List<String> getAllCategories();

    /**
     * Get all permission levels.
     *
     * @return list of level codes
     */
    List<String> getAllLevels();
}
