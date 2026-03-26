package com.communication.service;

import com.communication.dto.CategoryDto;
import com.communication.dto.ContentDto;
import com.communication.dto.PageResponse;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> getAllCategories();

    PageResponse<ContentDto> getContentsByCategory(Long categoryId, int page, int size);
}
