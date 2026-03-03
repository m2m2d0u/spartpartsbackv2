package sn.symmetry.spareparts.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sn.symmetry.spareparts.config.MapStructConfig;
import sn.symmetry.spareparts.dto.request.CreatePartRequest;
import sn.symmetry.spareparts.dto.request.UpdatePartRequest;
import sn.symmetry.spareparts.dto.response.PartImageResponse;
import sn.symmetry.spareparts.dto.response.PartResponse;
import sn.symmetry.spareparts.entity.Part;
import sn.symmetry.spareparts.entity.PartImage;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface PartMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Part toEntity(CreatePartRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdatePartRequest request, @MappingTarget Part part);

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "images", target = "images")
    PartResponse toResponse(Part part);

    @Mapping(target = "images", ignore = true)
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    PartResponse toListResponse(Part part);

    PartImageResponse toPartImageResponse(PartImage partImage);

    List<PartImageResponse> toPartImageResponseList(List<PartImage> partImages);
}
