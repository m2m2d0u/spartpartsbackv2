package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.request.CreateTagRequest;
import sn.symmetry.spareparts.dto.request.UpdateTagRequest;
import sn.symmetry.spareparts.dto.response.TagResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;

import java.util.UUID;

public interface TagService {

    PagedResponse<TagResponse> getAllTags(Pageable pageable);

    TagResponse getTagById(UUID id);

    TagResponse createTag(CreateTagRequest request);

    TagResponse updateTag(UUID id, UpdateTagRequest request);

    void deleteTag(UUID id);
}
