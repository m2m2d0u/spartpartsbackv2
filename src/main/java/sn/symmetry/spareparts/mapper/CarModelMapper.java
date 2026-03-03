package sn.symmetry.spareparts.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sn.symmetry.spareparts.config.MapStructConfig;
import sn.symmetry.spareparts.dto.request.CreateCarModelRequest;
import sn.symmetry.spareparts.dto.request.UpdateCarModelRequest;
import sn.symmetry.spareparts.dto.response.CarModelResponse;
import sn.symmetry.spareparts.entity.CarModel;

@Mapper(config = MapStructConfig.class)
public interface CarModelMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CarModel toEntity(CreateCarModelRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateCarModelRequest request, @MappingTarget CarModel carModel);

    @Mapping(source = "brand.id", target = "brandId")
    @Mapping(source = "brand.name", target = "brandName")
    CarModelResponse toResponse(CarModel carModel);
}
