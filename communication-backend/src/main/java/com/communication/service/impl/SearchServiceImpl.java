package com.communication.service.impl;

import com.communication.dto.ContentDto;
import com.communication.dto.PageResponse;
import com.communication.dto.UserDto;
import com.communication.entity.Content;
import com.communication.entity.ContentStatus;
import com.communication.entity.User;
import com.communication.repository.ContentRepository;
import com.communication.repository.ContentTagRepository;
import com.communication.repository.UserRepository;
import com.communication.service.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    private final ContentRepository contentRepository;
    private final ContentTagRepository contentTagRepository;
    private final UserRepository userRepository;

    public SearchServiceImpl(ContentRepository contentRepository, ContentTagRepository contentTagRepository, UserRepository userRepository) {
        this.contentRepository = contentRepository;
        this.contentTagRepository = contentTagRepository;
        this.userRepository = userRepository;
    }

    @Override
    public PageResponse<ContentDto> searchContents(String keyword, String tag, int page, int size) {
        Page<Content> contents;
        PageRequest pageRequest = PageRequest.of(page, size);

        if (tag != null && !tag.isBlank()) {
            // 按标签搜索
            List<Long> contentIds = contentTagRepository.findContentIdsByTag(tag);
            if (contentIds.isEmpty()) {
                return emptyPageResponse(page, size);
            }
            contents = contentRepository.findByIdInAndStatus(contentIds, ContentStatus.PUBLISHED, pageRequest);
        } else if (keyword != null && !keyword.isBlank()) {
            // 按关键词搜索
            contents = contentRepository.searchByKeyword(keyword, ContentStatus.PUBLISHED, pageRequest);
        } else {
            // 无搜索条件，返回最新内容
            contents = contentRepository.findByStatusOrderByCreatedAtDesc(ContentStatus.PUBLISHED, pageRequest);
        }

        List<ContentDto> dtos = contents.getContent().stream()
                .map(this::toContentDtoWithTags)
                .collect(Collectors.toList());

        return PageResponse.<ContentDto>builder()
                .content(dtos)
                .page(page)
                .size(size)
                .totalElements(contents.getTotalElements())
                .totalPages(contents.getTotalPages())
                .first(contents.isFirst())
                .last(contents.isLast())
                .build();
    }

    @Override
    public PageResponse<UserDto> searchUsers(String keyword, int page, int size) {
        if (keyword == null || keyword.isBlank()) {
            return emptyPageResponse(page, size);
        }

        Page<User> users = userRepository.searchByUsername(keyword, PageRequest.of(page, size));

        List<UserDto> dtos = users.getContent().stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());

        return PageResponse.<UserDto>builder()
                .content(dtos)
                .page(page)
                .size(size)
                .totalElements(users.getTotalElements())
                .totalPages(users.getTotalPages())
                .first(users.isFirst())
                .last(users.isLast())
                .build();
    }

    @Override
    public List<String> getPopularTags(int limit) {
        return contentTagRepository.findPopularTags().stream()
                .limit(limit)
                .map(row -> (String) row[0])
                .collect(Collectors.toList());
    }

    @Override
    public List<String> suggestTags(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        return contentTagRepository.findTagsByKeyword(keyword);
    }

    private ContentDto toContentDtoWithTags(Content content) {
        ContentDto dto = ContentDto.fromEntity(content);
        List<String> tags = contentTagRepository.findByContentId(content.getId())
                .stream()
                .map(ct -> ct.getTag())
                .collect(Collectors.toList());
        dto.setTags(tags);
        return dto;
    }

    private <T> PageResponse<T> emptyPageResponse(int page, int size) {
        return PageResponse.<T>builder()
                .content(List.of())
                .page(page)
                .size(size)
                .totalElements(0)
                .totalPages(0)
                .first(true)
                .last(true)
                .build();
    }
}
