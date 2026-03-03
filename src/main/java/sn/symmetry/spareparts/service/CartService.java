package sn.symmetry.spareparts.service;

import sn.symmetry.spareparts.dto.request.AddCartItemRequest;
import sn.symmetry.spareparts.dto.request.UpdateCartItemRequest;
import sn.symmetry.spareparts.dto.response.CartResponse;

public interface CartService {

    CartResponse getCart(Long customerId);

    CartResponse addItem(Long customerId, AddCartItemRequest request);

    CartResponse updateItem(Long customerId, Long itemId, UpdateCartItemRequest request);

    CartResponse removeItem(Long customerId, Long itemId);

    void clearCart(Long customerId);
}
