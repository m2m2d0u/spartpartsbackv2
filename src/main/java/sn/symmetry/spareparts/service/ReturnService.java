package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.request.CreateReturnRequest;
import sn.symmetry.spareparts.dto.request.UpdateReturnRequest;
import sn.symmetry.spareparts.dto.request.UpdateReturnStatusRequest;
import sn.symmetry.spareparts.dto.response.ReturnResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.enums.ReturnStatus;

import java.util.UUID;

public interface ReturnService {

    PagedResponse<ReturnResponse> getAllReturns(UUID customerId, ReturnStatus status, Pageable pageable);

    ReturnResponse getReturnById(UUID id);

    ReturnResponse createReturn(CreateReturnRequest request);

    ReturnResponse updateReturn(UUID id, UpdateReturnRequest request);

    ReturnResponse updateReturnStatus(UUID id, UpdateReturnStatusRequest request);

    void deleteReturn(UUID id);
}
