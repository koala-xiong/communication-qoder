package com.communication.service;

import com.communication.dto.ContentDto;
import com.communication.dto.PageResponse;

import java.util.List;
import java.util.Map;

public interface LikeService {

    boolean toggleLike(Long contentId, String username);

    boolean isLiked(Long contentId, String username);

    Map<Long, Boolean> batchCheckLiked(List<Long> contentIds, String username);

    PageResponse<ContentDto> getLikedContents(String username, int page, int size);
}
