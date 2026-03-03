package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.symmetry.spareparts.dto.request.CreatePaymentRequest;
import sn.symmetry.spareparts.dto.response.PaymentResponse;
import sn.symmetry.spareparts.entity.Invoice;
import sn.symmetry.spareparts.entity.Payment;
import sn.symmetry.spareparts.exception.BusinessRuleException;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.mapper.PaymentMapper;
import sn.symmetry.spareparts.repository.InvoiceRepository;
import sn.symmetry.spareparts.repository.PaymentRepository;
import sn.symmetry.spareparts.service.PaymentService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public List<PaymentResponse> getPayments(UUID invoiceId) {
        if (!invoiceRepository.existsById(invoiceId)) {
            throw new ResourceNotFoundException("Invoice", "id", invoiceId);
        }

        List<Payment> payments = paymentRepository.findByInvoiceId(invoiceId);
        return paymentMapper.toResponseList(payments);
    }

    @Override
    @Transactional
    public PaymentResponse addPayment(UUID invoiceId, CreatePaymentRequest request) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentDate(request.getPaymentDate());
        payment.setReference(request.getReference());
        payment.setNotes(request.getNotes());

        Payment saved = paymentRepository.save(payment);
        return paymentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deletePayment(UUID invoiceId, UUID paymentId) {
        if (!invoiceRepository.existsById(invoiceId)) {
            throw new ResourceNotFoundException("Invoice", "id", invoiceId);
        }

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));

        if (!payment.getInvoice().getId().equals(invoiceId)) {
            throw new BusinessRuleException("Payment does not belong to the specified invoice");
        }

        paymentRepository.delete(payment);
    }
}
