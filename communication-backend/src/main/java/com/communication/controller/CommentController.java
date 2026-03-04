package com.communication.controller;

import com.communication.dto.ApiResponse;
import com.communication.dto.CommentDto;
import com.communication.dto.CreateCommentRequest;
import com.communication.dto.PageResponse;
import com.communication.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contents/{contentId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CommentDto>> createComment(
            @PathVariable Long contentId,
            @Valid @RequestBody CreateCommentRequest request,
            Authentication authentication) {
        CommentDto comment = commentService.createComment(contentId, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(comment));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CommentDto>>> getComments(
            @PathVariable Long contentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<CommentDto> comments = commentService.getCommentsByContent(contentId, page, size);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentDto>> getComment(@PathVariable Long commentId) {
        CommentDto comment = commentService.getCommentById(commentId);
        return ResponseEntity.ok(ApiResponse.success(comment));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long commentId,
            Authentication authentication) {
        commentService.deleteComment(commentId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
