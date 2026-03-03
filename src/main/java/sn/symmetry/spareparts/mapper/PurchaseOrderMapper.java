package sn.symmetry.spareparts.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sn.symmetry.spareparts.config.MapStructConfig;
import sn.symmetry.spareparts.dto.response.PurchaseOrderItemResponse;
import sn.symmetry.spareparts.dto.response.PurchaseOrderResponse;
import sn.symmetry.spareparts.entity.PurchaseOrder;
import sn.symmetry.spareparts.entity.PurchaseOrderItem;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface PurchaseOrderMapper {

    @Mapping(source = "supplier.id", target = "supplierId")
    @Mapping(source = "supplier.name", target = "supplierName")
    @Mapping(source = "destinationWarehouse.id", target = "destinationWarehouseId")
    @Mapping(source = "destinationWarehouse.name", target = "destinationWarehouseName")
    PurchaseOrderResponse toResponse(PurchaseOrder purchaseOrder);

    @Mapping(source = "part.id", target = "partId")
    @Mapping(source = "part.name", target = "partName")
    @Mapping(source = "part.partNumber", target = "partNumber")
    PurchaseOrderItemResponse toItemResponse(PurchaseOrderItem item);

    List<PurchaseOrderItemResponse> toItemResponseList(List<PurchaseOrderItem> items);
}
