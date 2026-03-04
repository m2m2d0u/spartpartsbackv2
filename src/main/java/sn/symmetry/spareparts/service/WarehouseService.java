package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.request.CreateWarehouseRequest;
import sn.symmetry.spareparts.dto.request.UpdateWarehouseRequest;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.dto.response.UserResponse;
import sn.symmetry.spareparts.dto.response.WarehouseResponse;

import java.util.List;
import java.util.UUID;

public interface WarehouseService {

    PagedResponse<WarehouseResponse> getAllWarehouses(String name, Boolean isActive, Pageable pageable);

    WarehouseResponse getWarehouseById(UUID id);

    WarehouseResponse createWarehouse(CreateWarehouseRequest request);

    WarehouseResponse updateWarehouse(UUID id, UpdateWarehouseRequest request);

    void deleteWarehouse(UUID id);

    List<UserResponse> getWarehouseUsers(UUID warehouseId);
    void assignUserToWarehouse(UUID warehouseId, UUID userId);
    void unassignUserFromWarehouse(UUID warehouseId, UUID userId);
}
