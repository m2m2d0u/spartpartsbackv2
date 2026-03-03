package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.request.CreatePurchaseOrderRequest;
import sn.symmetry.spareparts.dto.request.UpdatePurchaseOrderRequest;
import sn.symmetry.spareparts.dto.response.PurchaseOrderResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.enums.PurchaseOrderStatus;

public interface PurchaseOrderService {

    PagedResponse<PurchaseOrderResponse> getAllPurchaseOrders(Long supplierId, PurchaseOrderStatus status, Pageable pageable);

    PurchaseOrderResponse getPurchaseOrderById(Long id);

    PurchaseOrderResponse createPurchaseOrder(CreatePurchaseOrderRequest request);

    PurchaseOrderResponse updatePurchaseOrder(Long id, UpdatePurchaseOrderRequest request);

    void deletePurchaseOrder(Long id);
}
