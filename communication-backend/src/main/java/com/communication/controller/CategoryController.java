package com.communication.controller;

import com.communication.dto.ApiResponse;
import com.communication.dto.CategoryDto;
import com.communication.dto.ContentDto;
import com.communication.dto.PageResponse;
import com.communication.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ApiResponse<List<CategoryDto>> getAllCategories() {
        return ApiResponse.success(categoryService.getAllCategories());
    }

    @GetMapping("/{id}/contents")
    public ApiResponse<PageResponse<ContentDto>> getContentsByCategory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(categoryService.getContentsByCategory(id, page, size));
    }
}
