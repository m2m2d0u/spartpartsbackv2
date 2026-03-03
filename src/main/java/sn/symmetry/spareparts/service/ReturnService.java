package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.request.CreateReturnRequest;
import sn.symmetry.spareparts.dto.request.UpdateReturnRequest;
import sn.symmetry.spareparts.dto.request.UpdateReturnStatusRequest;
import sn.symmetry.spareparts.dto.response.ReturnResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.enums.ReturnStatus;

public interface ReturnService {

    PagedResponse<ReturnResponse> getAllReturns(Long customerId, ReturnStatus status, Pageable pageable);

    ReturnResponse getReturnById(Long id);

    ReturnResponse createReturn(CreateReturnRequest request);

    ReturnResponse updateReturn(Long id, UpdateReturnRequest request);

    ReturnResponse updateReturnStatus(Long id, UpdateReturnStatusRequest request);

    void deleteReturn(Long id);
}
