package sn.symmetry.spareparts.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sn.symmetry.spareparts.dto.request.CreateClientOrderRequest;
import sn.symmetry.spareparts.dto.request.UpdateClientOrderRequest;
import sn.symmetry.spareparts.dto.request.UpdateOrderStatusRequest;
import sn.symmetry.spareparts.dto.response.ClientOrderResponse;
import sn.symmetry.spareparts.dto.response.common.ApiResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.enums.OrderStatus;
import sn.symmetry.spareparts.service.ClientOrderService;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class ClientOrderController {

    private final ClientOrderService clientOrderService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ClientOrderResponse>>> getAllOrders(
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) OrderStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(clientOrderService.getAllOrders(customerId, status, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientOrderResponse>> getOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(clientOrderService.getOrderById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ClientOrderResponse>> createOrder(
            @Valid @RequestBody CreateClientOrderRequest request) {
        ClientOrderResponse response = clientOrderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientOrderResponse>> updateOrder(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateClientOrderRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Order updated successfully",
                clientOrderService.updateOrder(id, request)));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ClientOrderResponse>> updateOrderStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Order status updated successfully",
                clientOrderService.updateOrderStatus(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable UUID id) {
        clientOrderService.deleteOrder(id);
        return ResponseEntity.ok(ApiResponse.success("Order deleted successfully", null));
    }
}
