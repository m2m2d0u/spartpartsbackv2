package sn.symmetry.spareparts.service;

import sn.symmetry.spareparts.dto.request.CreatePaymentRequest;
import sn.symmetry.spareparts.dto.response.PaymentResponse;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    List<PaymentResponse> getPayments(UUID invoiceId);

    PaymentResponse addPayment(UUID invoiceId, CreatePaymentRequest request);

    void deletePayment(UUID invoiceId, UUID paymentId);
}
