package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.symmetry.spareparts.dto.request.AddCartItemRequest;
import sn.symmetry.spareparts.dto.request.UpdateCartItemRequest;
import sn.symmetry.spareparts.dto.response.CartResponse;
import sn.symmetry.spareparts.entity.Cart;
import sn.symmetry.spareparts.entity.CartItem;
import sn.symmetry.spareparts.entity.Customer;
import sn.symmetry.spareparts.entity.Part;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.mapper.CartMapper;
import sn.symmetry.spareparts.repository.CartItemRepository;
import sn.symmetry.spareparts.repository.CartRepository;
import sn.symmetry.spareparts.repository.CustomerRepository;
import sn.symmetry.spareparts.repository.PartRepository;
import sn.symmetry.spareparts.service.CartService;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final PartRepository partRepository;
    private final CartMapper cartMapper;

    @Override
    public CartResponse getCart(UUID customerId) {
        Cart cart = findOrCreateCart(customerId);
        return cartMapper.toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addItem(UUID customerId, AddCartItemRequest request) {
        Cart cart = findOrCreateCart(customerId);

        Part part = partRepository.findById(request.getPartId())
                .orElseThrow(() -> new ResourceNotFoundException("Part", "id", request.getPartId()));

        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndPartId(cart.getId(), part.getId());

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setPart(part);
            newItem.setQuantity(request.getQuantity());
            cart.getItems().add(newItem);
            cartRepository.save(cart);
        }

        Cart updatedCart = cartRepository.findById(cart.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cart.getId()));
        return cartMapper.toResponse(updatedCart);
    }

    @Override
    @Transactional
    public CartResponse updateItem(UUID customerId, UUID itemId, UpdateCartItemRequest request) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "customerId", customerId));

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new ResourceNotFoundException("CartItem", "id", itemId);
        }

        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        Cart updatedCart = cartRepository.findById(cart.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cart.getId()));
        return cartMapper.toResponse(updatedCart);
    }

    @Override
    @Transactional
    public CartResponse removeItem(UUID customerId, UUID itemId) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "customerId", customerId));

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new ResourceNotFoundException("CartItem", "id", itemId);
        }

        cart.getItems().remove(item);
        cartRepository.save(cart);

        Cart updatedCart = cartRepository.findById(cart.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cart.getId()));
        return cartMapper.toResponse(updatedCart);
    }

    @Override
    @Transactional
    public void clearCart(UUID customerId) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "customerId", customerId));

        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private Cart findOrCreateCart(UUID customerId) {
        return cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    Customer customer = customerRepository.findById(customerId)
                            .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
                    Cart cart = new Cart();
                    cart.setCustomer(customer);
                    return cartRepository.save(cart);
                });
    }
}
