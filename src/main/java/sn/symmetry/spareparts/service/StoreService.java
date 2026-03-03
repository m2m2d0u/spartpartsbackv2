package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.request.CreateStoreRequest;
import sn.symmetry.spareparts.dto.request.UpdateStoreRequest;
import sn.symmetry.spareparts.dto.response.StoreResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;

import java.util.UUID;

public interface StoreService {
    PagedResponse<StoreResponse> getAllStores(Boolean isActive, Pageable pageable);
    StoreResponse getStoreById(UUID id);
    StoreResponse createStore(CreateStoreRequest request);
    StoreResponse updateStore(UUID id, UpdateStoreRequest request);
    void deleteStore(UUID id);
}
