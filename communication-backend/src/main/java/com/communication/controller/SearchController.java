package com.communication.controller;

import com.communication.dto.ApiResponse;
import com.communication.dto.ContentDto;
import com.communication.dto.PageResponse;
import com.communication.dto.UserDto;
import com.communication.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/contents")
    public ResponseEntity<ApiResponse<PageResponse<ContentDto>>> searchContents(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ContentDto> results = searchService.searchContents(q, tag, page, size);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<PageResponse<UserDto>>> searchUsers(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<UserDto> results = searchService.searchUsers(q, page, size);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @GetMapping("/tags/popular")
    public ResponseEntity<ApiResponse<List<String>>> getPopularTags(
            @RequestParam(defaultValue = "20") int limit) {
        List<String> tags = searchService.getPopularTags(limit);
        return ResponseEntity.ok(ApiResponse.success(tags));
    }

    @GetMapping("/tags/suggest")
    public ResponseEntity<ApiResponse<List<String>>> suggestTags(
            @RequestParam String q) {
        List<String> tags = searchService.suggestTags(q);
        return ResponseEntity.ok(ApiResponse.success(tags));
    }
}
