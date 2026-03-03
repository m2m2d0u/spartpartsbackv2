package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.request.CreateCarModelRequest;
import sn.symmetry.spareparts.dto.request.UpdateCarModelRequest;
import sn.symmetry.spareparts.dto.response.CarModelResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;

import java.util.UUID;

public interface CarModelService {

    PagedResponse<CarModelResponse> getAllCarModels(UUID brandId, Pageable pageable);

    CarModelResponse getCarModelById(UUID id);

    CarModelResponse createCarModel(CreateCarModelRequest request);

    CarModelResponse updateCarModel(UUID id, UpdateCarModelRequest request);

    void deleteCarModel(UUID id);
}
