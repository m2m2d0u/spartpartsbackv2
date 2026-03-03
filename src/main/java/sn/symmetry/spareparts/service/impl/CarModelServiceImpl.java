package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.symmetry.spareparts.dto.request.CreateCarModelRequest;
import sn.symmetry.spareparts.dto.request.UpdateCarModelRequest;
import sn.symmetry.spareparts.dto.response.CarModelResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.entity.CarBrand;
import sn.symmetry.spareparts.entity.CarModel;
import sn.symmetry.spareparts.exception.DuplicateResourceException;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.mapper.CarModelMapper;
import sn.symmetry.spareparts.repository.CarBrandRepository;
import sn.symmetry.spareparts.repository.CarModelRepository;
import sn.symmetry.spareparts.service.CarModelService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CarModelServiceImpl implements CarModelService {

    private final CarModelRepository carModelRepository;
    private final CarBrandRepository carBrandRepository;
    private final CarModelMapper carModelMapper;

    @Override
    public PagedResponse<CarModelResponse> getAllCarModels(UUID brandId, Pageable pageable) {
        Page<CarModel> page;
        if (brandId != null) {
            page = carModelRepository.findByBrandId(brandId, pageable);
        } else {
            page = carModelRepository.findAll(pageable);
        }
        return PagedResponse.of(page.map(carModelMapper::toResponse));
    }

    @Override
    @Cacheable(value = "carModels", key = "#brandId != null ? #brandId : 'all'")
    public List<CarModelResponse> getAllCarModelsList(UUID brandId) {
        List<CarModel> models;
        if (brandId != null) {
            models = carModelRepository.findByBrandId(brandId);
        } else {
            models = carModelRepository.findAll();
        }
        return models.stream()
                .map(carModelMapper::toResponse)
                .toList();
    }

    @Override
    public CarModelResponse getCarModelById(UUID id) {
        CarModel carModel = carModelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CarModel", "id", id));
        return carModelMapper.toResponse(carModel);
    }

    @Override
    @Transactional
    @CacheEvict(value = "carModels", allEntries = true)
    public CarModelResponse createCarModel(CreateCarModelRequest request) {
        CarBrand brand = carBrandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("CarBrand", "id", request.getBrandId()));

        if (carModelRepository.existsByNameAndBrandId(request.getName(), request.getBrandId())) {
            throw new DuplicateResourceException("CarModel", "name", request.getName());
        }

        CarModel carModel = carModelMapper.toEntity(request);
        carModel.setBrand(brand);
        CarModel saved = carModelRepository.save(carModel);
        return carModelMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "carModels", allEntries = true)
    public CarModelResponse updateCarModel(UUID id, UpdateCarModelRequest request) {
        CarModel carModel = carModelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CarModel", "id", id));

        CarBrand brand = carBrandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("CarBrand", "id", request.getBrandId()));

        if (carModelRepository.existsByNameAndBrandIdAndIdNot(request.getName(), request.getBrandId(), id)) {
            throw new DuplicateResourceException("CarModel", "name", request.getName());
        }

        carModelMapper.updateEntity(request, carModel);
        carModel.setBrand(brand);
        CarModel saved = carModelRepository.save(carModel);
        return carModelMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "carModels", allEntries = true)
    public void deleteCarModel(UUID id) {
        CarModel carModel = carModelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CarModel", "id", id));
        carModelRepository.delete(carModel);
    }
}
