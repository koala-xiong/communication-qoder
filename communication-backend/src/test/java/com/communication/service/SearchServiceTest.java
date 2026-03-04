package com.communication.service;

import com.communication.dto.ContentDto;
import com.communication.dto.PageResponse;
import com.communication.dto.UserDto;
import com.communication.entity.Content;
import com.communication.entity.ContentStatus;
import com.communication.entity.ContentTag;
import com.communication.entity.User;
import com.communication.repository.ContentRepository;
import com.communication.repository.ContentTagRepository;
import com.communication.repository.UserRepository;
import com.communication.service.impl.SearchServiceImpl;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private ContentTagRepository contentTagRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SearchServiceImpl searchService;

    private User testUser;
    private Content testContent;
    private ContentTag testTag;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        testContent = Content.builder()
                .id(1L)
                .title("Test Content")
                .body("Test body with keyword")
                .author(testUser)
                .status(ContentStatus.PUBLISHED)
                .viewCount(0)
                .commentCount(0)
                .build();

        testTag = ContentTag.builder()
                .id(1L)
                .content(testContent)
                .tag("java")
                .build();
    }

    @Test
    @DisplayName("按关键词搜索内容")
    void searchContents_ByKeyword() {
        Page<Content> contentPage = new PageImpl<>(List.of(testContent));

        when(contentRepository.searchByKeyword(eq("test"), eq(ContentStatus.PUBLISHED), any(PageRequest.class)))
                .thenReturn(contentPage);
        when(contentTagRepository.findByContentId(1L)).thenReturn(List.of(testTag));

        PageResponse<ContentDto> result = searchService.searchContents("test", null, 0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Content");
        assertThat(result.getContent().get(0).getTags()).contains("java");
    }

    @Test
    @DisplayName("按标签搜索内容")
    void searchContents_ByTag() {
        Page<Content> contentPage = new PageImpl<>(List.of(testContent));

        when(contentTagRepository.findContentIdsByTag("java")).thenReturn(List.of(1L));
        when(contentRepository.findByIdInAndStatus(eq(List.of(1L)), eq(ContentStatus.PUBLISHED), any(PageRequest.class)))
                .thenReturn(contentPage);
        when(contentTagRepository.findByContentId(1L)).thenReturn(List.of(testTag));

        PageResponse<ContentDto> result = searchService.searchContents(null, "java", 0, 10);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("按标签搜索 - 无结果")
    void searchContents_ByTag_NoResults() {
        when(contentTagRepository.findContentIdsByTag("unknown")).thenReturn(List.of());

        PageResponse<ContentDto> result = searchService.searchContents(null, "unknown", 0, 10);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("无搜索条件返回最新内容")
    void searchContents_NoKeyword_ReturnsLatest() {
        Page<Content> contentPage = new PageImpl<>(List.of(testContent));

        when(contentRepository.findByStatusOrderByCreatedAtDesc(eq(ContentStatus.PUBLISHED), any(PageRequest.class)))
                .thenReturn(contentPage);
        when(contentTagRepository.findByContentId(1L)).thenReturn(List.of());

        PageResponse<ContentDto> result = searchService.searchContents(null, null, 0, 10);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("搜索用户")
    void searchUsers_Success() {
        Page<User> userPage = new PageImpl<>(List.of(testUser));

        when(userRepository.searchByUsername(eq("test"), any(PageRequest.class))).thenReturn(userPage);

        PageResponse<UserDto> result = searchService.searchUsers("test", 0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("搜索用户 - 空关键词")
    void searchUsers_EmptyKeyword() {
        PageResponse<UserDto> result = searchService.searchUsers("", 0, 10);

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("获取热门标签")
    void getPopularTags_Success() {
        when(contentTagRepository.findPopularTags())
                .thenReturn(List.of(
                        new Object[]{"java", 10L},
                        new Object[]{"spring", 8L},
                        new Object[]{"vue", 5L}
                ));

        List<String> result = searchService.getPopularTags(3);

        assertThat(result).containsExactly("java", "spring", "vue");
    }

    @Test
    @DisplayName("标签建议")
    void suggestTags_Success() {
        when(contentTagRepository.findTagsByKeyword("ja")).thenReturn(List.of("java", "javascript"));

        List<String> result = searchService.suggestTags("ja");

        assertThat(result).containsExactly("java", "javascript");
    }

    @Test
    @DisplayName("标签建议 - 空关键词")
    void suggestTags_EmptyKeyword() {
        List<String> result = searchService.suggestTags("");

        assertThat(result).isEmpty();
    }
}
