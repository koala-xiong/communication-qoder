package com.communication.service.impl;

import com.communication.dto.ContentDto;
import com.communication.dto.PageResponse;
import com.communication.entity.Content;
import com.communication.entity.ReadingHistory;
import com.communication.entity.User;
import com.communication.exception.ResourceNotFoundException;
import com.communication.repository.ContentRepository;
import com.communication.repository.ContentTagRepository;
import com.communication.repository.ReadingHistoryRepository;
import com.communication.repository.UserRepository;
import com.communication.service.ReadingHistoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReadingHistoryServiceImpl implements ReadingHistoryService {

    private final ReadingHistoryRepository readingHistoryRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;
    private final ContentTagRepository contentTagRepository;

    public ReadingHistoryServiceImpl(ReadingHistoryRepository readingHistoryRepository,
                                     UserRepository userRepository, ContentRepository contentRepository,
                                     ContentTagRepository contentTagRepository) {
        this.readingHistoryRepository = readingHistoryRepository;
        this.userRepository = userRepository;
        this.contentRepository = contentRepository;
        this.contentTagRepository = contentTagRepository;
    }

    @Override
    @Transactional
    public void recordReading(Long contentId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found"));

        Optional<ReadingHistory> existing = readingHistoryRepository.findByUserIdAndContentId(user.getId(), contentId);
        if (existing.isPresent()) {
            existing.get().setReadAt(LocalDateTime.now());
            readingHistoryRepository.save(existing.get());
        } else {
            readingHistoryRepository.save(new ReadingHistory(user, content));
        }
    }

    @Override
    public PageResponse<ContentDto> getReadingHistory(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Page<ReadingHistory> history = readingHistoryRepository
                .findByUserIdOrderByReadAtDesc(user.getId(), PageRequest.of(page, size));
        List<ContentDto> dtos = history.getContent().stream()
                .map(rh -> {
                    ContentDto dto = ContentDto.fromEntity(rh.getContent());
                    dto.setTags(contentTagRepository.findByContentId(rh.getContent().getId())
                            .stream().map(t -> t.getTag()).collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
        return new PageResponse<>(dtos, history.getNumber(), history.getSize(),
                history.getTotalElements(), history.getTotalPages(),
                history.isFirst(), history.isLast());
    }

    @Override
    @Transactional
    public void clearHistory(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        readingHistoryRepository.deleteAllByUserId(user.getId());
    }
}
