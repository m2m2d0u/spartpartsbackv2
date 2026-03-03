package sn.symmetry.spareparts.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sn.symmetry.spareparts.config.MapStructConfig;
import sn.symmetry.spareparts.dto.response.ReturnItemResponse;
import sn.symmetry.spareparts.dto.response.ReturnResponse;
import sn.symmetry.spareparts.entity.Return;
import sn.symmetry.spareparts.entity.ReturnItem;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface ReturnMapper {

    @Mapping(source = "invoice.id", target = "invoiceId")
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "items", target = "items")
    ReturnResponse toResponse(Return returnEntity);

    @Mapping(source = "part.id", target = "partId")
    @Mapping(source = "part.name", target = "partName")
    @Mapping(source = "part.partNumber", target = "partNumber")
    @Mapping(source = "warehouse.id", target = "warehouseId")
    @Mapping(source = "warehouse.name", target = "warehouseName")
    ReturnItemResponse toItemResponse(ReturnItem item);

    List<ReturnItemResponse> toItemResponseList(List<ReturnItem> items);
}
