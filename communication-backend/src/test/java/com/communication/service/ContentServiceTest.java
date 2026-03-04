package com.communication.service;

import com.communication.dto.ContentDto;
import com.communication.dto.CreateContentRequest;
import com.communication.dto.PageResponse;
import com.communication.dto.UpdateContentRequest;
import com.communication.entity.Content;
import com.communication.entity.ContentStatus;
import com.communication.entity.MediaType;
import com.communication.entity.User;
import com.communication.exception.BadRequestException;
import com.communication.exception.ResourceNotFoundException;
import com.communication.repository.ContentRepository;
import com.communication.service.impl.ContentServiceImpl;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContentServiceTest {

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ContentServiceImpl contentService;

    private User testUser;
    private User otherUser;
    private Content testContent;
    private CreateContentRequest createRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        otherUser = User.builder()
                .id(2L)
                .username("otheruser")
                .email("other@example.com")
                .build();

        testContent = Content.builder()
                .id(1L)
                .author(testUser)
                .title("Test Title")
                .body("Test Body")
                .mediaType(MediaType.TEXT)
                .status(ContentStatus.PUBLISHED)
                .viewCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createRequest = new CreateContentRequest();
        createRequest.setTitle("New Content");
        createRequest.setBody("Content body");
        createRequest.setMediaType(MediaType.TEXT);
        createRequest.setStatus(ContentStatus.PUBLISHED);
    }

    @Test
    @DisplayName("Create content - Success")
    void createContent_Success() {
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(contentRepository.save(any(Content.class))).thenReturn(testContent);

        ContentDto result = contentService.createContent(createRequest, "testuser");

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Title");
        verify(contentRepository).save(any(Content.class));
    }

    @Test
    @DisplayName("Get content by ID - Success")
    void getContentById_Success() {
        when(contentRepository.findById(1L)).thenReturn(Optional.of(testContent));

        ContentDto result = contentService.getContentById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test Title");
    }

    @Test
    @DisplayName("Get content by ID - Not found")
    void getContentById_NotFound() {
        when(contentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contentService.getContentById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Update content - Success")
    void updateContent_Success() {
        UpdateContentRequest updateRequest = new UpdateContentRequest();
        updateRequest.setTitle("Updated Title");

        Content updatedContent = Content.builder()
                .id(1L)
                .author(testUser)
                .title("Updated Title")
                .body("Test Body")
                .mediaType(MediaType.TEXT)
                .status(ContentStatus.PUBLISHED)
                .build();

        when(contentRepository.findById(1L)).thenReturn(Optional.of(testContent));
        when(contentRepository.save(any(Content.class))).thenReturn(updatedContent);

        ContentDto result = contentService.updateContent(1L, updateRequest, "testuser");

        assertThat(result.getTitle()).isEqualTo("Updated Title");
        verify(contentRepository).save(any(Content.class));
    }

    @Test
    @DisplayName("Update content - Unauthorized")
    void updateContent_Unauthorized() {
        UpdateContentRequest updateRequest = new UpdateContentRequest();
        updateRequest.setTitle("Updated Title");

        when(contentRepository.findById(1L)).thenReturn(Optional.of(testContent));

        assertThatThrownBy(() -> contentService.updateContent(1L, updateRequest, "otheruser"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("You can only edit your own content");

        verify(contentRepository, never()).save(any(Content.class));
    }

    @Test
    @DisplayName("Delete content - Success")
    void deleteContent_Success() {
        when(contentRepository.findById(1L)).thenReturn(Optional.of(testContent));

        contentService.deleteContent(1L, "testuser");

        verify(contentRepository).delete(testContent);
    }

    @Test
    @DisplayName("Delete content - Unauthorized")
    void deleteContent_Unauthorized() {
        when(contentRepository.findById(1L)).thenReturn(Optional.of(testContent));

        assertThatThrownBy(() -> contentService.deleteContent(1L, "otheruser"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("You can only delete your own content");

        verify(contentRepository, never()).delete(any(Content.class));
    }

    @Test
    @DisplayName("Get published contents - Success")
    void getPublishedContents_Success() {
        Page<Content> contentPage = new PageImpl<>(
                List.of(testContent),
                PageRequest.of(0, 10),
                1
        );

        when(contentRepository.findByStatusOrderByCreatedAtDesc(
                eq(ContentStatus.PUBLISHED), any(PageRequest.class)))
                .thenReturn(contentPage);

        PageResponse<ContentDto> result = contentService.getPublishedContents(0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getPage()).isEqualTo(0);
    }

    @Test
    @DisplayName("Get contents by author - Success")
    void getContentsByAuthor_Success() {
        Page<Content> contentPage = new PageImpl<>(
                List.of(testContent),
                PageRequest.of(0, 10),
                1
        );

        when(contentRepository.findByAuthorIdAndStatusOrderByCreatedAtDesc(
                eq(1L), eq(ContentStatus.PUBLISHED), any(PageRequest.class)))
                .thenReturn(contentPage);

        PageResponse<ContentDto> result = contentService.getContentsByAuthor(1L, 0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getAuthor().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Increment view count")
    void incrementViewCount_Success() {
        contentService.incrementViewCount(1L);

        verify(contentRepository).incrementViewCount(1L);
    }
}
