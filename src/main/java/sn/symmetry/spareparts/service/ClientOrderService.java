package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.request.CreateClientOrderRequest;
import sn.symmetry.spareparts.dto.request.UpdateClientOrderRequest;
import sn.symmetry.spareparts.dto.request.UpdateOrderStatusRequest;
import sn.symmetry.spareparts.dto.response.ClientOrderResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.enums.OrderStatus;

import java.util.UUID;

public interface ClientOrderService {

    PagedResponse<ClientOrderResponse> getAllOrders(UUID customerId, OrderStatus status, Pageable pageable);

    ClientOrderResponse getOrderById(UUID id);

    ClientOrderResponse createOrder(CreateClientOrderRequest request);

    ClientOrderResponse updateOrder(UUID id, UpdateClientOrderRequest request);

    ClientOrderResponse updateOrderStatus(UUID id, UpdateOrderStatusRequest request);

    void deleteOrder(UUID id);
}
