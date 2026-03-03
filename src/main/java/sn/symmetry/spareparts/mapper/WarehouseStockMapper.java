package sn.symmetry.spareparts.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sn.symmetry.spareparts.config.MapStructConfig;
import sn.symmetry.spareparts.dto.response.WarehouseStockResponse;
import sn.symmetry.spareparts.entity.WarehouseStock;

@Mapper(config = MapStructConfig.class)
public interface WarehouseStockMapper {

    @Mapping(source = "warehouse.id", target = "warehouseId")
    @Mapping(source = "warehouse.name", target = "warehouseName")
    @Mapping(source = "warehouse.code", target = "warehouseCode")
    @Mapping(source = "part.id", target = "partId")
    @Mapping(source = "part.name", target = "partName")
    @Mapping(source = "part.partNumber", target = "partNumber")
    WarehouseStockResponse toResponse(WarehouseStock warehouseStock);
}
