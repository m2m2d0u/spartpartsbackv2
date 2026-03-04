package sn.symmetry.spareparts.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import sn.symmetry.spareparts.config.MapStructConfig;
import sn.symmetry.spareparts.dto.request.UpdateCompanySettingsRequest;
import sn.symmetry.spareparts.dto.response.CompanySettingsResponse;
import sn.symmetry.spareparts.entity.CompanySettings;

@Mapper(config = MapStructConfig.class)
public interface CompanySettingsMapper {

    @Mapping(source = "defaultTemplate.id", target = "defaultTemplateId")
    @Mapping(source = "defaultWarehouse.id", target = "defaultWarehouseId")
    @Mapping(source = "portalWarehouse.id", target = "portalWarehouseId")
    CompanySettingsResponse toResponse(CompanySettings companySettings);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "defaultTemplate", ignore = true)
    @Mapping(target = "defaultWarehouse", ignore = true)
    @Mapping(target = "portalWarehouse", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateCompanySettingsRequest request, @MappingTarget CompanySettings companySettings);
}
