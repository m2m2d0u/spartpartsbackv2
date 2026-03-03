package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.request.AssignPermissionsToRoleRequest;
import sn.symmetry.spareparts.dto.request.CreateRoleRequest;
import sn.symmetry.spareparts.dto.request.UpdateRoleRequest;
import sn.symmetry.spareparts.dto.response.RoleResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for role management.
 */
public interface RoleService {

    /**
     * Get all roles.
     *
     * @param pageable pagination parameters
     * @return paged list of roles
     */
    PagedResponse<RoleResponse> getAllRoles(Pageable pageable);

    /**
     * Get all active roles.
     *
     * @return list of active roles
     */
    List<RoleResponse> getAllActiveRoles();

    /**
     * Get role by ID.
     *
     * @param id the role ID
     * @return role response
     */
    RoleResponse getRoleById(UUID id);

    /**
     * Get role by code.
     *
     * @param code the role code
     * @return role response
     */
    RoleResponse getRoleByCode(String code);

    /**
     * Create a new role.
     *
     * @param request the create role request
     * @return created role
     */
    RoleResponse createRole(CreateRoleRequest request);

    /**
     * Update an existing role.
     *
     * @param id      the role ID
     * @param request the update role request
     * @return updated role
     */
    RoleResponse updateRole(UUID id, UpdateRoleRequest request);

    /**
     * Delete a role.
     *
     * @param id the role ID
     */
    void deleteRole(UUID id);

    /**
     * Assign permissions to a role.
     *
     * @param roleId  the role ID
     * @param request the assign permissions request
     * @return updated role with permissions
     */
    RoleResponse assignPermissionsToRole(UUID roleId, AssignPermissionsToRoleRequest request);

    /**
     * Get system roles only.
     *
     * @return list of system roles
     */
    List<RoleResponse> getSystemRoles();

    /**
     * Get custom (non-system) roles only.
     *
     * @return list of custom roles
     */
    List<RoleResponse> getCustomRoles();
}
