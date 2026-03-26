package com.communication.service.impl;

import com.communication.dto.CommentDto;
import com.communication.dto.CreateCommentRequest;
import com.communication.dto.PageResponse;
import com.communication.entity.Comment;
import com.communication.entity.Content;
import com.communication.entity.User;
import com.communication.exception.BadRequestException;
import com.communication.exception.ResourceNotFoundException;
import com.communication.entity.NotificationType;
import com.communication.repository.CommentRepository;
import com.communication.repository.ContentRepository;
import com.communication.repository.UserRepository;
import com.communication.service.CommentService;
import com.communication.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ContentRepository contentRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public CommentServiceImpl(CommentRepository commentRepository, ContentRepository contentRepository,
                              UserRepository userRepository, NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.contentRepository = contentRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public CommentDto createComment(Long contentId, CreateCommentRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("内容不存在"));

        Comment comment = Comment.builder()
                .content(content)
                .user(user)
                .body(request.getBody())
                .build();

        if (request.getParentId() != null) {
            Comment parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("父评论不存在"));

            if (!parent.getContent().getId().equals(contentId)) {
                throw new BadRequestException("父评论不属于该内容");
            }

            comment.setParent(parent);
        }

        Comment saved = commentRepository.save(comment);

        content.setCommentCount(content.getCommentCount() + 1);
        contentRepository.save(content);

        if (saved.getParent() == null) {
            if (!content.getAuthor().getId().equals(user.getId())) {
                notificationService.createNotification(
                        content.getAuthor().getId(), user.getId(),
                        NotificationType.COMMENT, contentId, saved.getId(),
                        user.getUsername() + " 评论了你的文章「" + content.getTitle() + "」");
            }
        } else {
            User parentAuthor = saved.getParent().getUser();
            if (!parentAuthor.getId().equals(user.getId())) {
                notificationService.createNotification(
                        parentAuthor.getId(), user.getId(),
                        NotificationType.COMMENT, contentId, saved.getId(),
                        user.getUsername() + " 回复了你在「" + content.getTitle() + "」下的评论");
            }
        }

        return CommentDto.fromEntity(saved);
    }

    @Override
    public CommentDto getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("评论不存在"));
        return CommentDto.fromEntityWithReplies(comment);
    }

    @Override
    public PageResponse<CommentDto> getCommentsByContent(Long contentId, int page, int size) {
        if (!contentRepository.existsById(contentId)) {
            throw new ResourceNotFoundException("内容不存在");
        }

        Page<Comment> comments = commentRepository
                .findByContentIdAndParentIsNullOrderByCreatedAtDesc(contentId, PageRequest.of(page, size));

        List<CommentDto> dtos = comments.getContent().stream()
                .map(comment -> {
                    CommentDto dto = CommentDto.fromEntity(comment);
                    List<Comment> replies = commentRepository.findByParentIdOrderByCreatedAtAsc(comment.getId());
                    if (!replies.isEmpty()) {
                        dto.setReplies(replies.stream()
                                .map(CommentDto::fromEntity)
                                .collect(Collectors.toList()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        return PageResponse.<CommentDto>builder()
                .content(dtos)
                .page(page)
                .size(size)
                .totalElements(comments.getTotalElements())
                .totalPages(comments.getTotalPages())
                .first(comments.isFirst())
                .last(comments.isLast())
                .build();
    }

    @Override
    @Transactional
    public void deleteComment(Long id, String username) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("评论不存在"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        boolean isCommentAuthor = comment.getUser().getId().equals(user.getId());
        boolean isContentAuthor = comment.getContent().getAuthor().getId().equals(user.getId());

        if (!isCommentAuthor && !isContentAuthor) {
            throw new BadRequestException("无权删除此评论");
        }

        long replyCount = comment.getReplies() != null ? comment.getReplies().size() : 0;
        long totalToDelete = 1 + replyCount;

        Content content = comment.getContent();
        commentRepository.delete(comment);

        content.setCommentCount(Math.max(0, content.getCommentCount() - (int) totalToDelete));
        contentRepository.save(content);
    }

    @Override
    public long getCommentCountByContent(Long contentId) {
        return commentRepository.countByContentId(contentId);
    }
}
