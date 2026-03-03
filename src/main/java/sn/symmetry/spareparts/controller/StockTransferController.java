package sn.symmetry.spareparts.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sn.symmetry.spareparts.dto.request.CreateStockTransferRequest;
import sn.symmetry.spareparts.dto.request.UpdateStockTransferRequest;
import sn.symmetry.spareparts.dto.response.StockTransferResponse;
import sn.symmetry.spareparts.dto.response.common.ApiResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.enums.StockTransferStatus;
import sn.symmetry.spareparts.service.StockTransferService;

import java.util.UUID;

@RestController
@RequestMapping("/api/stock-transfers")
@RequiredArgsConstructor
public class StockTransferController {

    private final StockTransferService stockTransferService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<StockTransferResponse>>> getAllStockTransfers(
            @RequestParam(required = false) StockTransferStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                stockTransferService.getAllStockTransfers(status, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StockTransferResponse>> getStockTransferById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(stockTransferService.getStockTransferById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StockTransferResponse>> createStockTransfer(
            @Valid @RequestBody CreateStockTransferRequest request) {
        StockTransferResponse response = stockTransferService.createStockTransfer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Stock transfer created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StockTransferResponse>> updateStockTransfer(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStockTransferRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Stock transfer updated successfully",
                stockTransferService.updateStockTransfer(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStockTransfer(@PathVariable UUID id) {
        stockTransferService.deleteStockTransfer(id);
        return ResponseEntity.ok(ApiResponse.success("Stock transfer deleted successfully", null));
    }
}
