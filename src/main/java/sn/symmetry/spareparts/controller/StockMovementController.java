package sn.symmetry.spareparts.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sn.symmetry.spareparts.dto.response.StockMovementResponse;
import sn.symmetry.spareparts.dto.response.common.ApiResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.enums.StockMovementType;
import sn.symmetry.spareparts.service.StockMovementService;

import java.util.UUID;

@RestController
@RequestMapping("/api/stock-movements")
@RequiredArgsConstructor
public class StockMovementController {

    private final StockMovementService stockMovementService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<StockMovementResponse>>> getAllStockMovements(
            @RequestParam(required = false) UUID warehouseId,
            @RequestParam(required = false) UUID partId,
            @RequestParam(required = false) StockMovementType type,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                stockMovementService.getAllStockMovements(warehouseId, partId, type, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StockMovementResponse>> getStockMovementById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(stockMovementService.getStockMovementById(id)));
    }
}
