package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.symmetry.spareparts.dto.request.CreateRefundRequest;
import sn.symmetry.spareparts.dto.response.RefundResponse;
import sn.symmetry.spareparts.entity.Invoice;
import sn.symmetry.spareparts.entity.Refund;
import sn.symmetry.spareparts.entity.Return;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.mapper.RefundMapper;
import sn.symmetry.spareparts.repository.InvoiceRepository;
import sn.symmetry.spareparts.repository.RefundRepository;
import sn.symmetry.spareparts.repository.ReturnRepository;
import sn.symmetry.spareparts.service.RefundService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefundServiceImpl implements RefundService {

    private final RefundRepository refundRepository;
    private final ReturnRepository returnRepository;
    private final InvoiceRepository invoiceRepository;
    private final RefundMapper refundMapper;

    @Override
    public List<RefundResponse> getRefunds(Long returnId) {
        if (!returnRepository.existsById(returnId)) {
            throw new ResourceNotFoundException("Return", "id", returnId);
        }

        List<Refund> refunds = refundRepository.findByReturnEntityId(returnId);
        return refundMapper.toResponseList(refunds);
    }

    @Override
    @Transactional
    public RefundResponse createRefund(Long returnId, CreateRefundRequest request) {
        Return returnEntity = returnRepository.findById(returnId)
                .orElseThrow(() -> new ResourceNotFoundException("Return", "id", returnId));

        Refund refund = new Refund();
        refund.setReturnEntity(returnEntity);
        refund.setAmount(request.getAmount());
        refund.setRefundMethod(request.getRefundMethod());
        refund.setRefundDate(request.getRefundDate());
        refund.setReference(request.getReference());
        refund.setNotes(request.getNotes());

        if (request.getInvoiceId() != null) {
            Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", request.getInvoiceId()));
            refund.setInvoice(invoice);
        }

        Refund saved = refundRepository.save(refund);
        return refundMapper.toResponse(saved);
    }
}
