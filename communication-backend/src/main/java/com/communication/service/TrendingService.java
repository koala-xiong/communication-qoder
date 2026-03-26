package com.communication.service;

import com.communication.dto.ContentDto;
import com.communication.dto.PageResponse;

public interface TrendingService {

    PageResponse<ContentDto> getTrendingContents(int page, int size);

    PageResponse<ContentDto> getRecentPopular(int page, int size);
}
