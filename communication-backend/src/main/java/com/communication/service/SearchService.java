package com.communication.service;

import com.communication.dto.ContentDto;
import com.communication.dto.PageResponse;
import com.communication.dto.UserDto;

import java.util.List;

public interface SearchService {

    PageResponse<ContentDto> searchContents(String keyword, String tag, int page, int size);

    PageResponse<UserDto> searchUsers(String keyword, int page, int size);

    List<String> getPopularTags(int limit);

    List<String> suggestTags(String keyword);
}
