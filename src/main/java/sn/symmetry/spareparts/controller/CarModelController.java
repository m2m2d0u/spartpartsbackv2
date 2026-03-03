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
import sn.symmetry.spareparts.dto.request.CreateCarModelRequest;
import sn.symmetry.spareparts.dto.request.UpdateCarModelRequest;
import sn.symmetry.spareparts.dto.response.CarModelResponse;
import sn.symmetry.spareparts.dto.response.common.ApiResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.service.CarModelService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/car-models")
@RequiredArgsConstructor
public class CarModelController {

    private final CarModelService carModelService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<CarModelResponse>>> getAllCarModels(
            @RequestParam(required = false) UUID brandId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(carModelService.getAllCarModels(brandId, pageable)));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<CarModelResponse>>> getAllCarModelsList(
            @RequestParam(required = false) UUID brandId) {
        return ResponseEntity.ok(ApiResponse.success(carModelService.getAllCarModelsList(brandId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CarModelResponse>> getCarModelById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(carModelService.getCarModelById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CarModelResponse>> createCarModel(
            @Valid @RequestBody CreateCarModelRequest request) {
        CarModelResponse response = carModelService.createCarModel(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Car model created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CarModelResponse>> updateCarModel(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCarModelRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Car model updated successfully",
                carModelService.updateCarModel(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCarModel(@PathVariable UUID id) {
        carModelService.deleteCarModel(id);
        return ResponseEntity.ok(ApiResponse.success("Car model deleted successfully", null));
    }
}
