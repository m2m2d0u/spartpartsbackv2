package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.request.CreatePurchaseOrderRequest;
import sn.symmetry.spareparts.dto.request.UpdatePurchaseOrderRequest;
import sn.symmetry.spareparts.dto.response.PurchaseOrderResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.enums.PurchaseOrderStatus;

import java.util.UUID;

public interface PurchaseOrderService {

    PagedResponse<PurchaseOrderResponse> getAllPurchaseOrders(UUID supplierId, PurchaseOrderStatus status, Pageable pageable);

    PurchaseOrderResponse getPurchaseOrderById(UUID id);

    PurchaseOrderResponse createPurchaseOrder(CreatePurchaseOrderRequest request);

    PurchaseOrderResponse updatePurchaseOrder(UUID id, UpdatePurchaseOrderRequest request);

    void deletePurchaseOrder(UUID id);
}
