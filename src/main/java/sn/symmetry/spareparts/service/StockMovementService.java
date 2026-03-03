package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.response.StockMovementResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.enums.StockMovementType;

public interface StockMovementService {

    PagedResponse<StockMovementResponse> getAllStockMovements(Long warehouseId, Long partId, StockMovementType type, Pageable pageable);

    StockMovementResponse getStockMovementById(Long id);
}
