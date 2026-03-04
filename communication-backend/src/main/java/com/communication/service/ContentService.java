package com.communication.service;

import com.communication.dto.*;
import com.communication.entity.ContentStatus;

public interface ContentService {

    ContentDto createContent(CreateContentRequest request, String username);

    ContentDto getContentById(Long id);

    ContentDto updateContent(Long id, UpdateContentRequest request, String username);

    void deleteContent(Long id, String username);

    PageResponse<ContentDto> getPublishedContents(int page, int size);

    PageResponse<ContentDto> getContentsByAuthor(Long authorId, int page, int size);

    PageResponse<ContentDto> getMyContents(String username, ContentStatus status, int page, int size);

    void incrementViewCount(Long id);
}
