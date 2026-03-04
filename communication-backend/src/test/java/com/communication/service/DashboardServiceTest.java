package com.communication.service;

import com.communication.dto.DashboardStatsDto;
import com.communication.dto.UpdateProfileRequest;
import com.communication.dto.UserDto;
import com.communication.entity.ContentStatus;
import com.communication.entity.User;
import com.communication.exception.ResourceNotFoundException;
import com.communication.repository.CommentRepository;
import com.communication.repository.ContentRepository;
import com.communication.repository.SubscriptionRepository;
import com.communication.repository.UserRepository;
import com.communication.service.impl.DashboardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .bio("Hello")
                .build();
    }

    @Test
    @DisplayName("获取统计数据成功")
    void getStats_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(contentRepository.countByAuthorId(1L)).thenReturn(10L);
        when(contentRepository.countByAuthorIdAndStatus(1L, ContentStatus.PUBLISHED)).thenReturn(8L);
        when(contentRepository.countByAuthorIdAndStatus(1L, ContentStatus.DRAFT)).thenReturn(2L);
        when(contentRepository.sumViewCountByAuthorId(1L)).thenReturn(1000L);
        when(contentRepository.sumCommentCountByAuthorId(1L)).thenReturn(50L);
        when(subscriptionRepository.countByAuthorId(1L)).thenReturn(100L);
        when(subscriptionRepository.countBySubscriberId(1L)).thenReturn(20L);

        DashboardStatsDto result = dashboardService.getStats("testuser");

        assertThat(result.getTotalContents()).isEqualTo(10);
        assertThat(result.getPublishedContents()).isEqualTo(8);
        assertThat(result.getDraftContents()).isEqualTo(2);
        assertThat(result.getTotalViews()).isEqualTo(1000);
        assertThat(result.getTotalComments()).isEqualTo(50);
        assertThat(result.getFollowerCount()).isEqualTo(100);
        assertThat(result.getFollowingCount()).isEqualTo(20);
    }

    @Test
    @DisplayName("获取统计数据 - 用户不存在")
    void getStats_UserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dashboardService.getStats("unknown"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("用户不存在");
    }

    @Test
    @DisplayName("获取统计数据 - 无内容时返回0")
    void getStats_NoContent() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(contentRepository.countByAuthorId(1L)).thenReturn(0L);
        when(contentRepository.countByAuthorIdAndStatus(1L, ContentStatus.PUBLISHED)).thenReturn(0L);
        when(contentRepository.countByAuthorIdAndStatus(1L, ContentStatus.DRAFT)).thenReturn(0L);
        when(contentRepository.sumViewCountByAuthorId(1L)).thenReturn(null);
        when(contentRepository.sumCommentCountByAuthorId(1L)).thenReturn(null);
        when(subscriptionRepository.countByAuthorId(1L)).thenReturn(0L);
        when(subscriptionRepository.countBySubscriberId(1L)).thenReturn(0L);

        DashboardStatsDto result = dashboardService.getStats("testuser");

        assertThat(result.getTotalViews()).isZero();
        assertThat(result.getTotalComments()).isZero();
    }

    @Test
    @DisplayName("更新个人资料成功")
    void updateProfile_Success() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setBio("New bio");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDto result = dashboardService.updateProfile("testuser", request);

        assertThat(result).isNotNull();
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("更新个人资料 - 用户不存在")
    void updateProfile_UserNotFound() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dashboardService.updateProfile("unknown", request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("更新头像成功")
    void updateAvatar_Success() {
        String avatarUrl = "/uploads/avatars/test.jpg";

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDto result = dashboardService.updateAvatar("testuser", avatarUrl);

        assertThat(result).isNotNull();
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("更新头像 - 用户不存在")
    void updateAvatar_UserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dashboardService.updateAvatar("unknown", "/test.jpg"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
