package sn.symmetry.spareparts.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sn.symmetry.spareparts.config.MapStructConfig;
import sn.symmetry.spareparts.dto.response.StockTransferItemResponse;
import sn.symmetry.spareparts.dto.response.StockTransferResponse;
import sn.symmetry.spareparts.entity.StockTransfer;
import sn.symmetry.spareparts.entity.StockTransferItem;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface StockTransferMapper {

    @Mapping(source = "sourceWarehouse.id", target = "sourceWarehouseId")
    @Mapping(source = "sourceWarehouse.name", target = "sourceWarehouseName")
    @Mapping(source = "destinationWarehouse.id", target = "destinationWarehouseId")
    @Mapping(source = "destinationWarehouse.name", target = "destinationWarehouseName")
    StockTransferResponse toResponse(StockTransfer stockTransfer);

    @Mapping(source = "part.id", target = "partId")
    @Mapping(source = "part.name", target = "partName")
    @Mapping(source = "part.partNumber", target = "partNumber")
    StockTransferItemResponse toItemResponse(StockTransferItem item);

    List<StockTransferItemResponse> toItemResponseList(List<StockTransferItem> items);
}
