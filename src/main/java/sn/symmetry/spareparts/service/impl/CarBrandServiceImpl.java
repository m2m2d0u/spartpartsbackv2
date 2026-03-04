package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.symmetry.spareparts.dto.request.CreateCarBrandRequest;
import sn.symmetry.spareparts.dto.request.UpdateCarBrandRequest;
import sn.symmetry.spareparts.dto.response.CarBrandResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.entity.CarBrand;
import sn.symmetry.spareparts.exception.DuplicateResourceException;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.mapper.CarBrandMapper;
import sn.symmetry.spareparts.repository.CarBrandRepository;
import sn.symmetry.spareparts.service.CarBrandService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CarBrandServiceImpl implements CarBrandService {

    private final CarBrandRepository carBrandRepository;
    private final CarBrandMapper carBrandMapper;

    @Override
    public PagedResponse<CarBrandResponse> getAllCarBrands(Pageable pageable) {
        Page<CarBrand> page = carBrandRepository.findAll(pageable);
        return PagedResponse.of(page.map(carBrandMapper::toResponse));
    }

    @Override
    @Cacheable(value = "carBrands", key = "'all'")
    public List<CarBrandResponse> getAllCarBrandsList() {
        return carBrandRepository.findAll().stream()
                .map(carBrandMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CarBrandResponse getCarBrandById(UUID id) {
        CarBrand carBrand = carBrandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CarBrand", "id", id));
        return carBrandMapper.toResponse(carBrand);
    }

    @Override
    @Transactional
    @CacheEvict(value = "carBrands", key = "'all'")
    public CarBrandResponse createCarBrand(CreateCarBrandRequest request) {
        if (carBrandRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("CarBrand", "name", request.getName());
        }

        CarBrand carBrand = carBrandMapper.toEntity(request);
        CarBrand saved = carBrandRepository.save(carBrand);
        return carBrandMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "carBrands", key = "'all'")
    public CarBrandResponse updateCarBrand(UUID id, UpdateCarBrandRequest request) {
        CarBrand carBrand = carBrandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CarBrand", "id", id));

        if (carBrandRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new DuplicateResourceException("CarBrand", "name", request.getName());
        }

        carBrandMapper.updateEntity(request, carBrand);
        CarBrand saved = carBrandRepository.save(carBrand);
        return carBrandMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "carBrands", key = "'all'")
    public void deleteCarBrand(UUID id) {
        CarBrand carBrand = carBrandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CarBrand", "id", id));
        carBrandRepository.delete(carBrand);
    }
}
