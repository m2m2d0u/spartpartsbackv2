package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.request.CreateCarBrandRequest;
import sn.symmetry.spareparts.dto.request.UpdateCarBrandRequest;
import sn.symmetry.spareparts.dto.response.CarBrandResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;

import java.util.UUID;

public interface CarBrandService {

    PagedResponse<CarBrandResponse> getAllCarBrands(Pageable pageable);

    CarBrandResponse getCarBrandById(UUID id);

    CarBrandResponse createCarBrand(CreateCarBrandRequest request);

    CarBrandResponse updateCarBrand(UUID id, UpdateCarBrandRequest request);

    void deleteCarBrand(UUID id);
}
