package com.communication.service;

import com.communication.dto.ContentDto;
import com.communication.dto.PageResponse;
import com.communication.dto.SubscriptionDto;
import com.communication.dto.UserDto;
import com.communication.entity.Content;
import com.communication.entity.ContentStatus;
import com.communication.entity.Subscription;
import com.communication.entity.User;
import com.communication.exception.BadRequestException;
import com.communication.exception.ResourceNotFoundException;
import com.communication.repository.ContentRepository;
import com.communication.repository.SubscriptionRepository;
import com.communication.repository.UserRepository;
import com.communication.service.impl.SubscriptionServiceImpl;
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
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ContentRepository contentRepository;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    private User subscriber;
    private User author;
    private Subscription subscription;

    @BeforeEach
    void setUp() {
        subscriber = User.builder()
                .id(1L)
                .username("subscriber")
                .email("subscriber@example.com")
                .build();

        author = User.builder()
                .id(2L)
                .username("author")
                .email("author@example.com")
                .build();

        subscription = Subscription.builder()
                .id(1L)
                .subscriber(subscriber)
                .author(author)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("订阅成功")
    void subscribe_Success() {
        when(userRepository.findByUsername("subscriber")).thenReturn(Optional.of(subscriber));
        when(userRepository.findById(2L)).thenReturn(Optional.of(author));
        when(subscriptionRepository.existsBySubscriberIdAndAuthorId(1L, 2L)).thenReturn(false);
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);

        SubscriptionDto result = subscriptionService.subscribe(2L, "subscriber");

        assertThat(result).isNotNull();
        assertThat(result.getAuthor().getUsername()).isEqualTo("author");
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    @DisplayName("订阅失败 - 不能订阅自己")
    void subscribe_CannotSubscribeSelf() {
        when(userRepository.findByUsername("subscriber")).thenReturn(Optional.of(subscriber));
        when(userRepository.findById(1L)).thenReturn(Optional.of(subscriber));

        assertThatThrownBy(() -> subscriptionService.subscribe(1L, "subscriber"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("不能订阅自己");
    }

    @Test
    @DisplayName("订阅失败 - 已订阅")
    void subscribe_AlreadySubscribed() {
        when(userRepository.findByUsername("subscriber")).thenReturn(Optional.of(subscriber));
        when(userRepository.findById(2L)).thenReturn(Optional.of(author));
        when(subscriptionRepository.existsBySubscriberIdAndAuthorId(1L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> subscriptionService.subscribe(2L, "subscriber"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("已经订阅该作者");
    }

    @Test
    @DisplayName("取消订阅成功")
    void unsubscribe_Success() {
        when(userRepository.findByUsername("subscriber")).thenReturn(Optional.of(subscriber));
        when(subscriptionRepository.existsBySubscriberIdAndAuthorId(1L, 2L)).thenReturn(true);

        subscriptionService.unsubscribe(2L, "subscriber");

        verify(subscriptionRepository).deleteBySubscriberIdAndAuthorId(1L, 2L);
    }

    @Test
    @DisplayName("取消订阅失败 - 未订阅")
    void unsubscribe_NotSubscribed() {
        when(userRepository.findByUsername("subscriber")).thenReturn(Optional.of(subscriber));
        when(subscriptionRepository.existsBySubscriberIdAndAuthorId(1L, 2L)).thenReturn(false);

        assertThatThrownBy(() -> subscriptionService.unsubscribe(2L, "subscriber"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("未订阅该作者");
    }

    @Test
    @DisplayName("检查订阅状态")
    void isSubscribed_True() {
        when(userRepository.findByUsername("subscriber")).thenReturn(Optional.of(subscriber));
        when(subscriptionRepository.existsBySubscriberIdAndAuthorId(1L, 2L)).thenReturn(true);

        boolean result = subscriptionService.isSubscribed(2L, "subscriber");

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("获取关注列表")
    void getSubscriptions_Success() {
        Page<Subscription> page = new PageImpl<>(List.of(subscription));

        when(userRepository.findByUsername("subscriber")).thenReturn(Optional.of(subscriber));
        when(subscriptionRepository.findBySubscriberIdOrderByCreatedAtDesc(eq(1L), any(PageRequest.class)))
                .thenReturn(page);

        PageResponse<UserDto> result = subscriptionService.getSubscriptions("subscriber", 0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("author");
    }

    @Test
    @DisplayName("获取粉丝列表")
    void getFollowers_Success() {
        Page<Subscription> page = new PageImpl<>(List.of(subscription));

        when(userRepository.existsById(2L)).thenReturn(true);
        when(subscriptionRepository.findByAuthorIdOrderByCreatedAtDesc(eq(2L), any(PageRequest.class)))
                .thenReturn(page);

        PageResponse<UserDto> result = subscriptionService.getFollowers(2L, 0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("subscriber");
    }

    @Test
    @DisplayName("获取订阅动态 - 有订阅")
    void getSubscriptionFeed_WithSubscriptions() {
        Content content = Content.builder()
                .id(1L)
                .title("Test")
                .author(author)
                .status(ContentStatus.PUBLISHED)
                .viewCount(0)
                .commentCount(0)
                .build();

        Page<Content> contentPage = new PageImpl<>(List.of(content));

        when(userRepository.findByUsername("subscriber")).thenReturn(Optional.of(subscriber));
        when(subscriptionRepository.findAuthorIdsBySubscriberId(1L)).thenReturn(List.of(2L));
        when(contentRepository.findByAuthorIdInAndStatusOrderByCreatedAtDesc(
                eq(List.of(2L)), eq(ContentStatus.PUBLISHED), any(PageRequest.class)))
                .thenReturn(contentPage);

        PageResponse<ContentDto> result = subscriptionService.getSubscriptionFeed("subscriber", 0, 10);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("获取订阅动态 - 无订阅")
    void getSubscriptionFeed_NoSubscriptions() {
        when(userRepository.findByUsername("subscriber")).thenReturn(Optional.of(subscriber));
        when(subscriptionRepository.findAuthorIdsBySubscriberId(1L)).thenReturn(List.of());

        PageResponse<ContentDto> result = subscriptionService.getSubscriptionFeed("subscriber", 0, 10);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("获取订阅数统计")
    void getSubscriptionCount_Success() {
        when(subscriptionRepository.countBySubscriberId(1L)).thenReturn(5L);

        long count = subscriptionService.getSubscriptionCount(1L);

        assertThat(count).isEqualTo(5);
    }

    @Test
    @DisplayName("获取粉丝数统计")
    void getFollowerCount_Success() {
        when(subscriptionRepository.countByAuthorId(2L)).thenReturn(10L);

        long count = subscriptionService.getFollowerCount(2L);

        assertThat(count).isEqualTo(10);
    }
}
