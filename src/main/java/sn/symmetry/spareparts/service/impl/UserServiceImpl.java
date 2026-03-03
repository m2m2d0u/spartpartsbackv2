package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.symmetry.spareparts.dto.request.CreateUserRequest;
import sn.symmetry.spareparts.dto.request.UpdateUserRequest;
import sn.symmetry.spareparts.dto.request.UserWarehouseAssignmentRequest;
import org.springframework.security.access.AccessDeniedException;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.dto.response.MeResponse;
import sn.symmetry.spareparts.dto.response.UserResponse;
import sn.symmetry.spareparts.dto.response.UserStoreResponse;
import sn.symmetry.spareparts.dto.response.UserWarehouseAssignmentResponse;
import sn.symmetry.spareparts.entity.Store;
import sn.symmetry.spareparts.entity.User;
import sn.symmetry.spareparts.entity.UserStore;
import sn.symmetry.spareparts.entity.UserWarehouse;
import sn.symmetry.spareparts.entity.UserWarehousePermission;
import sn.symmetry.spareparts.entity.Warehouse;
import sn.symmetry.spareparts.enums.UserRole;
import sn.symmetry.spareparts.enums.WarehousePermission;
import sn.symmetry.spareparts.exception.DuplicateResourceException;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.mapper.UserMapper;
import sn.symmetry.spareparts.repository.StoreRepository;
import sn.symmetry.spareparts.repository.UserRepository;
import sn.symmetry.spareparts.repository.UserStoreRepository;
import sn.symmetry.spareparts.repository.UserWarehouseRepository;
import sn.symmetry.spareparts.repository.WarehouseRepository;
import sn.symmetry.spareparts.service.AuthorizationService;
import sn.symmetry.spareparts.service.UserService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserWarehouseRepository userWarehouseRepository;
    private final UserStoreRepository userStoreRepository;
    private final WarehouseRepository warehouseRepository;
    private final StoreRepository storeRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthorizationService authorizationService;

    @Override
    public PagedResponse<UserResponse> getAllUsers(UserRole role, Boolean isActive, Pageable pageable) {
        Page<User> page;
        if (role != null && isActive != null) {
            page = userRepository.findByRoleAndIsActive(role, isActive, pageable);
        } else if (role != null) {
            page = userRepository.findByRole(role, pageable);
        } else if (isActive != null) {
            page = userRepository.findByIsActive(isActive, pageable);
        } else {
            page = userRepository.findAll(pageable);
        }
        return PagedResponse.of(page.map(userMapper::toResponse));
    }

    @Override
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        UserResponse response = userMapper.toResponse(user);
        response.setStores(buildStoreAssignments(id));
        response.setWarehouseAssignments(buildWarehouseAssignments(id));
        return response;
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        userMapper.updateEntity(request, user);
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserResponse updateUserWarehouses(UUID id, List<UserWarehouseAssignmentRequest> assignments) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // Authorization check: STORE_MANAGER can only assign warehouses from their stores
        User currentUser = authorizationService.getCurrentUser();
        if (currentUser.getRole() == UserRole.STORE_MANAGER) {
            List<UUID> managerWarehouseIds = authorizationService.getAccessibleWarehouseIds();
            List<UUID> requestedWarehouseIds = assignments.stream()
                    .map(UserWarehouseAssignmentRequest::getWarehouseId)
                    .toList();

            if (!new HashSet<>(managerWarehouseIds).containsAll(requestedWarehouseIds)) {
                throw new AccessDeniedException("Cannot assign warehouses outside your stores");
            }
        }

        // Validate all warehouse IDs exist
        for (UserWarehouseAssignmentRequest assignment : assignments) {
            if (!warehouseRepository.existsById(assignment.getWarehouseId())) {
                throw new ResourceNotFoundException("Warehouse", "id", assignment.getWarehouseId());
            }
        }

        // Delete existing assignments (cascade deletes permissions)
        userWarehouseRepository.deleteByUserId(id);
        userWarehouseRepository.flush();

        // Create new assignments
        List<UserWarehouse> newAssignments = new ArrayList<>();
        for (UserWarehouseAssignmentRequest assignment : assignments) {
            Warehouse warehouse = warehouseRepository.getReferenceById(assignment.getWarehouseId());

            UserWarehouse uw = new UserWarehouse();
            uw.setUser(user);
            uw.setWarehouse(warehouse);
            uw.setPermissions(new ArrayList<>());

            for (WarehousePermission perm : assignment.getPermissions()) {
                UserWarehousePermission uwp = new UserWarehousePermission();
                uwp.setUserWarehouse(uw);
                uwp.setPermission(perm);
                uw.getPermissions().add(uwp);
            }

            newAssignments.add(uw);
        }
        userWarehouseRepository.saveAll(newAssignments);

        UserResponse response = userMapper.toResponse(user);
        response.setStores(buildStoreAssignments(id));
        response.setWarehouseAssignments(buildWarehouseAssignments(id));
        return response;
    }

    @Override
    @Transactional
    public UserResponse updateUserStores(UUID id, List<UUID> storeIds) {
        User currentUser = authorizationService.getCurrentUser();
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // Only ADMIN or STORE_MANAGER can assign stores
        if (currentUser.getRole() == UserRole.WAREHOUSE_OPERATOR) {
            throw new AccessDeniedException("WAREHOUSE_OPERATOR cannot assign stores");
        }

        // STORE_MANAGER can only assign their own stores
        if (currentUser.getRole() == UserRole.STORE_MANAGER) {
            List<UUID> managerStoreIds = authorizationService.getAccessibleStoreIds();
            if (!new HashSet<>(managerStoreIds).containsAll(storeIds)) {
                throw new AccessDeniedException("Cannot assign stores you don't manage");
            }
        }

        // Validate all store IDs exist
        for (UUID storeId : storeIds) {
            if (!storeRepository.existsById(storeId)) {
                throw new ResourceNotFoundException("Store", "id", storeId);
            }
        }

        // Delete existing assignments
        userStoreRepository.deleteByUserId(id);
        userStoreRepository.flush();

        // Create new assignments
        List<UserStore> newAssignments = storeIds.stream()
                .map(storeId -> {
                    Store store = storeRepository.getReferenceById(storeId);
                    UserStore us = new UserStore();
                    us.setUser(targetUser);
                    us.setStore(store);
                    return us;
                })
                .toList();

        userStoreRepository.saveAll(newAssignments);

        UserResponse response = userMapper.toResponse(targetUser);
        response.setStores(buildStoreAssignments(id));
        response.setWarehouseAssignments(buildWarehouseAssignments(id));
        return response;
    }

    @Override
    public MeResponse getCurrentUserInfo() {
        User currentUser = authorizationService.getCurrentUser();

        // Build basic user info
        MeResponse response = MeResponse.builder()
                .id(currentUser.getId())
                .name(currentUser.getName())
                .email(currentUser.getEmail())
                .role(currentUser.getRole())
                .isActive(currentUser.getIsActive())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();

        // Get accessible stores
        List<UUID> accessibleStoreIds = authorizationService.getAccessibleStoreIds();
        if (accessibleStoreIds != null && !accessibleStoreIds.isEmpty()) {
            List<Store> stores = storeRepository.findAllById(accessibleStoreIds);
            List<MeResponse.StoreInfo> storeInfos = stores.stream()
                    .map(store -> MeResponse.StoreInfo.builder()
                            .id(store.getId())
                            .code(store.getCode())
                            .name(store.getName())
                            .street(store.getStreet())
                            .city(store.getCity())
                            .state(store.getState())
                            .postalCode(store.getPostalCode())
                            .country(store.getCountry())
                            .phone(store.getPhone())
                            .email(store.getEmail())
                            .isActive(store.getIsActive())
                            .build())
                    .toList();
            response.setAccessibleStores(storeInfos);
        } else if (accessibleStoreIds == null) {
            // ADMIN - get all stores
            List<Store> allStores = storeRepository.findAll();
            List<MeResponse.StoreInfo> storeInfos = allStores.stream()
                    .map(store -> MeResponse.StoreInfo.builder()
                            .id(store.getId())
                            .code(store.getCode())
                            .name(store.getName())
                            .street(store.getStreet())
                            .city(store.getCity())
                            .state(store.getState())
                            .postalCode(store.getPostalCode())
                            .country(store.getCountry())
                            .phone(store.getPhone())
                            .email(store.getEmail())
                            .isActive(store.getIsActive())
                            .build())
                    .toList();
            response.setAccessibleStores(storeInfos);
        }

        // Get accessible warehouses
        List<UUID> accessibleWarehouseIds = authorizationService.getAccessibleWarehouseIds();
        if (accessibleWarehouseIds != null && !accessibleWarehouseIds.isEmpty()) {
            List<Warehouse> warehouses = warehouseRepository.findAllById(accessibleWarehouseIds);
            List<MeResponse.WarehouseInfo> warehouseInfos = warehouses.stream()
                    .map(warehouse -> MeResponse.WarehouseInfo.builder()
                            .id(warehouse.getId())
                            .code(warehouse.getCode())
                            .name(warehouse.getName())
                            .location(warehouse.getLocation())
                            .storeId(warehouse.getStore().getId())
                            .storeName(warehouse.getStore().getName())
                            .isActive(warehouse.getIsActive())
                            .build())
                    .toList();
            response.setAccessibleWarehouses(warehouseInfos);
        } else if (accessibleWarehouseIds == null) {
            // ADMIN - get all warehouses
            List<Warehouse> allWarehouses = warehouseRepository.findAll();
            List<MeResponse.WarehouseInfo> warehouseInfos = allWarehouses.stream()
                    .map(warehouse -> MeResponse.WarehouseInfo.builder()
                            .id(warehouse.getId())
                            .code(warehouse.getCode())
                            .name(warehouse.getName())
                            .location(warehouse.getLocation())
                            .storeId(warehouse.getStore().getId())
                            .storeName(warehouse.getStore().getName())
                            .isActive(warehouse.getIsActive())
                            .build())
                    .toList();
            response.setAccessibleWarehouses(warehouseInfos);
        }

        // Get warehouse assignments with permissions (for WAREHOUSE_OPERATOR)
        response.setWarehouseAssignments(buildWarehouseAssignments(currentUser.getId()));

        return response;
    }

    private List<UserStoreResponse> buildStoreAssignments(UUID userId) {
        List<UserStore> assignments = userStoreRepository.findByUserId(userId);
        return assignments.stream()
                .map(us -> UserStoreResponse.builder()
                        .id(us.getId())
                        .storeId(us.getStore().getId())
                        .storeName(us.getStore().getName())
                        .storeCode(us.getStore().getCode())
                        .createdAt(us.getCreatedAt())
                        .build())
                .toList();
    }

    private List<UserWarehouseAssignmentResponse> buildWarehouseAssignments(UUID userId) {
        List<UserWarehouse> assignments = userWarehouseRepository.findByUserId(userId);
        return assignments.stream()
                .map(uw -> UserWarehouseAssignmentResponse.builder()
                        .warehouseId(uw.getWarehouse().getId())
                        .warehouseName(uw.getWarehouse().getName())
                        .warehouseCode(uw.getWarehouse().getCode())
                        .permissions(uw.getPermissions().stream()
                                .map(UserWarehousePermission::getPermission)
                                .toList())
                        .build())
                .toList();
    }
}
