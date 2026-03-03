package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.symmetry.spareparts.dto.request.AdjustWarehouseStockRequest;
import sn.symmetry.spareparts.dto.request.UpdateWarehouseStockRequest;
import sn.symmetry.spareparts.dto.response.WarehouseStockResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.entity.Part;
import sn.symmetry.spareparts.entity.StockMovement;
import sn.symmetry.spareparts.entity.Warehouse;
import sn.symmetry.spareparts.entity.WarehouseStock;
import sn.symmetry.spareparts.enums.StockMovementType;
import sn.symmetry.spareparts.enums.WarehousePermission;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.mapper.WarehouseStockMapper;
import sn.symmetry.spareparts.service.AuthorizationService;
import sn.symmetry.spareparts.repository.PartRepository;
import sn.symmetry.spareparts.repository.StockMovementRepository;
import sn.symmetry.spareparts.repository.WarehouseRepository;
import sn.symmetry.spareparts.repository.WarehouseStockRepository;
import sn.symmetry.spareparts.service.WarehouseStockService;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseStockServiceImpl implements WarehouseStockService {

    private final WarehouseStockRepository warehouseStockRepository;
    private final WarehouseRepository warehouseRepository;
    private final PartRepository partRepository;
    private final StockMovementRepository stockMovementRepository;
    private final WarehouseStockMapper warehouseStockMapper;
    private final AuthorizationService authorizationService;

    @Override
    public PagedResponse<WarehouseStockResponse> getAllWarehouseStock(UUID warehouseId, UUID partId, Pageable pageable) {
        authorizationService.requireWarehouseAccess(warehouseId);

        Page<WarehouseStock> page;
        if (partId != null) {
            page = warehouseStockRepository.findByWarehouseIdAndPartId(warehouseId, partId, pageable);
        } else {
            page = warehouseStockRepository.findByWarehouseId(warehouseId, pageable);
        }
        return PagedResponse.of(page.map(warehouseStockMapper::toResponse));
    }

    @Override
    public WarehouseStockResponse getWarehouseStockById(UUID id) {
        WarehouseStock warehouseStock = warehouseStockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WarehouseStock", "id", id));

        authorizationService.requireWarehouseAccess(warehouseStock.getWarehouse().getId());

        return warehouseStockMapper.toResponse(warehouseStock);
    }

    @Override
    @Transactional
    public WarehouseStockResponse updateWarehouseStock(UUID id, UpdateWarehouseStockRequest request) {
        WarehouseStock warehouseStock = warehouseStockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WarehouseStock", "id", id));

        authorizationService.requireWarehousePermission(
                warehouseStock.getWarehouse().getId(),
                WarehousePermission.STOCK_MANAGE
        );

        warehouseStock.setMinStockLevel(request.getMinStockLevel());

        WarehouseStock saved = warehouseStockRepository.save(warehouseStock);
        return warehouseStockMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public WarehouseStockResponse adjustStock(AdjustWarehouseStockRequest request) {
        authorizationService.requireWarehousePermission(
                request.getWarehouseId(),
                WarehousePermission.STOCK_MANAGE
        );

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", request.getWarehouseId()));

        Part part = partRepository.findById(request.getPartId())
                .orElseThrow(() -> new ResourceNotFoundException("Part", "id", request.getPartId()));

        // Find existing stock or create new entry
        Optional<WarehouseStock> existingStock = warehouseStockRepository
                .findByWarehouseIdAndPartId(request.getWarehouseId(), request.getPartId());

        WarehouseStock warehouseStock;
        StockMovementType movementType;

        if (existingStock.isPresent()) {
            warehouseStock = existingStock.get();
            movementType = StockMovementType.ADJUSTMENT;
        } else {
            warehouseStock = new WarehouseStock();
            warehouseStock.setWarehouse(warehouse);
            warehouseStock.setPart(part);
            warehouseStock.setQuantity(0);
            warehouseStock.setMinStockLevel(0);
            movementType = StockMovementType.INITIAL;
        }

        // Set min stock level if provided
        if (request.getMinStockLevel() != null) {
            warehouseStock.setMinStockLevel(request.getMinStockLevel());
        }

        int quantityChange = request.getQuantity();
        int newBalance = warehouseStock.getQuantity() + quantityChange;
        warehouseStock.setQuantity(newBalance);

        WarehouseStock saved = warehouseStockRepository.save(warehouseStock);

        // Create stock movement record
        StockMovement movement = new StockMovement();
        movement.setPart(part);
        movement.setWarehouse(warehouse);
        movement.setType(movementType);
        movement.setQuantityChange(quantityChange);
        movement.setBalanceAfter(newBalance);
        movement.setNotes(request.getNotes());
        stockMovementRepository.save(movement);

        return warehouseStockMapper.toResponse(saved);
    }
}
