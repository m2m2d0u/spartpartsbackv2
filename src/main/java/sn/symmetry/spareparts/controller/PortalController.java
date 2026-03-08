package sn.symmetry.spareparts.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sn.symmetry.spareparts.dto.request.portal.PortalCreateOrderRequest;
import sn.symmetry.spareparts.dto.response.common.ApiResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.dto.response.portal.PortalCarBrandResponse;
import sn.symmetry.spareparts.dto.response.portal.PortalCarModelResponse;
import sn.symmetry.spareparts.dto.response.portal.PortalCategoryResponse;
import sn.symmetry.spareparts.dto.response.portal.PortalCompanySettingsResponse;
import sn.symmetry.spareparts.dto.response.portal.PortalOrderConfirmationResponse;
import sn.symmetry.spareparts.dto.response.portal.PortalPartDetailResponse;
import sn.symmetry.spareparts.dto.response.portal.PortalPartResponse;
import sn.symmetry.spareparts.dto.response.portal.PortalStoreConfigResponse;
import sn.symmetry.spareparts.service.PortalService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/portal")
@RequiredArgsConstructor
public class PortalController {

    private final PortalService portalService;

    @GetMapping("/parts")
    public ResponseEntity<ApiResponse<PagedResponse<PortalPartResponse>>> searchParts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID carBrandId,
            @RequestParam(required = false) UUID carModelId,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                portalService.searchParts(name, categoryId, carBrandId, carModelId, pageable)));
    }

    @GetMapping("/parts/{id}")
    public ResponseEntity<ApiResponse<PortalPartDetailResponse>> getPartById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(portalService.getPartById(id)));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<PortalCategoryResponse>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(portalService.getCategories()));
    }

    @GetMapping("/car-brands")
    public ResponseEntity<ApiResponse<List<PortalCarBrandResponse>>> getCarBrands() {
        return ResponseEntity.ok(ApiResponse.success(portalService.getCarBrands()));
    }

    @GetMapping("/car-models")
    public ResponseEntity<ApiResponse<List<PortalCarModelResponse>>> getCarModels(
            @RequestParam(required = false) UUID brandId) {
        return ResponseEntity.ok(ApiResponse.success(portalService.getCarModels(brandId)));
    }

    @GetMapping("/store-config")
    public ResponseEntity<ApiResponse<PortalStoreConfigResponse>> getStoreConfig() {
        return ResponseEntity.ok(ApiResponse.success(portalService.getStoreConfig()));
    }

    @GetMapping("/company-settings")
    public ResponseEntity<ApiResponse<PortalCompanySettingsResponse>> getCompanySettings() {
        return ResponseEntity.ok(ApiResponse.success(portalService.getCompanySettings()));
    }

    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<PortalOrderConfirmationResponse>> createOrder(
            @Valid @RequestBody PortalCreateOrderRequest request) {
        PortalOrderConfirmationResponse response = portalService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order placed successfully", response));
    }

    @GetMapping("/orders/{orderNumber}")
    public ResponseEntity<ApiResponse<PortalOrderConfirmationResponse>> getOrderByNumber(
            @PathVariable String orderNumber) {
        return ResponseEntity.ok(ApiResponse.success(portalService.getOrderByNumber(orderNumber)));
    }
}
