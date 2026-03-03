package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.symmetry.spareparts.dto.request.CreatePartImageRequest;
import sn.symmetry.spareparts.dto.request.CreatePartRequest;
import sn.symmetry.spareparts.dto.request.UpdatePartRequest;
import sn.symmetry.spareparts.dto.response.PartImageResponse;
import sn.symmetry.spareparts.dto.response.PartResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.entity.Category;
import sn.symmetry.spareparts.entity.Part;
import sn.symmetry.spareparts.entity.PartImage;
import sn.symmetry.spareparts.exception.DuplicateResourceException;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.mapper.PartMapper;
import sn.symmetry.spareparts.repository.CategoryRepository;
import sn.symmetry.spareparts.repository.PartImageRepository;
import sn.symmetry.spareparts.repository.PartRepository;
import sn.symmetry.spareparts.service.PartService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartServiceImpl implements PartService {

    private final PartRepository partRepository;
    private final PartImageRepository partImageRepository;
    private final CategoryRepository categoryRepository;
    private final PartMapper partMapper;

    @Override
    public PagedResponse<PartResponse> getAllParts(Long categoryId, Boolean published, Pageable pageable) {
        Page<Part> page;
        if (categoryId != null && published != null) {
            page = partRepository.findByCategoryIdAndPublished(categoryId, published, pageable);
        } else if (categoryId != null) {
            page = partRepository.findByCategoryId(categoryId, pageable);
        } else if (published != null) {
            page = partRepository.findByPublished(published, pageable);
        } else {
            page = partRepository.findAll(pageable);
        }
        return PagedResponse.of(page.map(partMapper::toListResponse));
    }

    @Override
    public PartResponse getPartById(Long id) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Part", "id", id));
        return partMapper.toResponse(part);
    }

    @Override
    @Transactional
    public PartResponse createPart(CreatePartRequest request) {
        if (partRepository.existsByPartNumber(request.getPartNumber())) {
            throw new DuplicateResourceException("Part", "partNumber", request.getPartNumber());
        }

        Part part = partMapper.toEntity(request);

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            part.setCategory(category);
        }

        Part saved = partRepository.save(part);
        return partMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PartResponse updatePart(Long id, UpdatePartRequest request) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Part", "id", id));

        if (partRepository.existsByPartNumberAndIdNot(request.getPartNumber(), id)) {
            throw new DuplicateResourceException("Part", "partNumber", request.getPartNumber());
        }

        partMapper.updateEntity(request, part);

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            part.setCategory(category);
        } else {
            part.setCategory(null);
        }

        Part saved = partRepository.save(part);
        return partMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deletePart(Long id) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Part", "id", id));
        partRepository.delete(part);
    }

    @Override
    @Transactional
    public PartImageResponse addImageToPart(Long partId, CreatePartImageRequest request) {
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new ResourceNotFoundException("Part", "id", partId));

        PartImage partImage = new PartImage();
        partImage.setPart(part);
        partImage.setUrl(request.getUrl());
        partImage.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);

        part.getImages().add(partImage);
        partRepository.save(part);

        return partMapper.toPartImageResponse(partImage);
    }

    @Override
    @Transactional
    public void removeImageFromPart(Long partId, Long imageId) {
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new ResourceNotFoundException("Part", "id", partId));

        PartImage partImage = part.getImages().stream()
                .filter(img -> img.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("PartImage", "id", imageId));

        part.getImages().remove(partImage);
        partRepository.save(part);
    }
}
