package sn.symmetry.spareparts.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.symmetry.spareparts.dto.request.CreatePaymentRequest;
import sn.symmetry.spareparts.dto.response.PaymentResponse;
import sn.symmetry.spareparts.dto.response.common.ApiResponse;
import sn.symmetry.spareparts.service.PaymentService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/invoices/{invoiceId}/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPayments(
            @PathVariable UUID invoiceId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPayments(invoiceId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> addPayment(
            @PathVariable UUID invoiceId,
            @Valid @RequestBody CreatePaymentRequest request) {
        PaymentResponse response = paymentService.addPayment(invoiceId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment recorded successfully", response));
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<Void>> deletePayment(
            @PathVariable UUID invoiceId,
            @PathVariable UUID paymentId) {
        paymentService.deletePayment(invoiceId, paymentId);
        return ResponseEntity.ok(ApiResponse.success("Payment deleted successfully", null));
    }
}
