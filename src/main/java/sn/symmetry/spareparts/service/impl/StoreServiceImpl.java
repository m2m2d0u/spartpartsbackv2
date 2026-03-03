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
import sn.symmetry.spareparts.service.StoreService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final StoreMapper storeMapper;

    @Override
    public PagedResponse<StoreResponse> getAllStores(Boolean isActive, Pageable pageable) {
        Page<StoreResponse> page;
        if (isActive != null) {
            page = storeRepository.findByIsActive(isActive, pageable).map(storeMapper::toResponse);
        } else {
            page = storeRepository.findAll(pageable).map(storeMapper::toResponse);
        }
        return PagedResponse.of(page);
    }

    @Override
    public StoreResponse getStoreById(UUID id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store", "id", id));
        return storeMapper.toResponse(store);
    }

    @Override
    @Transactional
    public StoreResponse createStore(CreateStoreRequest request) {
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
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store", "id", id));
        store.setIsActive(false);
        storeRepository.save(store);
    }
}
