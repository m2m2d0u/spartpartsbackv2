package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.request.CreateWarehouseRequest;
import sn.symmetry.spareparts.dto.request.UpdateWarehouseRequest;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.dto.response.WarehouseResponse;

public interface WarehouseService {

    PagedResponse<WarehouseResponse> getAllWarehouses(Boolean isActive, Pageable pageable);

    WarehouseResponse getWarehouseById(Long id);

    WarehouseResponse createWarehouse(CreateWarehouseRequest request);

    WarehouseResponse updateWarehouse(Long id, UpdateWarehouseRequest request);

    void deleteWarehouse(Long id);
}
