package sn.symmetry.spareparts.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sn.symmetry.spareparts.dto.request.UpdateWarehouseStockRequest;
import sn.symmetry.spareparts.dto.response.WarehouseStockResponse;
import sn.symmetry.spareparts.dto.response.common.ApiResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.service.WarehouseStockService;

@RestController
@RequestMapping("/api/warehouse-stock")
@RequiredArgsConstructor
public class WarehouseStockController {

    private final WarehouseStockService warehouseStockService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<WarehouseStockResponse>>> getAllWarehouseStock(
            @RequestParam Long warehouseId,
            @RequestParam(required = false) Long partId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                warehouseStockService.getAllWarehouseStock(warehouseId, partId, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WarehouseStockResponse>> getWarehouseStockById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(warehouseStockService.getWarehouseStockById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WarehouseStockResponse>> updateWarehouseStock(
            @PathVariable Long id,
            @Valid @RequestBody UpdateWarehouseStockRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Warehouse stock updated successfully",
                warehouseStockService.updateWarehouseStock(id, request)));
    }
}
