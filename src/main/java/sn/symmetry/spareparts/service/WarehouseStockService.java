package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.request.UpdateWarehouseStockRequest;
import sn.symmetry.spareparts.dto.response.WarehouseStockResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;

public interface WarehouseStockService {

    PagedResponse<WarehouseStockResponse> getAllWarehouseStock(Long warehouseId, Long partId, Pageable pageable);

    WarehouseStockResponse getWarehouseStockById(Long id);

    WarehouseStockResponse updateWarehouseStock(Long id, UpdateWarehouseStockRequest request);
}
