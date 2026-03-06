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

import sn.symmetry.spareparts.entity.StockMovement;
import sn.symmetry.spareparts.entity.WarehouseStock;
import sn.symmetry.spareparts.enums.StockMovementType;
import sn.symmetry.spareparts.repository.StockMovementRepository;
import sn.symmetry.spareparts.repository.WarehouseStockRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockTransferServiceImpl implements StockTransferService {

    private final StockTransferRepository stockTransferRepository;
    private final WarehouseRepository warehouseRepository;
    private final PartRepository partRepository;
    private final StockTransferMapper stockTransferMapper;
    private final WarehouseStockRepository warehouseStockRepository;
    private final StockMovementRepository stockMovementRepository;

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
    public StockTransferResponse getStockTransferById(UUID id) {
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
    public StockTransferResponse updateStockTransfer(UUID id, UpdateStockTransferRequest request) {
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
    public void deleteStockTransfer(UUID id) {
        StockTransfer stockTransfer = stockTransferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StockTransfer", "id", id));

        if (stockTransfer.getStatus() != StockTransferStatus.PENDING) {
            throw new BusinessRuleException("Only stock transfers with PENDING status can be deleted");
        }

        stockTransferRepository.delete(stockTransfer);
    }

    @Override
    @Transactional
    public StockTransferResponse approveTransfer(UUID id) {
        StockTransfer stockTransfer = stockTransferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StockTransfer", "id", id));

        if (stockTransfer.getStatus() != StockTransferStatus.PENDING) {
            throw new BusinessRuleException("Only PENDING transfers can be approved");
        }

        // Validate stock availability before approving
        for (StockTransferItem item : stockTransfer.getItems()) {
            Optional<WarehouseStock> stock = warehouseStockRepository
                    .findByWarehouseIdAndPartId(stockTransfer.getSourceWarehouse().getId(), item.getPart().getId());
            int available = stock.map(WarehouseStock::getQuantity).orElse(0);
            if (available < item.getQuantity()) {
                throw new BusinessRuleException("Insufficient stock for part " + item.getPart().getPartNumber()
                        + ": available " + available + ", requested " + item.getQuantity());
            }
        }

        stockTransfer.setStatus(StockTransferStatus.IN_TRANSIT);
        StockTransfer saved = stockTransferRepository.save(stockTransfer);
        return stockTransferMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public StockTransferResponse completeTransfer(UUID id) {
        StockTransfer stockTransfer = stockTransferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StockTransfer", "id", id));

        if (stockTransfer.getStatus() != StockTransferStatus.IN_TRANSIT) {
            throw new BusinessRuleException("Only IN_TRANSIT transfers can be completed");
        }

        UUID sourceWarehouseId = stockTransfer.getSourceWarehouse().getId();
        UUID destinationWarehouseId = stockTransfer.getDestinationWarehouse().getId();

        for (StockTransferItem item : stockTransfer.getItems()) {
            UUID partId = item.getPart().getId();
            int qty = item.getQuantity();

            // Deduct from source warehouse
            WarehouseStock sourceStock = warehouseStockRepository
                    .findByWarehouseIdAndPartId(sourceWarehouseId, partId)
                    .orElseThrow(() -> new BusinessRuleException(
                            "No stock entry found for part " + item.getPart().getPartNumber() + " in source warehouse"));

            int newSourceBalance = sourceStock.getQuantity() - qty;
            if (newSourceBalance < 0) {
                throw new BusinessRuleException("Insufficient stock for part " + item.getPart().getPartNumber()
                        + ": available " + sourceStock.getQuantity() + ", requested " + qty);
            }
            sourceStock.setQuantity(newSourceBalance);
            warehouseStockRepository.save(sourceStock);

            // Record TRANSFER_OUT movement
            StockMovement outMovement = new StockMovement();
            outMovement.setPart(item.getPart());
            outMovement.setWarehouse(stockTransfer.getSourceWarehouse());
            outMovement.setType(StockMovementType.TRANSFER_OUT);
            outMovement.setQuantityChange(-qty);
            outMovement.setBalanceAfter(newSourceBalance);
            outMovement.setReferenceType("STOCK_TRANSFER");
            outMovement.setReferenceId(stockTransfer.getId());
            outMovement.setNotes("Transfer " + stockTransfer.getTransferNumber() + " to " + stockTransfer.getDestinationWarehouse().getName());
            stockMovementRepository.save(outMovement);

            // Add to destination warehouse (create stock entry if not exists)
            WarehouseStock destStock = warehouseStockRepository
                    .findByWarehouseIdAndPartId(destinationWarehouseId, partId)
                    .orElseGet(() -> {
                        WarehouseStock ws = new WarehouseStock();
                        ws.setWarehouse(stockTransfer.getDestinationWarehouse());
                        ws.setPart(item.getPart());
                        ws.setQuantity(0);
                        ws.setMinStockLevel(0);
                        return ws;
                    });

            int newDestBalance = destStock.getQuantity() + qty;
            destStock.setQuantity(newDestBalance);
            warehouseStockRepository.save(destStock);

            // Record TRANSFER_IN movement
            StockMovement inMovement = new StockMovement();
            inMovement.setPart(item.getPart());
            inMovement.setWarehouse(stockTransfer.getDestinationWarehouse());
            inMovement.setType(StockMovementType.TRANSFER_IN);
            inMovement.setQuantityChange(qty);
            inMovement.setBalanceAfter(newDestBalance);
            inMovement.setReferenceType("STOCK_TRANSFER");
            inMovement.setReferenceId(stockTransfer.getId());
            inMovement.setNotes("Transfer " + stockTransfer.getTransferNumber() + " from " + stockTransfer.getSourceWarehouse().getName());
            stockMovementRepository.save(inMovement);
        }

        stockTransfer.setStatus(StockTransferStatus.COMPLETED);
        StockTransfer saved = stockTransferRepository.save(stockTransfer);
        return stockTransferMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public StockTransferResponse cancelTransfer(UUID id) {
        StockTransfer stockTransfer = stockTransferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StockTransfer", "id", id));

        if (stockTransfer.getStatus() != StockTransferStatus.PENDING
                && stockTransfer.getStatus() != StockTransferStatus.IN_TRANSIT) {
            throw new BusinessRuleException("Only PENDING or IN_TRANSIT transfers can be cancelled");
        }

        stockTransfer.setStatus(StockTransferStatus.CANCELLED);
        StockTransfer saved = stockTransferRepository.save(stockTransfer);
        return stockTransferMapper.toResponse(saved);
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
