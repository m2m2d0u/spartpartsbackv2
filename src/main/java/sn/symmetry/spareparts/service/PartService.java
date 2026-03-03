package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.request.CreatePartImageRequest;
import sn.symmetry.spareparts.dto.request.CreatePartRequest;
import sn.symmetry.spareparts.dto.request.UpdatePartRequest;
import sn.symmetry.spareparts.dto.response.PartImageResponse;
import sn.symmetry.spareparts.dto.response.PartResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;

public interface PartService {

    PagedResponse<PartResponse> getAllParts(Long categoryId, Boolean published, Pageable pageable);

    PartResponse getPartById(Long id);

    PartResponse createPart(CreatePartRequest request);

    PartResponse updatePart(Long id, UpdatePartRequest request);

    void deletePart(Long id);

    PartImageResponse addImageToPart(Long partId, CreatePartImageRequest request);

    void removeImageFromPart(Long partId, Long imageId);
}
