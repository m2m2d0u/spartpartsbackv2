package sn.symmetry.spareparts.service;

import sn.symmetry.spareparts.dto.request.AddCartItemRequest;
import sn.symmetry.spareparts.dto.request.UpdateCartItemRequest;
import sn.symmetry.spareparts.dto.response.CartResponse;

import java.util.UUID;

public interface CartService {

    CartResponse getCart(UUID customerId);

    CartResponse addItem(UUID customerId, AddCartItemRequest request);

    CartResponse updateItem(UUID customerId, UUID itemId, UpdateCartItemRequest request);

    CartResponse removeItem(UUID customerId, UUID itemId);

    void clearCart(UUID customerId);
}
