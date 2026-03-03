package sn.symmetry.spareparts.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sn.symmetry.spareparts.config.MapStructConfig;
import sn.symmetry.spareparts.dto.request.CreatePartRequest;
import sn.symmetry.spareparts.dto.request.UpdatePartRequest;
import sn.symmetry.spareparts.dto.response.PartImageResponse;
import sn.symmetry.spareparts.dto.response.PartResponse;
import sn.symmetry.spareparts.dto.response.TagResponse;
import sn.symmetry.spareparts.entity.Part;
import sn.symmetry.spareparts.entity.PartImage;
import sn.symmetry.spareparts.entity.Tag;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface PartMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "carBrand", ignore = true)
    @Mapping(target = "carModel", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Part toEntity(CreatePartRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "carBrand", ignore = true)
    @Mapping(target = "carModel", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdatePartRequest request, @MappingTarget Part part);

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "carBrand.id", target = "carBrandId")
    @Mapping(source = "carBrand.name", target = "carBrandName")
    @Mapping(source = "carModel.id", target = "carModelId")
    @Mapping(source = "carModel.name", target = "carModelName")
    @Mapping(source = "images", target = "images")
    @Mapping(source = "tags", target = "tags")
    PartResponse toResponse(Part part);

    @Mapping(target = "images", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "carBrand.id", target = "carBrandId")
    @Mapping(source = "carBrand.name", target = "carBrandName")
    @Mapping(source = "carModel.id", target = "carModelId")
    @Mapping(source = "carModel.name", target = "carModelName")
    PartResponse toListResponse(Part part);

    PartImageResponse toPartImageResponse(PartImage partImage);

    List<PartImageResponse> toPartImageResponseList(List<PartImage> partImages);

    TagResponse toTagResponse(Tag tag);

    List<TagResponse> toTagResponseList(List<Tag> tags);
}
