package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.symmetry.spareparts.dto.request.CreateTagRequest;
import sn.symmetry.spareparts.dto.request.UpdateTagRequest;
import sn.symmetry.spareparts.dto.response.TagResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.entity.Tag;
import sn.symmetry.spareparts.exception.DuplicateResourceException;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.mapper.TagMapper;
import sn.symmetry.spareparts.repository.TagRepository;
import sn.symmetry.spareparts.service.TagService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Override
    public PagedResponse<TagResponse> getAllTags(Pageable pageable) {
        Page<Tag> page = tagRepository.findAll(pageable);
        return PagedResponse.of(page.map(tagMapper::toResponse));
    }

    @Override
    @Cacheable(value = "tags", key = "'all'")
    public List<TagResponse> getAllTagsList() {
        return tagRepository.findAll().stream()
                .map(tagMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TagResponse getTagById(UUID id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));
        return tagMapper.toResponse(tag);
    }

    @Override
    @Transactional
    @CacheEvict(value = "tags", key = "'all'")
    public TagResponse createTag(CreateTagRequest request) {
        if (tagRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Tag", "name", request.getName());
        }

        Tag tag = tagMapper.toEntity(request);
        Tag saved = tagRepository.save(tag);
        return tagMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "tags", key = "'all'")
    public TagResponse updateTag(UUID id, UpdateTagRequest request) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));

        if (tagRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new DuplicateResourceException("Tag", "name", request.getName());
        }

        tagMapper.updateEntity(request, tag);
        Tag saved = tagRepository.save(tag);
        return tagMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "tags", key = "'all'")
    public void deleteTag(UUID id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));
        tagRepository.delete(tag);
    }
}
