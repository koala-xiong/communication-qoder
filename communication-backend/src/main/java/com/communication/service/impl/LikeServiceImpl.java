package com.communication.service.impl;

import com.communication.dto.ContentDto;
import com.communication.dto.PageResponse;
import com.communication.entity.Content;
import com.communication.entity.Like;
import com.communication.entity.User;
import com.communication.exception.ResourceNotFoundException;
import com.communication.repository.ContentRepository;
import com.communication.repository.ContentTagRepository;
import com.communication.repository.LikeRepository;
import com.communication.repository.UserRepository;
import com.communication.service.LikeService;
import com.communication.service.NotificationService;
import com.communication.entity.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;
    private final ContentTagRepository contentTagRepository;
    private final NotificationService notificationService;

    public LikeServiceImpl(LikeRepository likeRepository, UserRepository userRepository,
                           ContentRepository contentRepository, ContentTagRepository contentTagRepository,
                           NotificationService notificationService) {
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.contentRepository = contentRepository;
        this.contentTagRepository = contentTagRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public boolean toggleLike(Long contentId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found"));

        Optional<Like> existing = likeRepository.findByUserIdAndContentId(user.getId(), contentId);
        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
            content.setLikeCount(Math.max(0, content.getLikeCount() - 1));
            contentRepository.save(content);
            return false;
        } else {
            likeRepository.save(new Like(user, content));
            content.setLikeCount(content.getLikeCount() + 1);
            contentRepository.save(content);
            if (!content.getAuthor().getId().equals(user.getId())) {
                notificationService.createNotification(
                        content.getAuthor().getId(), user.getId(),
                        NotificationType.LIKE, contentId, null,
                        user.getUsername() + " 赞了你的文章「" + content.getTitle() + "」");
            }
            return true;
        }
    }

    @Override
    public boolean isLiked(Long contentId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return likeRepository.existsByUserIdAndContentId(user.getId(), contentId);
    }

    @Override
    public Map<Long, Boolean> batchCheckLiked(List<Long> contentIds, String username) {
        if (contentIds == null || contentIds.isEmpty()) return Collections.emptyMap();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Long> likedIds = likeRepository.findLikedContentIds(user.getId(), contentIds);
        Set<Long> likedSet = new HashSet<>(likedIds);
        return contentIds.stream().collect(Collectors.toMap(id -> id, likedSet::contains));
    }

    @Override
    public PageResponse<ContentDto> getLikedContents(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Page<Like> likes = likeRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), PageRequest.of(page, size));
        List<ContentDto> dtos = likes.getContent().stream()
                .map(like -> {
                    ContentDto dto = ContentDto.fromEntity(like.getContent());
                    dto.setTags(contentTagRepository.findByContentId(like.getContent().getId())
                            .stream().map(t -> t.getTag()).collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
        return new PageResponse<>(dtos, likes.getNumber(), likes.getSize(),
                likes.getTotalElements(), likes.getTotalPages(), likes.isFirst(), likes.isLast());
    }
}
