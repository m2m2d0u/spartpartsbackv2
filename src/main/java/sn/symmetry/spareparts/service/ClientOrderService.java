package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.request.CreateClientOrderRequest;
import sn.symmetry.spareparts.dto.request.UpdateClientOrderRequest;
import sn.symmetry.spareparts.dto.request.UpdateOrderStatusRequest;
import sn.symmetry.spareparts.dto.response.ClientOrderResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.enums.OrderStatus;

public interface ClientOrderService {

    PagedResponse<ClientOrderResponse> getAllOrders(Long customerId, OrderStatus status, Pageable pageable);

    ClientOrderResponse getOrderById(Long id);

    ClientOrderResponse createOrder(CreateClientOrderRequest request);

    ClientOrderResponse updateOrder(Long id, UpdateClientOrderRequest request);

    ClientOrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request);

    void deleteOrder(Long id);
}
