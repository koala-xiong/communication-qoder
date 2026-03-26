package com.communication.service.impl;

import com.communication.dto.*;
import com.communication.entity.Content;
import com.communication.entity.ContentStatus;
import com.communication.entity.ContentTag;
import com.communication.entity.User;
import com.communication.exception.BadRequestException;
import com.communication.exception.ResourceNotFoundException;
import com.communication.entity.Category;
import com.communication.repository.CategoryRepository;
import com.communication.repository.ContentRepository;
import com.communication.repository.ContentTagRepository;
import com.communication.service.ContentService;
import com.communication.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContentServiceImpl implements ContentService {

    private final ContentRepository contentRepository;
    private final ContentTagRepository contentTagRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;

    public ContentServiceImpl(ContentRepository contentRepository, ContentTagRepository contentTagRepository,
                              CategoryRepository categoryRepository, UserService userService) {
        this.contentRepository = contentRepository;
        this.contentTagRepository = contentTagRepository;
        this.categoryRepository = categoryRepository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public ContentDto createContent(CreateContentRequest request, String username) {
        User author = userService.findByUsername(username);

        Content content = Content.builder()
                .author(author)
                .title(request.getTitle())
                .body(request.getBody())
                .mediaUrl(request.getMediaUrl())
                .mediaType(request.getMediaType())
                .status(request.getStatus())
                .build();

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("分类不存在"));
            content.setCategory(category);
        }

        content = contentRepository.save(content);

        // 保存标签
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            saveTags(content, request.getTags());
        }

        return toContentDtoWithTags(content);
    }

    @Override
    @Transactional(readOnly = true)
    public ContentDto getContentById(Long id) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", id));
        return toContentDtoWithTags(content);
    }

    @Override
    @Transactional
    public ContentDto updateContent(Long id, UpdateContentRequest request, String username) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", id));

        if (!content.getAuthor().getUsername().equals(username)) {
            throw new BadRequestException("You can only edit your own content");
        }

        if (request.getTitle() != null) {
            content.setTitle(request.getTitle());
        }
        if (request.getBody() != null) {
            content.setBody(request.getBody());
        }
        if (request.getMediaUrl() != null) {
            content.setMediaUrl(request.getMediaUrl());
        }
        if (request.getMediaType() != null) {
            content.setMediaType(request.getMediaType());
        }
        if (request.getStatus() != null) {
            content.setStatus(request.getStatus());
        }
        if (Boolean.TRUE.equals(request.getClearCategory())) {
            content.setCategory(null);
        } else if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("分类不存在"));
            content.setCategory(category);
        }

        // 更新标签
        if (request.getTags() != null) {
            contentTagRepository.deleteByContentId(id);
            if (!request.getTags().isEmpty()) {
                saveTags(content, request.getTags());
            }
        }

        content = contentRepository.save(content);
        return toContentDtoWithTags(content);
    }

    @Override
    @Transactional
    public void deleteContent(Long id, String username) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", id));

        if (!content.getAuthor().getUsername().equals(username)) {
            throw new BadRequestException("You can only delete your own content");
        }

        contentRepository.delete(content);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ContentDto> getPublishedContents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Content> contentPage = contentRepository.findByStatusOrderByCreatedAtDesc(
                ContentStatus.PUBLISHED, pageable);

        return toPageResponse(contentPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ContentDto> getContentsByAuthor(Long authorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Content> contentPage = contentRepository.findByAuthorIdAndStatusOrderByCreatedAtDesc(
                authorId, ContentStatus.PUBLISHED, pageable);

        return toPageResponse(contentPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ContentDto> getMyContents(String username, ContentStatus status, int page, int size) {
        User user = userService.findByUsername(username);
        Pageable pageable = PageRequest.of(page, size);

        Page<Content> contentPage;
        if (status != null) {
            contentPage = contentRepository.findByAuthorIdAndStatusOrderByCreatedAtDesc(
                    user.getId(), status, pageable);
        } else {
            contentPage = contentRepository.findByAuthorIdOrderByCreatedAtDesc(user.getId(), pageable);
        }

        return toPageResponse(contentPage);
    }

    @Override
    @Transactional
    public void incrementViewCount(Long id) {
        contentRepository.incrementViewCount(id);
    }

    private PageResponse<ContentDto> toPageResponse(Page<Content> contentPage) {
        List<ContentDto> contents = contentPage.getContent().stream()
                .map(this::toContentDtoWithTags)
                .toList();

        return PageResponse.<ContentDto>builder()
                .content(contents)
                .page(contentPage.getNumber())
                .size(contentPage.getSize())
                .totalElements(contentPage.getTotalElements())
                .totalPages(contentPage.getTotalPages())
                .first(contentPage.isFirst())
                .last(contentPage.isLast())
                .build();
    }

    private void saveTags(Content content, List<String> tags) {
        List<ContentTag> contentTags = tags.stream()
                .distinct()
                .map(tag -> ContentTag.builder()
                        .content(content)
                        .tag(tag.trim().toLowerCase())
                        .build())
                .collect(Collectors.toList());
        contentTagRepository.saveAll(contentTags);
    }

    private ContentDto toContentDtoWithTags(Content content) {
        ContentDto dto = ContentDto.fromEntity(content);
        List<String> tags = contentTagRepository.findByContentId(content.getId())
                .stream()
                .map(ContentTag::getTag)
                .collect(Collectors.toList());
        dto.setTags(tags);
        return dto;
    }
}
