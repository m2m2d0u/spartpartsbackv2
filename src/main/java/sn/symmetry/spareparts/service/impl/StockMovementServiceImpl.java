package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.symmetry.spareparts.dto.response.StockMovementResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.entity.StockMovement;
import sn.symmetry.spareparts.enums.StockMovementType;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.mapper.StockMovementMapper;
import sn.symmetry.spareparts.repository.StockMovementRepository;
import sn.symmetry.spareparts.service.AuthorizationService;
import sn.symmetry.spareparts.service.StockMovementService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockMovementServiceImpl implements StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final StockMovementMapper stockMovementMapper;
    private final AuthorizationService authorizationService;

    @Override
    public PagedResponse<StockMovementResponse> getAllStockMovements(UUID warehouseId, UUID partId, StockMovementType type, Pageable pageable) {
        List<UUID> accessibleWarehouseIds = authorizationService.getAccessibleWarehouseIds();

        Page<StockMovement> page;

        if (accessibleWarehouseIds == null) {
            // Admin: no scope restriction
            page = queryUnscoped(warehouseId, partId, type, pageable);
        } else if (accessibleWarehouseIds.isEmpty()) {
            // No accessible warehouses — return empty page
            page = Page.empty(pageable);
        } else if (warehouseId != null) {
            // Specific warehouse requested — validate access
            authorizationService.requireWarehouseAccess(warehouseId);
            page = queryUnscoped(warehouseId, partId, type, pageable);
        } else {
            // Scope to accessible warehouses
            page = queryScoped(accessibleWarehouseIds, partId, type, pageable);
        }

        return PagedResponse.of(page.map(stockMovementMapper::toResponse));
    }

    @Override
    public StockMovementResponse getStockMovementById(UUID id) {
        StockMovement stockMovement = stockMovementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StockMovement", "id", id));

        authorizationService.requireWarehouseAccess(stockMovement.getWarehouse().getId());

        return stockMovementMapper.toResponse(stockMovement);
    }

    /**
     * Query without warehouse scoping (admin or specific warehouse already validated).
     */
    private Page<StockMovement> queryUnscoped(UUID warehouseId, UUID partId, StockMovementType type, Pageable pageable) {
        if (warehouseId != null && partId != null) {
            return stockMovementRepository.findByWarehouseIdAndPartId(warehouseId, partId, pageable);
        } else if (warehouseId != null) {
            return stockMovementRepository.findByWarehouseId(warehouseId, pageable);
        } else if (partId != null) {
            return stockMovementRepository.findByPartId(partId, pageable);
        } else if (type != null) {
            return stockMovementRepository.findByType(type, pageable);
        } else {
            return stockMovementRepository.findAll(pageable);
        }
    }

    /**
     * Query scoped to accessible warehouse IDs (store-level and warehouse-level users).
     */
    private Page<StockMovement> queryScoped(List<UUID> warehouseIds, UUID partId, StockMovementType type, Pageable pageable) {
        if (partId != null) {
            return stockMovementRepository.findByWarehouseIdInAndPartId(warehouseIds, partId, pageable);
        } else if (type != null) {
            return stockMovementRepository.findByWarehouseIdInAndType(warehouseIds, type, pageable);
        } else {
            return stockMovementRepository.findByWarehouseIdIn(warehouseIds, pageable);
        }
    }
}
