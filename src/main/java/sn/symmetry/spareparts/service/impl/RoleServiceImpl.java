package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.symmetry.spareparts.dto.request.AssignPermissionsToRoleRequest;
import sn.symmetry.spareparts.dto.request.CreateRoleRequest;
import sn.symmetry.spareparts.dto.request.UpdateRoleRequest;
import sn.symmetry.spareparts.dto.response.PermissionInfo;
import sn.symmetry.spareparts.dto.response.RoleResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.entity.Permission;
import sn.symmetry.spareparts.entity.Role;
import sn.symmetry.spareparts.entity.RolePermission;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.config.CacheConfig;
import sn.symmetry.spareparts.repository.PermissionRepository;
import sn.symmetry.spareparts.repository.RolePermissionRepository;
import sn.symmetry.spareparts.repository.RoleRepository;
import sn.symmetry.spareparts.service.RoleService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public PagedResponse<RoleResponse> getAllRoles(Pageable pageable) {
        Page<Role> page = roleRepository.findAll(pageable);
        return PagedResponse.of(page.map(this::toRoleResponse));
    }

    @Override
    @Cacheable(value = CacheConfig.ROLES_ALL_CACHE, key = "'active'")
    public List<RoleResponse> getAllActiveRoles() {
        return roleRepository.findAllActiveOrderByDisplayName().stream()
                .map(this::toRoleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = CacheConfig.ROLES_CACHE, key = "#id")
    public RoleResponse getRoleById(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
        return toRoleResponseWithPermissions(role);
    }

    @Override
    @Cacheable(value = CacheConfig.ROLES_CACHE, key = "'code:' + #code")
    public RoleResponse getRoleByCode(String code) {
        Role role = roleRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "code", code));
        return toRoleResponseWithPermissions(role);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.ROLES_ALL_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.ROLES_CUSTOM_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.USER_ME_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.USER_WAREHOUSE_PERMISSIONS_CACHE, allEntries = true)
    })
    public RoleResponse createRole(CreateRoleRequest request) {
        // Check if role code already exists
        if (roleRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Role with code '" + request.getCode() + "' already exists");
        }

        Role role = new Role();
        role.setCode(request.getCode());
        role.setDisplayName(request.getDisplayName());
        role.setDescription(request.getDescription());
        role.setIsSystem(false);
        role.setIsActive(true);

        Role saved = roleRepository.save(role);

        // Assign permissions if provided
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            assignPermissions(saved, request.getPermissionIds());
        }

        return toRoleResponseWithPermissions(saved);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.ROLES_CACHE, key = "#id"),
            @CacheEvict(value = CacheConfig.ROLES_ALL_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.ROLES_CUSTOM_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.USER_ME_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.USER_WAREHOUSE_PERMISSIONS_CACHE, allEntries = true)
    })
    public RoleResponse updateRole(UUID id, UpdateRoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));

        // Prevent modification of system roles' basic info
        if (role.getIsSystem()) {
            throw new AccessDeniedException("Cannot modify system role properties");
        }

        role.setDisplayName(request.getDisplayName());
        role.setDescription(request.getDescription());

        if (request.getIsActive() != null) {
            role.setIsActive(request.getIsActive());
        }

        Role saved = roleRepository.save(role);
        return toRoleResponseWithPermissions(saved);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.ROLES_CACHE, key = "#id"),
            @CacheEvict(value = CacheConfig.ROLES_ALL_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.ROLES_CUSTOM_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.USER_ME_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.USER_WAREHOUSE_PERMISSIONS_CACHE, allEntries = true)
    })
    public void deleteRole(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));

        // Prevent deletion of system roles
        if (role.getIsSystem()) {
            throw new AccessDeniedException("Cannot delete system role");
        }

        roleRepository.delete(role);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.ROLES_CACHE, key = "#roleId"),
            @CacheEvict(value = CacheConfig.ROLES_ALL_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.ROLES_SYSTEM_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.ROLES_CUSTOM_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.USER_ME_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.USER_WAREHOUSE_PERMISSIONS_CACHE, allEntries = true)
    })
    public RoleResponse assignPermissionsToRole(UUID roleId, AssignPermissionsToRoleRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));

        assignPermissions(role, request.getPermissionIds());

        return toRoleResponseWithPermissions(role);
    }

    @Override
    @Cacheable(value = CacheConfig.ROLES_SYSTEM_CACHE, key = "'all'")
    public List<RoleResponse> getSystemRoles() {
        return roleRepository.findByIsSystemAndIsActiveOrderByDisplayName(true).stream()
                .map(this::toRoleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = CacheConfig.ROLES_CUSTOM_CACHE, key = "'all'")
    public List<RoleResponse> getCustomRoles() {
        return roleRepository.findByIsSystemAndIsActiveOrderByDisplayName(false).stream()
                .map(this::toRoleResponse)
                .collect(Collectors.toList());
    }

    private void assignPermissions(Role role, List<UUID> permissionIds) {
        // Delete existing permissions
        rolePermissionRepository.deleteByRoleId(role.getId());

        // Create new permission assignments
        List<RolePermission> rolePermissions = permissionIds.stream()
                .map(permissionId -> {
                    Permission permission = permissionRepository.findById(permissionId)
                            .orElseThrow(() -> new ResourceNotFoundException("Permission", "id", permissionId));

                    RolePermission rp = new RolePermission();
                    rp.setRole(role);
                    rp.setPermission(permission);
                    return rp;
                })
                .collect(Collectors.toList());

        rolePermissionRepository.saveAll(rolePermissions);
    }

    private RoleResponse toRoleResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .code(role.getCode())
                .displayName(role.getDisplayName())
                .description(role.getDescription())
                .isSystem(role.getIsSystem())
                .isActive(role.getIsActive())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .permissionCount(role.getRolePermissions().size())
                .build();
    }

    private RoleResponse toRoleResponseWithPermissions(Role role) {
        List<PermissionInfo> permissions = rolePermissionRepository.findPermissionsByRoleId(role.getId()).stream()
                .map(this::toPermissionInfo)
                .collect(Collectors.toList());

        return RoleResponse.builder()
                .id(role.getId())
                .code(role.getCode())
                .displayName(role.getDisplayName())
                .description(role.getDescription())
                .isSystem(role.getIsSystem())
                .isActive(role.getIsActive())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .permissions(permissions)
                .permissionCount(permissions.size())
                .build();
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
}
