package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.response.StockMovementResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.enums.StockMovementType;

import java.util.UUID;

public interface StockMovementService {

    PagedResponse<StockMovementResponse> getAllStockMovements(UUID warehouseId, UUID partId, StockMovementType type, Pageable pageable);

    StockMovementResponse getStockMovementById(UUID id);
}
