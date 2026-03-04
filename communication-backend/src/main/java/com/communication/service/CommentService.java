package com.communication.service;

import com.communication.dto.CommentDto;
import com.communication.dto.CreateCommentRequest;
import com.communication.dto.PageResponse;

public interface CommentService {

    CommentDto createComment(Long contentId, CreateCommentRequest request, String username);

    CommentDto getCommentById(Long id);

    PageResponse<CommentDto> getCommentsByContent(Long contentId, int page, int size);

    void deleteComment(Long id, String username);

    long getCommentCountByContent(Long contentId);
}
