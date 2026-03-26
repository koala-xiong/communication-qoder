package com.communication.service.impl;

import com.communication.dto.BadgeDto;
import com.communication.entity.BadgeType;
import com.communication.entity.ContentStatus;
import com.communication.entity.User;
import com.communication.entity.UserBadge;
import com.communication.exception.ResourceNotFoundException;
import com.communication.repository.*;
import com.communication.service.BadgeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BadgeServiceImpl implements BadgeService {

    private final UserBadgeRepository userBadgeRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;
    private final CommentRepository commentRepository;
    private final ReadingHistoryRepository readingHistoryRepository;

    public BadgeServiceImpl(UserBadgeRepository userBadgeRepository, UserRepository userRepository,
                            ContentRepository contentRepository, CommentRepository commentRepository,
                            ReadingHistoryRepository readingHistoryRepository) {
        this.userBadgeRepository = userBadgeRepository;
        this.userRepository = userRepository;
        this.contentRepository = contentRepository;
        this.commentRepository = commentRepository;
        this.readingHistoryRepository = readingHistoryRepository;
    }

    @Override
    public List<BadgeDto> getUserBadges(Long userId) {
        return userBadgeRepository.findByUserIdOrderByEarnedAtDesc(userId).stream()
                .map(BadgeDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void checkAndAwardBadges(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Long userId = user.getId();

        awardIfAbsent(user, BadgeType.NEWCOMER);

        if (userRepository.count() <= 100) {
            awardIfAbsent(user, BadgeType.EARLY_ADOPTER);
        }

        if (contentRepository.countByAuthorIdAndStatus(userId, ContentStatus.PUBLISHED) >= 10) {
            awardIfAbsent(user, BadgeType.ACTIVE_WRITER);
        }

        if (contentRepository.maxLikeCountByAuthorId(userId, ContentStatus.PUBLISHED) >= 50) {
            awardIfAbsent(user, BadgeType.POPULAR_AUTHOR);
        }

        if (commentRepository.countByUserId(userId) >= 50) {
            awardIfAbsent(user, BadgeType.TOP_COMMENTER);
        }

        if (readingHistoryRepository.countByUserId(userId) >= 100) {
            awardIfAbsent(user, BadgeType.LOYAL_READER);
        }
    }

    private void awardIfAbsent(User user, BadgeType type) {
        if (!userBadgeRepository.existsByUserIdAndBadgeType(user.getId(), type)) {
            userBadgeRepository.save(new UserBadge(user, type));
        }
    }
}
