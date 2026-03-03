package sn.symmetry.spareparts.service;

import sn.symmetry.spareparts.dto.request.CreateRefundRequest;
import sn.symmetry.spareparts.dto.response.RefundResponse;

import java.util.List;
import java.util.UUID;

public interface RefundService {

    List<RefundResponse> getRefunds(UUID returnId);

    RefundResponse createRefund(UUID returnId, CreateRefundRequest request);
}
