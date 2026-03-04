package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.symmetry.spareparts.dto.request.CreateStoreRequest;
import sn.symmetry.spareparts.dto.request.UpdateStoreRequest;
import sn.symmetry.spareparts.dto.response.StoreResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.entity.Store;
import sn.symmetry.spareparts.exception.DuplicateResourceException;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.mapper.StoreMapper;
import sn.symmetry.spareparts.repository.StoreRepository;
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

    @Override
    public PagedResponse<StoreResponse> getAllStores(Boolean isActive, Pageable pageable) {
        List<UUID> accessibleStoreIds = authorizationService.getAccessibleStoreIds();

        Page<Store> page;
        if (accessibleStoreIds == null) {
            // ADMIN - see all stores
            page = isActive != null
                    ? storeRepository.findByIsActive(isActive, pageable)
                    : storeRepository.findAll(pageable);
        } else if (accessibleStoreIds.isEmpty()) {
            // No access - return empty
            page = Page.empty(pageable);
        } else {
            // Filter by accessible stores
            // Note: For simplicity, ignoring isActive filter when filtering by IDs
            // You could add a compound query method if needed
            page = storeRepository.findByIdIn(accessibleStoreIds, pageable);
        }

        return PagedResponse.of(page.map(storeMapper::toResponse));
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
}
