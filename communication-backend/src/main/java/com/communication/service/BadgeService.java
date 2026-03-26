package com.communication.service;

import com.communication.dto.BadgeDto;

import java.util.List;

public interface BadgeService {

    List<BadgeDto> getUserBadges(Long userId);

    void checkAndAwardBadges(String username);
}
