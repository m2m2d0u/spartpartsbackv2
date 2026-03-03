package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.request.CreateStockTransferRequest;
import sn.symmetry.spareparts.dto.request.UpdateStockTransferRequest;
import sn.symmetry.spareparts.dto.response.StockTransferResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.enums.StockTransferStatus;

public interface StockTransferService {

    PagedResponse<StockTransferResponse> getAllStockTransfers(StockTransferStatus status, Pageable pageable);

    StockTransferResponse getStockTransferById(Long id);

    StockTransferResponse createStockTransfer(CreateStockTransferRequest request);

    StockTransferResponse updateStockTransfer(Long id, UpdateStockTransferRequest request);

    void deleteStockTransfer(Long id);
}
