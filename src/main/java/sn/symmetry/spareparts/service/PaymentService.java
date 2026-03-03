package sn.symmetry.spareparts.service;

import sn.symmetry.spareparts.dto.request.CreatePaymentRequest;
import sn.symmetry.spareparts.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {

    List<PaymentResponse> getPayments(Long invoiceId);

    PaymentResponse addPayment(Long invoiceId, CreatePaymentRequest request);

    void deletePayment(Long invoiceId, Long paymentId);
}
