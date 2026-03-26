package com.communication.service;

import com.communication.dto.ContentDto;
import com.communication.dto.PageResponse;

public interface ReadingHistoryService {

    void recordReading(Long contentId, String username);

    PageResponse<ContentDto> getReadingHistory(String username, int page, int size);

    void clearHistory(String username);
}
