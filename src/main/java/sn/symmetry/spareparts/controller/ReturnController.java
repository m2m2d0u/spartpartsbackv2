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
import sn.symmetry.spareparts.dto.request.CreateReturnRequest;
import sn.symmetry.spareparts.dto.request.UpdateReturnRequest;
import sn.symmetry.spareparts.dto.request.UpdateReturnStatusRequest;
import sn.symmetry.spareparts.dto.response.ReturnResponse;
import sn.symmetry.spareparts.dto.response.common.ApiResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.enums.ReturnStatus;
import sn.symmetry.spareparts.service.ReturnService;

import java.util.UUID;

@RestController
@RequestMapping("/api/returns")
@RequiredArgsConstructor
public class ReturnController {

    private final ReturnService returnService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ReturnResponse>>> getAllReturns(
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) ReturnStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                returnService.getAllReturns(customerId, status, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReturnResponse>> getReturnById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(returnService.getReturnById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReturnResponse>> createReturn(
            @Valid @RequestBody CreateReturnRequest request) {
        ReturnResponse response = returnService.createReturn(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Return created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReturnResponse>> updateReturn(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateReturnRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Return updated successfully",
                returnService.updateReturn(id, request)));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ReturnResponse>> updateReturnStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateReturnStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Return status updated successfully",
                returnService.updateReturnStatus(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReturn(@PathVariable UUID id) {
        returnService.deleteReturn(id);
        return ResponseEntity.ok(ApiResponse.success("Return deleted successfully", null));
    }
}
