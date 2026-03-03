package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.symmetry.spareparts.dto.request.CreateStockTransferRequest;
import sn.symmetry.spareparts.dto.request.StockTransferItemRequest;
import sn.symmetry.spareparts.dto.request.UpdateStockTransferRequest;
import sn.symmetry.spareparts.dto.response.StockTransferResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.entity.Part;
import sn.symmetry.spareparts.entity.StockTransfer;
import sn.symmetry.spareparts.entity.StockTransferItem;
import sn.symmetry.spareparts.entity.Warehouse;
import sn.symmetry.spareparts.enums.StockTransferStatus;
import sn.symmetry.spareparts.exception.BusinessRuleException;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.mapper.StockTransferMapper;
import sn.symmetry.spareparts.repository.PartRepository;
import sn.symmetry.spareparts.repository.StockTransferRepository;
import sn.symmetry.spareparts.repository.WarehouseRepository;
import sn.symmetry.spareparts.service.StockTransferService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockTransferServiceImpl implements StockTransferService {

    private final StockTransferRepository stockTransferRepository;
    private final WarehouseRepository warehouseRepository;
    private final PartRepository partRepository;
    private final StockTransferMapper stockTransferMapper;

    @Override
    public PagedResponse<StockTransferResponse> getAllStockTransfers(StockTransferStatus status, Pageable pageable) {
        Page<StockTransfer> page;
        if (status != null) {
            page = stockTransferRepository.findByStatus(status, pageable);
        } else {
            page = stockTransferRepository.findAll(pageable);
        }
        return PagedResponse.of(page.map(stockTransferMapper::toResponse));
    }

    @Override
    public StockTransferResponse getStockTransferById(Long id) {
        StockTransfer stockTransfer = stockTransferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StockTransfer", "id", id));
        return stockTransferMapper.toResponse(stockTransfer);
    }

    @Override
    @Transactional
    public StockTransferResponse createStockTransfer(CreateStockTransferRequest request) {
        if (request.getSourceWarehouseId().equals(request.getDestinationWarehouseId())) {
            throw new BusinessRuleException("Source and destination warehouses must be different");
        }

        Warehouse sourceWarehouse = warehouseRepository.findById(request.getSourceWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", request.getSourceWarehouseId()));

        Warehouse destinationWarehouse = warehouseRepository.findById(request.getDestinationWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", request.getDestinationWarehouseId()));

        StockTransfer stockTransfer = new StockTransfer();
        stockTransfer.setTransferNumber(generateTransferNumber());
        stockTransfer.setSourceWarehouse(sourceWarehouse);
        stockTransfer.setDestinationWarehouse(destinationWarehouse);
        stockTransfer.setStatus(StockTransferStatus.PENDING);
        stockTransfer.setTransferDate(request.getTransferDate());
        stockTransfer.setNotes(request.getNotes());

        for (StockTransferItemRequest itemRequest : request.getItems()) {
            Part part = partRepository.findById(itemRequest.getPartId())
                    .orElseThrow(() -> new ResourceNotFoundException("Part", "id", itemRequest.getPartId()));

            StockTransferItem item = new StockTransferItem();
            item.setStockTransfer(stockTransfer);
            item.setPart(part);
            item.setQuantity(itemRequest.getQuantity());
            stockTransfer.getItems().add(item);
        }

        StockTransfer saved = stockTransferRepository.save(stockTransfer);
        return stockTransferMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public StockTransferResponse updateStockTransfer(Long id, UpdateStockTransferRequest request) {
        StockTransfer stockTransfer = stockTransferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StockTransfer", "id", id));

        if (stockTransfer.getStatus() != StockTransferStatus.PENDING) {
            throw new BusinessRuleException("Only stock transfers with PENDING status can be updated");
        }

        if (request.getSourceWarehouseId().equals(request.getDestinationWarehouseId())) {
            throw new BusinessRuleException("Source and destination warehouses must be different");
        }

        Warehouse sourceWarehouse = warehouseRepository.findById(request.getSourceWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", request.getSourceWarehouseId()));

        Warehouse destinationWarehouse = warehouseRepository.findById(request.getDestinationWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", request.getDestinationWarehouseId()));

        stockTransfer.setSourceWarehouse(sourceWarehouse);
        stockTransfer.setDestinationWarehouse(destinationWarehouse);
        stockTransfer.setTransferDate(request.getTransferDate());
        stockTransfer.setNotes(request.getNotes());

        stockTransfer.getItems().clear();

        for (StockTransferItemRequest itemRequest : request.getItems()) {
            Part part = partRepository.findById(itemRequest.getPartId())
                    .orElseThrow(() -> new ResourceNotFoundException("Part", "id", itemRequest.getPartId()));

            StockTransferItem item = new StockTransferItem();
            item.setStockTransfer(stockTransfer);
            item.setPart(part);
            item.setQuantity(itemRequest.getQuantity());
            stockTransfer.getItems().add(item);
        }

        StockTransfer saved = stockTransferRepository.save(stockTransfer);
        return stockTransferMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteStockTransfer(Long id) {
        StockTransfer stockTransfer = stockTransferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StockTransfer", "id", id));

        if (stockTransfer.getStatus() != StockTransferStatus.PENDING) {
            throw new BusinessRuleException("Only stock transfers with PENDING status can be deleted");
        }

        stockTransferRepository.delete(stockTransfer);
    }

    private String generateTransferNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uniquePart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String transferNumber = "ST-" + datePart + "-" + uniquePart;

        while (stockTransferRepository.existsByTransferNumber(transferNumber)) {
            uniquePart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            transferNumber = "ST-" + datePart + "-" + uniquePart;
        }

        return transferNumber;
    }
}
