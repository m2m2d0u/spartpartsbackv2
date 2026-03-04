package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.request.CreatePartImageRequest;
import sn.symmetry.spareparts.dto.request.CreatePartRequest;
import sn.symmetry.spareparts.dto.request.UpdatePartRequest;
import sn.symmetry.spareparts.dto.response.PartImageResponse;
import sn.symmetry.spareparts.dto.response.PartResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;

import java.util.UUID;

public interface PartService {

    PagedResponse<PartResponse> getAllParts(UUID categoryId, Boolean published, UUID carBrandId, UUID carModelId, Pageable pageable);

    PagedResponse<PartResponse> getPartsNotInWarehouse(UUID warehouseId, String name, Pageable pageable);

    PagedResponse<PartResponse> getPartsInWarehouse(UUID warehouseId, String name, Pageable pageable);

    PartResponse getPartById(UUID id);

    PartResponse createPart(CreatePartRequest request);

    PartResponse updatePart(UUID id, UpdatePartRequest request);

    void deletePart(UUID id);

    PartImageResponse addImageToPart(UUID partId, CreatePartImageRequest request);

    void removeImageFromPart(UUID partId, UUID imageId);
}
