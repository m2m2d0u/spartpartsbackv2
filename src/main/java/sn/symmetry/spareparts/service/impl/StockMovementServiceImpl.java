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
import sn.symmetry.spareparts.service.StockMovementService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockMovementServiceImpl implements StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final StockMovementMapper stockMovementMapper;

    @Override
    public PagedResponse<StockMovementResponse> getAllStockMovements(UUID warehouseId, UUID partId, StockMovementType type, Pageable pageable) {
        Page<StockMovement> page;
        if (warehouseId != null && partId != null) {
            page = stockMovementRepository.findByWarehouseIdAndPartId(warehouseId, partId, pageable);
        } else if (warehouseId != null) {
            page = stockMovementRepository.findByWarehouseId(warehouseId, pageable);
        } else if (partId != null) {
            page = stockMovementRepository.findByPartId(partId, pageable);
        } else if (type != null) {
            page = stockMovementRepository.findByType(type, pageable);
        } else {
            page = stockMovementRepository.findAll(pageable);
        }
        return PagedResponse.of(page.map(stockMovementMapper::toResponse));
    }

    @Override
    public StockMovementResponse getStockMovementById(UUID id) {
        StockMovement stockMovement = stockMovementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StockMovement", "id", id));
        return stockMovementMapper.toResponse(stockMovement);
    }
}
