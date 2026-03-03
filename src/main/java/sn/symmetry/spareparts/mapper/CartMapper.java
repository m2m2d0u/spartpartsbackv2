package sn.symmetry.spareparts.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sn.symmetry.spareparts.config.MapStructConfig;
import sn.symmetry.spareparts.dto.response.CartItemResponse;
import sn.symmetry.spareparts.dto.response.CartResponse;
import sn.symmetry.spareparts.entity.Cart;
import sn.symmetry.spareparts.entity.CartItem;

import java.math.BigDecimal;

@Mapper(config = MapStructConfig.class)
public interface CartMapper {

    @Mapping(target = "customerId", source = "customer.id")
    CartResponse toResponse(Cart cart);

    @Mapping(target = "partId", source = "part.id")
    @Mapping(target = "partName", source = "part.name")
    @Mapping(target = "partNumber", source = "part.partNumber")
    @Mapping(target = "unitPrice", source = "part.sellingPrice")
    @Mapping(target = "lineTotal", expression = "java(computeLineTotal(cartItem))")
    CartItemResponse toCartItemResponse(CartItem cartItem);

    default BigDecimal computeLineTotal(CartItem cartItem) {
        if (cartItem.getPart() != null && cartItem.getPart().getSellingPrice() != null && cartItem.getQuantity() != null) {
            return cartItem.getPart().getSellingPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
        }
        return BigDecimal.ZERO;
    }
}
