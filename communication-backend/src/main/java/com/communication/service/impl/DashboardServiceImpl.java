package com.communication.service.impl;

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
import com.communication.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final ContentRepository contentRepository;
    private final CommentRepository commentRepository;
    private final SubscriptionRepository subscriptionRepository;

    public DashboardServiceImpl(UserRepository userRepository, ContentRepository contentRepository, 
                                CommentRepository commentRepository, SubscriptionRepository subscriptionRepository) {
        this.userRepository = userRepository;
        this.contentRepository = contentRepository;
        this.commentRepository = commentRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public DashboardStatsDto getStats(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        Long userId = user.getId();

        long totalContents = contentRepository.countByAuthorId(userId);
        long publishedContents = contentRepository.countByAuthorIdAndStatus(userId, ContentStatus.PUBLISHED);
        long draftContents = contentRepository.countByAuthorIdAndStatus(userId, ContentStatus.DRAFT);
        Long totalViews = contentRepository.sumViewCountByAuthorId(userId);
        Long totalComments = contentRepository.sumCommentCountByAuthorId(userId);
        long followerCount = subscriptionRepository.countByAuthorId(userId);
        long followingCount = subscriptionRepository.countBySubscriberId(userId);

        return DashboardStatsDto.builder()
                .totalContents(totalContents)
                .publishedContents(publishedContents)
                .draftContents(draftContents)
                .totalViews(totalViews != null ? totalViews : 0)
                .totalComments(totalComments != null ? totalComments : 0)
                .followerCount(followerCount)
                .followingCount(followingCount)
                .build();
    }

    @Override
    @Transactional
    public UserDto updateProfile(String username, UpdateProfileRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        user = userRepository.save(user);
        return UserDto.fromEntity(user);
    }

    @Override
    @Transactional
    public UserDto updateAvatar(String username, String avatarUrl) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        user.setAvatarUrl(avatarUrl);
        user = userRepository.save(user);
        return UserDto.fromEntity(user);
    }
}
