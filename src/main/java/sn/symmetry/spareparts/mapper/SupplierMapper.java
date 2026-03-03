package sn.symmetry.spareparts.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sn.symmetry.spareparts.config.MapStructConfig;
import sn.symmetry.spareparts.dto.request.CreateSupplierRequest;
import sn.symmetry.spareparts.dto.request.UpdateSupplierRequest;
import sn.symmetry.spareparts.dto.response.SupplierResponse;
import sn.symmetry.spareparts.entity.Supplier;

@Mapper(config = MapStructConfig.class)
public interface SupplierMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Supplier toEntity(CreateSupplierRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateSupplierRequest request, @MappingTarget Supplier supplier);

    SupplierResponse toResponse(Supplier supplier);
}
