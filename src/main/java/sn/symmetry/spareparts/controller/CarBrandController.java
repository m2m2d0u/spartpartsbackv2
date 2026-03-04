package sn.symmetry.spareparts.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import sn.symmetry.spareparts.dto.request.CreateCarBrandRequest;
import sn.symmetry.spareparts.dto.request.UpdateCarBrandRequest;
import sn.symmetry.spareparts.dto.response.CarBrandResponse;
import sn.symmetry.spareparts.dto.response.common.ApiResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.service.CarBrandService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/car-brands")
@RequiredArgsConstructor
public class CarBrandController {

    private final CarBrandService carBrandService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<CarBrandResponse>>> getAllCarBrands(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(carBrandService.getAllCarBrands(pageable)));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<CarBrandResponse>>> getAllCarBrandsList() {
        return ResponseEntity.ok(ApiResponse.success(carBrandService.getAllCarBrandsList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CarBrandResponse>> getCarBrandById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(carBrandService.getCarBrandById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CarBrandResponse>> createCarBrand(
            @Valid @RequestBody CreateCarBrandRequest request) {
        CarBrandResponse response = carBrandService.createCarBrand(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Car brand created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CarBrandResponse>> updateCarBrand(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCarBrandRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Car brand updated successfully",
                carBrandService.updateCarBrand(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCarBrand(@PathVariable UUID id) {
        carBrandService.deleteCarBrand(id);
        return ResponseEntity.ok(ApiResponse.success("Car brand deleted successfully", null));
    }
}
