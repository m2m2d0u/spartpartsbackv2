package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.symmetry.spareparts.dto.request.CreateWarehouseRequest;
import sn.symmetry.spareparts.dto.request.UpdateWarehouseRequest;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.dto.response.UserResponse;
import sn.symmetry.spareparts.dto.response.WarehouseResponse;
import sn.symmetry.spareparts.entity.Store;
import sn.symmetry.spareparts.entity.User;
import sn.symmetry.spareparts.entity.UserWarehouse;
import sn.symmetry.spareparts.entity.Warehouse;
import sn.symmetry.spareparts.exception.DuplicateResourceException;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.mapper.UserMapper;
import sn.symmetry.spareparts.mapper.WarehouseMapper;
import sn.symmetry.spareparts.repository.StoreRepository;
import sn.symmetry.spareparts.repository.UserRepository;
import sn.symmetry.spareparts.repository.UserWarehouseRepository;
import sn.symmetry.spareparts.repository.WarehouseRepository;
import sn.symmetry.spareparts.service.AuthorizationService;
import sn.symmetry.spareparts.service.WarehouseService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final StoreRepository storeRepository;
    private final AuthorizationService authorizationService;
    private final UserWarehouseRepository userWarehouseRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public PagedResponse<WarehouseResponse> getAllWarehouses(Boolean isActive, Pageable pageable) {
        List<UUID> accessibleWarehouseIds = authorizationService.getAccessibleWarehouseIds();

        Page<Warehouse> page;
        if (accessibleWarehouseIds == null) {
            // ADMIN - see all warehouses
            page = isActive != null
                    ? warehouseRepository.findByIsActive(isActive, pageable)
                    : warehouseRepository.findAll(pageable);
        } else if (accessibleWarehouseIds.isEmpty()) {
            // No access - return empty
            page = Page.empty(pageable);
        } else {
            // Filter by accessible warehouses
            page = warehouseRepository.findByIdIn(accessibleWarehouseIds, pageable);
        }

        return PagedResponse.of(page.map(warehouseMapper::toResponse));
    }

    @Override
    public WarehouseResponse getWarehouseById(UUID id) {
        authorizationService.requireWarehouseAccess(id);

        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));
        return warehouseMapper.toResponse(warehouse);
    }

    @Override
    @Transactional
    public WarehouseResponse createWarehouse(CreateWarehouseRequest request) {
        // For STORE_MANAGER, check they can access the store
        if (authorizationService.isStoreManager()) {
            authorizationService.requireStoreAccess(request.getStoreId());
        }

        if (warehouseRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Warehouse", "code", request.getCode());
        }

        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("Store", "id", request.getStoreId()));
        Warehouse warehouse = warehouseMapper.toEntity(request);
        warehouse.setStore(store);
        Warehouse saved = warehouseRepository.save(warehouse);
        return warehouseMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public WarehouseResponse updateWarehouse(UUID id, UpdateWarehouseRequest request) {
        authorizationService.requireWarehouseAccess(id);

        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));

        if (warehouseRepository.existsByCodeAndIdNot(request.getCode(), id)) {
            throw new DuplicateResourceException("Warehouse", "code", request.getCode());
        }

        // Check access to the new store if it's being changed
        if (authorizationService.isStoreManager()) {
            authorizationService.requireStoreAccess(request.getStoreId());
        }

        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("Store", "id", request.getStoreId()));
        warehouseMapper.updateEntity(request, warehouse);
        warehouse.setStore(store);
        Warehouse saved = warehouseRepository.save(warehouse);
        return warehouseMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteWarehouse(UUID id) {
        // Only ADMIN can delete warehouses
        authorizationService.requireAdmin();

        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));
        warehouse.setIsActive(false);
        warehouseRepository.save(warehouse);
    }

    @Override
    public List<UserResponse> getWarehouseUsers(UUID warehouseId) {
        if (!warehouseRepository.existsById(warehouseId)) {
            throw new ResourceNotFoundException("Warehouse", "id", warehouseId);
        }
        return userWarehouseRepository.findByWarehouseId(warehouseId).stream()
                .map(uw -> userMapper.toResponse(uw.getUser()))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional
    public void assignUserToWarehouse(UUID warehouseId, UUID userId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", warehouseId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (userWarehouseRepository.findByUserIdAndWarehouseId(userId, warehouseId).isPresent()) {
            throw new DuplicateResourceException("UserWarehouse", "userId+warehouseId", userId + "+" + warehouseId);
        }

        UserWarehouse userWarehouse = new UserWarehouse();
        userWarehouse.setUser(user);
        userWarehouse.setWarehouse(warehouse);
        userWarehouseRepository.save(userWarehouse);
    }

    @Override
    @Transactional
    public void unassignUserFromWarehouse(UUID warehouseId, UUID userId) {
        if (!warehouseRepository.existsById(warehouseId)) {
            throw new ResourceNotFoundException("Warehouse", "id", warehouseId);
        }
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        if (userWarehouseRepository.findByUserIdAndWarehouseId(userId, warehouseId).isEmpty()) {
            throw new ResourceNotFoundException("UserWarehouse", "userId+warehouseId", userId + "+" + warehouseId);
        }
        userWarehouseRepository.deleteByUserIdAndWarehouseId(userId, warehouseId);
    }
}
