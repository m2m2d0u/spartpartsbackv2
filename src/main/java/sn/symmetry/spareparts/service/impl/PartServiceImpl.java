package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sn.symmetry.spareparts.dto.request.CreatePartImageRequest;
import sn.symmetry.spareparts.dto.request.CreatePartRequest;
import sn.symmetry.spareparts.dto.request.UpdatePartRequest;
import sn.symmetry.spareparts.dto.response.PartImageResponse;
import sn.symmetry.spareparts.dto.response.PartResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.entity.CarBrand;
import sn.symmetry.spareparts.entity.CarModel;
import sn.symmetry.spareparts.entity.Category;
import sn.symmetry.spareparts.entity.Part;
import sn.symmetry.spareparts.entity.PartImage;
import sn.symmetry.spareparts.entity.Tag;
import sn.symmetry.spareparts.exception.DuplicateResourceException;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.mapper.PartMapper;
import sn.symmetry.spareparts.repository.CarBrandRepository;
import sn.symmetry.spareparts.repository.CarModelRepository;
import sn.symmetry.spareparts.repository.CategoryRepository;
import sn.symmetry.spareparts.repository.PartImageRepository;
import sn.symmetry.spareparts.repository.PartRepository;
import sn.symmetry.spareparts.repository.TagRepository;
import sn.symmetry.spareparts.service.FileStorageService;
import sn.symmetry.spareparts.service.PartService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartServiceImpl implements PartService {

    private final PartRepository partRepository;
    private final PartImageRepository partImageRepository;
    private final CategoryRepository categoryRepository;
    private final CarBrandRepository carBrandRepository;
    private final CarModelRepository carModelRepository;
    private final TagRepository tagRepository;
    private final PartMapper partMapper;
    private final FileStorageService fileStorageService;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final int MAX_FILES_PER_UPLOAD = 10;
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/webp", "image/gif"
    );

    @Override
    public PagedResponse<PartResponse> getAllParts(String name, UUID categoryId, Boolean published, UUID carBrandId, UUID carModelId, Pageable pageable) {
        Page<Part> page = partRepository.searchParts(name, categoryId, published, carBrandId, carModelId, pageable);
        return PagedResponse.of(page.map(partMapper::toListResponse));
    }

    @Override
    public PagedResponse<PartResponse> getPartsNotInWarehouse(UUID warehouseId, String name, Pageable pageable) {
        Page<Part> page = partRepository.findPartsNotInWarehouse(warehouseId, name, pageable);
        return PagedResponse.of(page.map(partMapper::toListResponse));
    }

    @Override
    public PagedResponse<PartResponse> getPartsInWarehouse(UUID warehouseId, String name, Pageable pageable) {
        Page<Part> page = partRepository.findPartsInWarehouse(warehouseId, name, pageable);
        return PagedResponse.of(page.map(partMapper::toListResponse));
    }

    @Override
    public PartResponse getPartById(UUID id) {
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

        resolveCarBrandAndModel(part, request.getCarBrandId(), request.getCarModelId());
        resolveTags(part, request.getTagIds());

        Part saved = partRepository.save(part);
        return partMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PartResponse updatePart(UUID id, UpdatePartRequest request) {
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

        resolveCarBrandAndModel(part, request.getCarBrandId(), request.getCarModelId());
        resolveTags(part, request.getTagIds());

        Part saved = partRepository.save(part);
        return partMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deletePart(UUID id) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Part", "id", id));
        partRepository.delete(part);
    }

    @Override
    @Transactional
    public PartImageResponse addImageToPart(UUID partId, CreatePartImageRequest request) {
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new ResourceNotFoundException("Part", "id", partId));

        PartImage partImage = new PartImage();
        partImage.setPart(part);
        partImage.setReference(request.getUrl());
        partImage.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);

        part.getImages().add(partImage);
        partRepository.save(part);

        return partMapper.toPartImageResponse(partImage);
    }

    @Override
    @Transactional
    public void removeImageFromPart(UUID partId, UUID imageId) {
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new ResourceNotFoundException("Part", "id", partId));

        PartImage partImage = part.getImages().stream()
                .filter(img -> img.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("PartImage", "id", imageId));

        part.getImages().remove(partImage);
        partRepository.save(part);
    }

    @Override
    @Transactional
    public List<PartImageResponse> uploadImages(UUID partId, MultipartFile[] files) {
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new ResourceNotFoundException("Part", "id", partId));

        validateFiles(files);

        List<PartImageResponse> uploadedImages = new ArrayList<>();
        int currentMaxSortOrder = part.getImages().stream()
                .mapToInt(PartImage::getSortOrder)
                .max()
                .orElse(-1);

        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];

            String imageReference = fileStorageService.uploadFileReturnReference(file, "parts/images");

            PartImage partImage = new PartImage();
            partImage.setPart(part);
            partImage.setReference(imageReference);
            partImage.setSortOrder(currentMaxSortOrder + i + 1);

            part.getImages().add(partImage);
            uploadedImages.add(partMapper.toPartImageResponse(partImage));
        }

        partRepository.save(part);

        return uploadedImages;
    }

    @Override
    @Transactional
    public List<PartImageResponse> replaceAllImages(UUID partId, MultipartFile[] files) {
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new ResourceNotFoundException("Part", "id", partId));

        validateFiles(files);

        List<PartImage> existingImages = new ArrayList<>(part.getImages());
        for (PartImage image : existingImages) {
            try {
                fileStorageService.deleteFileByReference(image.getReference());
            } catch (Exception e) {
            }
        }
        part.getImages().clear();

        List<PartImageResponse> uploadedImages = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];

            String imageReference = fileStorageService.uploadFileReturnReference(file, "parts/images");

            PartImage partImage = new PartImage();
            partImage.setPart(part);
            partImage.setReference(imageReference);
            partImage.setSortOrder(i);

            part.getImages().add(partImage);
            uploadedImages.add(partMapper.toPartImageResponse(partImage));
        }

        partRepository.save(part);

        return uploadedImages;
    }

    private void validateFiles(MultipartFile[] files) {
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("At least one file is required");
        }

        if (files.length > MAX_FILES_PER_UPLOAD) {
            throw new IllegalArgumentException("Maximum " + MAX_FILES_PER_UPLOAD + " files allowed per upload");
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("Empty files are not allowed");
            }

            if (file.getSize() > MAX_FILE_SIZE) {
                throw new IllegalArgumentException("File size must not exceed " + (MAX_FILE_SIZE / 1024 / 1024) + "MB");
            }

            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
                throw new IllegalArgumentException("Invalid file type. Allowed types: " + String.join(", ", ALLOWED_CONTENT_TYPES));
            }
        }
    }

    private void resolveCarBrandAndModel(Part part, UUID carBrandId, UUID carModelId) {
        if (carBrandId != null) {
            CarBrand carBrand = carBrandRepository.findById(carBrandId)
                    .orElseThrow(() -> new ResourceNotFoundException("CarBrand", "id", carBrandId));
            part.setCarBrand(carBrand);
        } else {
            part.setCarBrand(null);
        }

        if (carModelId != null) {
            CarModel carModel = carModelRepository.findById(carModelId)
                    .orElseThrow(() -> new ResourceNotFoundException("CarModel", "id", carModelId));
            part.setCarModel(carModel);
        } else {
            part.setCarModel(null);
        }
    }

    private void resolveTags(Part part, List<UUID> tagIds) {
        if (tagIds != null) {
            List<Tag> tags = new ArrayList<>();
            for (UUID tagId : tagIds) {
                Tag tag = tagRepository.findById(tagId)
                        .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", tagId));
                tags.add(tag);
            }
            part.setTags(tags);
        } else {
            part.setTags(new ArrayList<>());
        }
    }
}
