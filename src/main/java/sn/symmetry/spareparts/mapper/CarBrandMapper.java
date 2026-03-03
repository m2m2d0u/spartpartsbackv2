package sn.symmetry.spareparts.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sn.symmetry.spareparts.config.MapStructConfig;
import sn.symmetry.spareparts.dto.request.CreateCarBrandRequest;
import sn.symmetry.spareparts.dto.request.UpdateCarBrandRequest;
import sn.symmetry.spareparts.dto.response.CarBrandResponse;
import sn.symmetry.spareparts.entity.CarBrand;

@Mapper(config = MapStructConfig.class)
public interface CarBrandMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "models", ignore = true)
    CarBrand toEntity(CreateCarBrandRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "models", ignore = true)
    void updateEntity(UpdateCarBrandRequest request, @MappingTarget CarBrand carBrand);

    CarBrandResponse toResponse(CarBrand carBrand);
}
