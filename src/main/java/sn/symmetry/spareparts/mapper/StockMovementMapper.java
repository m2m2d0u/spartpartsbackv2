package sn.symmetry.spareparts.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sn.symmetry.spareparts.config.MapStructConfig;
import sn.symmetry.spareparts.dto.response.StockMovementResponse;
import sn.symmetry.spareparts.entity.StockMovement;

@Mapper(config = MapStructConfig.class)
public interface StockMovementMapper {

    @Mapping(source = "part.id", target = "partId")
    @Mapping(source = "part.name", target = "partName")
    @Mapping(source = "part.partNumber", target = "partNumber")
    @Mapping(source = "warehouse.id", target = "warehouseId")
    @Mapping(source = "warehouse.name", target = "warehouseName")
    StockMovementResponse toResponse(StockMovement stockMovement);
}
