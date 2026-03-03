package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.symmetry.spareparts.dto.request.UpdateWarehouseStockRequest;
import sn.symmetry.spareparts.dto.response.WarehouseStockResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.entity.WarehouseStock;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.mapper.WarehouseStockMapper;
import sn.symmetry.spareparts.repository.WarehouseStockRepository;
import sn.symmetry.spareparts.service.WarehouseStockService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseStockServiceImpl implements WarehouseStockService {

    private final WarehouseStockRepository warehouseStockRepository;
    private final WarehouseStockMapper warehouseStockMapper;

    @Override
    public PagedResponse<WarehouseStockResponse> getAllWarehouseStock(UUID warehouseId, UUID partId, Pageable pageable) {
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
        return warehouseStockMapper.toResponse(warehouseStock);
    }

    @Override
    @Transactional
    public WarehouseStockResponse updateWarehouseStock(UUID id, UpdateWarehouseStockRequest request) {
        WarehouseStock warehouseStock = warehouseStockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WarehouseStock", "id", id));

        warehouseStock.setMinStockLevel(request.getMinStockLevel());

        WarehouseStock saved = warehouseStockRepository.save(warehouseStock);
        return warehouseStockMapper.toResponse(saved);
    }
}
