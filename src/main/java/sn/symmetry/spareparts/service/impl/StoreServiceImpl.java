package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.symmetry.spareparts.dto.request.CreateStoreRequest;
import sn.symmetry.spareparts.dto.request.UpdateStoreRequest;
import sn.symmetry.spareparts.dto.response.StoreResponse;
import sn.symmetry.spareparts.dto.response.UserResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.entity.Store;
import sn.symmetry.spareparts.entity.User;
import sn.symmetry.spareparts.entity.UserStore;
import sn.symmetry.spareparts.exception.DuplicateResourceException;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.mapper.StoreMapper;
import sn.symmetry.spareparts.mapper.UserMapper;
import sn.symmetry.spareparts.repository.StoreRepository;
import sn.symmetry.spareparts.repository.UserRepository;
import sn.symmetry.spareparts.repository.UserStoreRepository;
import sn.symmetry.spareparts.service.AuthorizationService;
import sn.symmetry.spareparts.service.StoreService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final StoreMapper storeMapper;
    private final AuthorizationService authorizationService;
    private final UserStoreRepository userStoreRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public PagedResponse<StoreResponse> getAllStores(String name, Boolean isActive, Pageable pageable) {
        List<UUID> accessibleStoreIds = authorizationService.getAccessibleStoreIds();

        Page<Store> page;
        if (accessibleStoreIds == null) {
            // ADMIN - see all stores
            if (name != null && isActive != null) {
                page = storeRepository.findByNameContainingIgnoreCaseAndIsActive(name, isActive, pageable);
            } else if (name != null) {
                page = storeRepository.findByNameContainingIgnoreCase(name, pageable);
            } else if (isActive != null) {
                page = storeRepository.findByIsActive(isActive, pageable);
            } else {
                page = storeRepository.findAll(pageable);
            }
        } else if (accessibleStoreIds.isEmpty()) {
            // No access - return empty
            page = Page.empty(pageable);
        } else {
            // Filter by accessible stores
            if (name != null) {
                page = storeRepository.findByNameContainingIgnoreCaseAndIdIn(name, accessibleStoreIds, pageable);
            } else {
                page = storeRepository.findByIdIn(accessibleStoreIds, pageable);
            }
        }

        return PagedResponse.of(page.map(storeMapper::toResponse));
    }

    @Override
    public List<StoreResponse> getMyStores() {
        List<UUID> accessibleStoreIds = authorizationService.getAccessibleStoreIds();

        List<Store> stores;
        if (accessibleStoreIds == null) {
            // ADMIN - all stores
            stores = storeRepository.findAll();
        } else if (accessibleStoreIds.isEmpty()) {
            stores = List.of();
        } else {
            stores = storeRepository.findAllById(accessibleStoreIds);
        }

        return stores.stream().map(storeMapper::toResponse).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public StoreResponse getStoreById(UUID id) {
        authorizationService.requireStoreAccess(id);

        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store", "id", id));
        return storeMapper.toResponse(store);
    }

    @Override
    @Transactional
    public StoreResponse createStore(CreateStoreRequest request) {
        // Only ADMIN can create stores
        authorizationService.requireAdmin();

        if (storeRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Store", "code", request.getCode());
        }
        Store store = storeMapper.toEntity(request);
        store = storeRepository.save(store);
        return storeMapper.toResponse(store);
    }

    @Override
    @Transactional
    public StoreResponse updateStore(UUID id, UpdateStoreRequest request) {
        // STORE_MANAGER can only update their own stores
        if (authorizationService.isStoreManager()) {
            authorizationService.requireStoreAccess(id);
        }

        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store", "id", id));
        if (storeRepository.existsByCodeAndIdNot(request.getCode(), id)) {
            throw new DuplicateResourceException("Store", "code", request.getCode());
        }
        storeMapper.updateEntity(request, store);
        store = storeRepository.save(store);
        return storeMapper.toResponse(store);
    }

    @Override
    @Transactional
    public void deleteStore(UUID id) {
        // Only ADMIN can delete stores
        authorizationService.requireAdmin();

        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store", "id", id));
        store.setIsActive(false);
        storeRepository.save(store);
    }

    @Override
    public List<UserResponse> getStoreUsers(UUID storeId) {
        if (!storeRepository.existsById(storeId)) {
            throw new ResourceNotFoundException("Store", "id", storeId);
        }
        return userStoreRepository.findByStoreId(storeId).stream()
                .map(us -> userMapper.toResponse(us.getUser()))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional
    public void assignUserToStore(UUID storeId, UUID userId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store", "id", storeId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (userStoreRepository.existsByUserIdAndStoreId(userId, storeId)) {
            throw new DuplicateResourceException("UserStore", "userId+storeId", userId + "+" + storeId);
        }

        UserStore userStore = new UserStore();
        userStore.setUser(user);
        userStore.setStore(store);
        userStoreRepository.save(userStore);
    }

    @Override
    @Transactional
    public void unassignUserFromStore(UUID storeId, UUID userId) {
        if (!storeRepository.existsById(storeId)) {
            throw new ResourceNotFoundException("Store", "id", storeId);
        }
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        if (!userStoreRepository.existsByUserIdAndStoreId(userId, storeId)) {
            throw new ResourceNotFoundException("UserStore", "userId+storeId", userId + "+" + storeId);
        }
        userStoreRepository.deleteByUserIdAndStoreId(userId, storeId);
    }
}
