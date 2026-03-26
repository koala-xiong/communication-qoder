package com.communication.service.impl;

import com.communication.dto.ContentDto;
import com.communication.dto.PageResponse;
import com.communication.entity.Content;
import com.communication.entity.ContentStatus;
import com.communication.repository.ContentRepository;
import com.communication.repository.ContentTagRepository;
import com.communication.service.TrendingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrendingServiceImpl implements TrendingService {

    private final ContentRepository contentRepository;
    private final ContentTagRepository contentTagRepository;

    public TrendingServiceImpl(ContentRepository contentRepository, ContentTagRepository contentTagRepository) {
        this.contentRepository = contentRepository;
        this.contentTagRepository = contentTagRepository;
    }

    @Override
    public PageResponse<ContentDto> getTrendingContents(int page, int size) {
        Page<Content> contents = contentRepository.findTrending(
                ContentStatus.PUBLISHED, PageRequest.of(page, size));
        return toPageResponse(contents);
    }

    @Override
    public PageResponse<ContentDto> getRecentPopular(int page, int size) {
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        Page<Content> contents = contentRepository.findRecentPopular(
                ContentStatus.PUBLISHED, since, PageRequest.of(page, size));
        return toPageResponse(contents);
    }

    private PageResponse<ContentDto> toPageResponse(Page<Content> contents) {
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
