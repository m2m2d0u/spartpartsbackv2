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
import org.springframework.web.bind.annotation.RestController;
import sn.symmetry.spareparts.dto.request.CreateTaxRateRequest;
import sn.symmetry.spareparts.dto.request.UpdateTaxRateRequest;
import sn.symmetry.spareparts.dto.response.TaxRateResponse;
import sn.symmetry.spareparts.dto.response.common.ApiResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.service.TaxRateService;

@RestController
@RequestMapping("/api/tax-rates")
@RequiredArgsConstructor
public class TaxRateController {

    private final TaxRateService taxRateService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<TaxRateResponse>>> getAllTaxRates(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(taxRateService.getAllTaxRates(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaxRateResponse>> getTaxRateById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(taxRateService.getTaxRateById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TaxRateResponse>> createTaxRate(
            @Valid @RequestBody CreateTaxRateRequest request) {
        TaxRateResponse response = taxRateService.createTaxRate(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tax rate created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaxRateResponse>> updateTaxRate(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaxRateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Tax rate updated successfully",
                taxRateService.updateTaxRate(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTaxRate(@PathVariable Long id) {
        taxRateService.deleteTaxRate(id);
        return ResponseEntity.ok(ApiResponse.success("Tax rate deleted successfully", null));
    }
}
