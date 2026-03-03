package sn.symmetry.spareparts.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sn.symmetry.spareparts.config.MapStructConfig;
import sn.symmetry.spareparts.dto.request.CreateTagRequest;
import sn.symmetry.spareparts.dto.request.UpdateTagRequest;
import sn.symmetry.spareparts.dto.response.TagResponse;
import sn.symmetry.spareparts.entity.Tag;

@Mapper(config = MapStructConfig.class)
public interface TagMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "parts", ignore = true)
    Tag toEntity(CreateTagRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "parts", ignore = true)
    void updateEntity(UpdateTagRequest request, @MappingTarget Tag tag);

    TagResponse toResponse(Tag tag);
}
