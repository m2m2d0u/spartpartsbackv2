package sn.symmetry.spareparts.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.symmetry.spareparts.dto.request.CreateRefundRequest;
import sn.symmetry.spareparts.dto.response.RefundResponse;
import sn.symmetry.spareparts.dto.response.common.ApiResponse;
import sn.symmetry.spareparts.service.RefundService;

import java.util.List;

@RestController
@RequestMapping("/api/returns/{returnId}/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RefundResponse>>> getRefunds(
            @PathVariable Long returnId) {
        return ResponseEntity.ok(ApiResponse.success(refundService.getRefunds(returnId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RefundResponse>> createRefund(
            @PathVariable Long returnId,
            @Valid @RequestBody CreateRefundRequest request) {
        RefundResponse response = refundService.createRefund(returnId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Refund created successfully", response));
    }
}
