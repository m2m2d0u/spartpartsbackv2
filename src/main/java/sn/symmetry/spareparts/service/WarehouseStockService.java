package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.request.UpdateWarehouseStockRequest;
import sn.symmetry.spareparts.dto.response.WarehouseStockResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;

import java.util.UUID;

public interface WarehouseStockService {

    PagedResponse<WarehouseStockResponse> getAllWarehouseStock(UUID warehouseId, UUID partId, Pageable pageable);

    WarehouseStockResponse getWarehouseStockById(UUID id);

    WarehouseStockResponse updateWarehouseStock(UUID id, UpdateWarehouseStockRequest request);
}
