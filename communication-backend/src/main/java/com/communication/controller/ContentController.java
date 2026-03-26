package com.communication.controller;

import com.communication.dto.*;
import com.communication.entity.ContentStatus;
import com.communication.service.ContentService;
import com.communication.service.ReadingHistoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contents")
public class ContentController {

    private final ContentService contentService;
    private final ReadingHistoryService readingHistoryService;

    public ContentController(ContentService contentService, ReadingHistoryService readingHistoryService) {
        this.contentService = contentService;
        this.readingHistoryService = readingHistoryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ContentDto>> createContent(
            @Valid @RequestBody CreateContentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ContentDto content = contentService.createContent(request, userDetails.getUsername());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Content created successfully", content));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ContentDto>>> getContents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ContentDto> contents = contentService.getPublishedContents(page, size);
        return ResponseEntity.ok(ApiResponse.success(contents));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContentDto>> getContent(
            @PathVariable Long id,
            Authentication authentication) {
        ContentDto content = contentService.getContentById(id);
        contentService.incrementViewCount(id);
        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserDetails ud) {
            readingHistoryService.recordReading(id, ud.getUsername());
        }
        return ResponseEntity.ok(ApiResponse.success(content));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ContentDto>> updateContent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateContentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ContentDto content = contentService.updateContent(id, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Content updated successfully", content));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteContent(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        contentService.deleteContent(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Content deleted successfully", null));
    }

    @GetMapping("/user/{authorId}")
    public ResponseEntity<ApiResponse<PageResponse<ContentDto>>> getContentsByAuthor(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ContentDto> contents = contentService.getContentsByAuthor(authorId, page, size);
        return ResponseEntity.ok(ApiResponse.success(contents));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<PageResponse<ContentDto>>> getMyContents(
            @RequestParam(required = false) ContentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        PageResponse<ContentDto> contents = contentService.getMyContents(
                userDetails.getUsername(), status, page, size);
        return ResponseEntity.ok(ApiResponse.success(contents));
    }
}
