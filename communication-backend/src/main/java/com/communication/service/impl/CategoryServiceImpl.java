package com.communication.service.impl;

import com.communication.dto.CategoryDto;
import com.communication.dto.ContentDto;
import com.communication.dto.PageResponse;
import com.communication.entity.ContentStatus;
import com.communication.exception.ResourceNotFoundException;
import com.communication.repository.CategoryRepository;
import com.communication.repository.ContentRepository;
import com.communication.repository.ContentTagRepository;
import com.communication.service.CategoryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ContentRepository contentRepository;
    private final ContentTagRepository contentTagRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               ContentRepository contentRepository,
                               ContentTagRepository contentTagRepository) {
        this.categoryRepository = categoryRepository;
        this.contentRepository = contentRepository;
        this.contentTagRepository = contentTagRepository;
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAllByOrderBySortOrderAsc().stream()
                .map(CategoryDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<ContentDto> getContentsByCategory(Long categoryId, int page, int size) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        var contents = contentRepository.findByCategoryIdAndStatus(
                categoryId, ContentStatus.PUBLISHED,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        List<ContentDto> dtos = contents.getContent().stream()
                .map(c -> {
                    ContentDto dto = ContentDto.fromEntity(c);
                    dto.setTags(contentTagRepository.findByContentId(c.getId())
                            .stream().map(t -> t.getTag()).collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
        return new PageResponse<>(dtos, contents.getNumber(), contents.getSize(),
                contents.getTotalElements(), contents.getTotalPages(),
                contents.isFirst(), contents.isLast());
    }
}
