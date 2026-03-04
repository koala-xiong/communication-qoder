package com.communication.service;

import com.communication.dto.CommentDto;
import com.communication.dto.CreateCommentRequest;
import com.communication.dto.PageResponse;
import com.communication.entity.Comment;
import com.communication.entity.Content;
import com.communication.entity.User;
import com.communication.exception.BadRequestException;
import com.communication.exception.ResourceNotFoundException;
import com.communication.repository.CommentRepository;
import com.communication.repository.ContentRepository;
import com.communication.repository.UserRepository;
import com.communication.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User testUser;
    private User authorUser;
    private Content testContent;
    private Comment testComment;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encoded")
                .build();

        authorUser = User.builder()
                .id(2L)
                .username("author")
                .email("author@example.com")
                .password("encoded")
                .build();

        testContent = Content.builder()
                .id(1L)
                .title("Test Content")
                .body("Test body")
                .author(authorUser)
                .viewCount(0)
                .commentCount(0)
                .build();

        testComment = Comment.builder()
                .id(1L)
                .content(testContent)
                .user(testUser)
                .body("Test comment")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .replies(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("创建评论成功")
    void createComment_Success() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setBody("New comment");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(contentRepository.findById(1L)).thenReturn(Optional.of(testContent));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);
        when(contentRepository.save(any(Content.class))).thenReturn(testContent);

        CommentDto result = commentService.createComment(1L, request, "testuser");

        assertThat(result).isNotNull();
        assertThat(result.getBody()).isEqualTo("Test comment");
        verify(commentRepository).save(any(Comment.class));
        verify(contentRepository).save(testContent);
    }

    @Test
    @DisplayName("创建评论 - 用户不存在")
    void createComment_UserNotFound() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setBody("New comment");

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.createComment(1L, request, "unknown"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("用户不存在");
    }

    @Test
    @DisplayName("创建评论 - 内容不存在")
    void createComment_ContentNotFound() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setBody("New comment");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(contentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.createComment(999L, request, "testuser"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("内容不存在");
    }

    @Test
    @DisplayName("创建回复成功")
    void createReply_Success() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setBody("Reply comment");
        request.setParentId(1L);

        Comment replyComment = Comment.builder()
                .id(2L)
                .content(testContent)
                .user(testUser)
                .body("Reply comment")
                .parent(testComment)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(contentRepository.findById(1L)).thenReturn(Optional.of(testContent));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(replyComment);
        when(contentRepository.save(any(Content.class))).thenReturn(testContent);

        CommentDto result = commentService.createComment(1L, request, "testuser");

        assertThat(result).isNotNull();
        assertThat(result.getParentId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("创建回复 - 父评论不属于该内容")
    void createReply_ParentNotBelongToContent() {
        Content otherContent = Content.builder().id(2L).author(authorUser).build();
        Comment otherComment = Comment.builder()
                .id(2L)
                .content(otherContent)
                .user(testUser)
                .body("Other comment")
                .build();

        CreateCommentRequest request = new CreateCommentRequest();
        request.setBody("Reply");
        request.setParentId(2L);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(contentRepository.findById(1L)).thenReturn(Optional.of(testContent));
        when(commentRepository.findById(2L)).thenReturn(Optional.of(otherComment));

        assertThatThrownBy(() -> commentService.createComment(1L, request, "testuser"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("父评论不属于该内容");
    }

    @Test
    @DisplayName("获取评论列表")
    void getCommentsByContent_Success() {
        Page<Comment> commentPage = new PageImpl<>(List.of(testComment));

        when(contentRepository.existsById(1L)).thenReturn(true);
        when(commentRepository.findByContentIdAndParentIsNullOrderByCreatedAtDesc(eq(1L), any(PageRequest.class)))
                .thenReturn(commentPage);
        when(commentRepository.findByParentIdOrderByCreatedAtAsc(1L)).thenReturn(new ArrayList<>());

        PageResponse<CommentDto> result = commentService.getCommentsByContent(1L, 0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("删除评论 - 评论作者删除")
    void deleteComment_ByCommentAuthor() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(contentRepository.save(any(Content.class))).thenReturn(testContent);

        commentService.deleteComment(1L, "testuser");

        verify(commentRepository).delete(testComment);
    }

    @Test
    @DisplayName("删除评论 - 内容作者删除")
    void deleteComment_ByContentAuthor() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(userRepository.findByUsername("author")).thenReturn(Optional.of(authorUser));
        when(contentRepository.save(any(Content.class))).thenReturn(testContent);

        commentService.deleteComment(1L, "author");

        verify(commentRepository).delete(testComment);
    }

    @Test
    @DisplayName("删除评论 - 无权限")
    void deleteComment_Unauthorized() {
        User otherUser = User.builder().id(3L).username("other").build();

        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(userRepository.findByUsername("other")).thenReturn(Optional.of(otherUser));

        assertThatThrownBy(() -> commentService.deleteComment(1L, "other"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("无权删除此评论");
    }
}
