package sn.symmetry.spareparts.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sn.symmetry.spareparts.config.MapStructConfig;
import sn.symmetry.spareparts.dto.request.CreateCategoryRequest;
import sn.symmetry.spareparts.dto.request.UpdateCategoryRequest;
import sn.symmetry.spareparts.dto.response.CategoryResponse;
import sn.symmetry.spareparts.entity.Category;

@Mapper(config = MapStructConfig.class)
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "parts", ignore = true)
    Category toEntity(CreateCategoryRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "parts", ignore = true)
    void updateEntity(UpdateCategoryRequest request, @MappingTarget Category category);

    CategoryResponse toResponse(Category category);
}
