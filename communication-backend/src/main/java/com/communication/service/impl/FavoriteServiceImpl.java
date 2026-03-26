package com.communication.service.impl;

import com.communication.dto.ContentDto;
import com.communication.dto.PageResponse;
import com.communication.entity.Content;
import com.communication.entity.Favorite;
import com.communication.entity.User;
import com.communication.exception.ResourceNotFoundException;
import com.communication.repository.ContentTagRepository;
import com.communication.repository.FavoriteRepository;
import com.communication.repository.ContentRepository;
import com.communication.repository.UserRepository;
import com.communication.service.FavoriteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;
    private final ContentTagRepository contentTagRepository;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository, UserRepository userRepository,
                               ContentRepository contentRepository, ContentTagRepository contentTagRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.contentRepository = contentRepository;
        this.contentTagRepository = contentTagRepository;
    }

    @Override
    @Transactional
    public boolean toggleFavorite(Long contentId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found"));

        Optional<Favorite> existing = favoriteRepository.findByUserIdAndContentId(user.getId(), contentId);
        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            return false;
        } else {
            favoriteRepository.save(new Favorite(user, content));
            return true;
        }
    }

    @Override
    public boolean isFavorited(Long contentId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return favoriteRepository.existsByUserIdAndContentId(user.getId(), contentId);
    }

    @Override
    public Map<Long, Boolean> batchCheckFavorited(List<Long> contentIds, String username) {
        if (contentIds == null || contentIds.isEmpty()) return Collections.emptyMap();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Long> favIds = favoriteRepository.findFavoritedContentIds(user.getId(), contentIds);
        Set<Long> favSet = new HashSet<>(favIds);
        return contentIds.stream().collect(Collectors.toMap(id -> id, favSet::contains));
    }

    @Override
    public PageResponse<ContentDto> getFavoriteContents(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Page<Favorite> favs = favoriteRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), PageRequest.of(page, size));
        List<ContentDto> dtos = favs.getContent().stream()
                .map(fav -> {
                    ContentDto dto = ContentDto.fromEntity(fav.getContent());
                    dto.setTags(contentTagRepository.findByContentId(fav.getContent().getId())
                            .stream().map(t -> t.getTag()).collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
        return new PageResponse<>(dtos, favs.getNumber(), favs.getSize(),
                favs.getTotalElements(), favs.getTotalPages(), favs.isFirst(), favs.isLast());
    }

    @Override
    public long getFavoriteCount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return favoriteRepository.countByUserId(user.getId());
    }
}
