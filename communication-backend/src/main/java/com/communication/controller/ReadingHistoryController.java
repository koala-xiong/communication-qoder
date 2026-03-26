package com.communication.controller;

import com.communication.dto.ApiResponse;
import com.communication.dto.ContentDto;
import com.communication.dto.PageResponse;
import com.communication.service.ReadingHistoryService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/history")
public class ReadingHistoryController {

    private final ReadingHistoryService readingHistoryService;

    public ReadingHistoryController(ReadingHistoryService readingHistoryService) {
        this.readingHistoryService = readingHistoryService;
    }

    @GetMapping
    public ApiResponse<PageResponse<ContentDto>> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        return ApiResponse.success(readingHistoryService.getReadingHistory(auth.getName(), page, size));
    }

    @DeleteMapping
    public ApiResponse<Void> clearHistory(Authentication auth) {
        readingHistoryService.clearHistory(auth.getName());
        return ApiResponse.success(null);
    }
}
