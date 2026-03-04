package com.communication.service.impl;

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
import com.communication.service.SubscriptionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository, UserRepository userRepository, ContentRepository contentRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.contentRepository = contentRepository;
    }

    @Override
    @Transactional
    public SubscriptionDto subscribe(Long authorId, String username) {
        User subscriber = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("作者不存在"));

        if (subscriber.getId().equals(authorId)) {
            throw new BadRequestException("不能订阅自己");
        }

        if (subscriptionRepository.existsBySubscriberIdAndAuthorId(subscriber.getId(), authorId)) {
            throw new BadRequestException("已经订阅该作者");
        }

        Subscription subscription = Subscription.builder()
                .subscriber(subscriber)
                .author(author)
                .build();

        Subscription saved = subscriptionRepository.save(subscription);
        return SubscriptionDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public void unsubscribe(Long authorId, String username) {
        User subscriber = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        if (!subscriptionRepository.existsBySubscriberIdAndAuthorId(subscriber.getId(), authorId)) {
            throw new BadRequestException("未订阅该作者");
        }

        subscriptionRepository.deleteBySubscriberIdAndAuthorId(subscriber.getId(), authorId);
    }

    @Override
    public boolean isSubscribed(Long authorId, String username) {
        User subscriber = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        return subscriptionRepository.existsBySubscriberIdAndAuthorId(subscriber.getId(), authorId);
    }

    @Override
    public PageResponse<UserDto> getSubscriptions(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        Page<Subscription> subscriptions = subscriptionRepository
                .findBySubscriberIdOrderByCreatedAtDesc(user.getId(), PageRequest.of(page, size));

        List<UserDto> authors = subscriptions.getContent().stream()
                .map(s -> UserDto.fromEntity(s.getAuthor()))
                .collect(Collectors.toList());

        return PageResponse.<UserDto>builder()
                .content(authors)
                .page(page)
                .size(size)
                .totalElements(subscriptions.getTotalElements())
                .totalPages(subscriptions.getTotalPages())
                .first(subscriptions.isFirst())
                .last(subscriptions.isLast())
                .build();
    }

    @Override
    public PageResponse<UserDto> getFollowers(Long userId, int page, int size) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("用户不存在");
        }

        Page<Subscription> followers = subscriptionRepository
                .findByAuthorIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size));

        List<UserDto> users = followers.getContent().stream()
                .map(s -> UserDto.fromEntity(s.getSubscriber()))
                .collect(Collectors.toList());

        return PageResponse.<UserDto>builder()
                .content(users)
                .page(page)
                .size(size)
                .totalElements(followers.getTotalElements())
                .totalPages(followers.getTotalPages())
                .first(followers.isFirst())
                .last(followers.isLast())
                .build();
    }

    @Override
    public PageResponse<ContentDto> getSubscriptionFeed(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        List<Long> authorIds = subscriptionRepository.findAuthorIdsBySubscriberId(user.getId());

        if (authorIds.isEmpty()) {
            return PageResponse.<ContentDto>builder()
                    .content(List.of())
                    .page(page)
                    .size(size)
                    .totalElements(0)
                    .totalPages(0)
                    .first(true)
                    .last(true)
                    .build();
        }

        Page<Content> contents = contentRepository
                .findByAuthorIdInAndStatusOrderByCreatedAtDesc(authorIds, ContentStatus.PUBLISHED, PageRequest.of(page, size));

        List<ContentDto> dtos = contents.getContent().stream()
                .map(ContentDto::fromEntity)
                .collect(Collectors.toList());

        return PageResponse.<ContentDto>builder()
                .content(dtos)
                .page(page)
                .size(size)
                .totalElements(contents.getTotalElements())
                .totalPages(contents.getTotalPages())
                .first(contents.isFirst())
                .last(contents.isLast())
                .build();
    }

    @Override
    public long getSubscriptionCount(Long userId) {
        return subscriptionRepository.countBySubscriberId(userId);
    }

    @Override
    public long getFollowerCount(Long userId) {
        return subscriptionRepository.countByAuthorId(userId);
    }
}
