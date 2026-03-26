package com.communication.service;

import com.communication.dto.ContentDto;
import com.communication.dto.PageResponse;

import java.util.List;
import java.util.Map;

public interface FavoriteService {

    boolean toggleFavorite(Long contentId, String username);

    boolean isFavorited(Long contentId, String username);

    Map<Long, Boolean> batchCheckFavorited(List<Long> contentIds, String username);

    PageResponse<ContentDto> getFavoriteContents(String username, int page, int size);

    long getFavoriteCount(String username);
}
