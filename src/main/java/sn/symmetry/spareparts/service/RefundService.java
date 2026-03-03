package sn.symmetry.spareparts.service;

import sn.symmetry.spareparts.dto.request.CreateRefundRequest;
import sn.symmetry.spareparts.dto.response.RefundResponse;

import java.util.List;

public interface RefundService {

    List<RefundResponse> getRefunds(Long returnId);

    RefundResponse createRefund(Long returnId, CreateRefundRequest request);
}
