package sn.symmetry.spareparts.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sn.symmetry.spareparts.config.MapStructConfig;
import sn.symmetry.spareparts.dto.request.UpdateClientOrderRequest;
import sn.symmetry.spareparts.dto.response.ClientOrderResponse;
import sn.symmetry.spareparts.dto.response.OrderItemResponse;
import sn.symmetry.spareparts.entity.ClientOrder;
import sn.symmetry.spareparts.entity.OrderItem;

@Mapper(config = MapStructConfig.class)
public interface ClientOrderMapper {

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", source = "customer.name")
    @Mapping(target = "warehouseId", source = "warehouse.id")
    @Mapping(target = "warehouseName", source = "warehouse.name")
    ClientOrderResponse toResponse(ClientOrder clientOrder);

    @Mapping(target = "partId", source = "part.id")
    @Mapping(target = "partName", source = "part.name")
    @Mapping(target = "partNumber", source = "part.partNumber")
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "warehouse", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "orderNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "taxAmount", ignore = true)
    @Mapping(target = "discountAmount", ignore = true)
    @Mapping(target = "shippingAmount", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "trackingNumber", ignore = true)
    @Mapping(target = "orderDate", ignore = true)
    void updateEntity(UpdateClientOrderRequest request, @MappingTarget ClientOrder clientOrder);
}
