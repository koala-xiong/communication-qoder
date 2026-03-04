package com.communication.service;

import com.communication.dto.DashboardStatsDto;
import com.communication.dto.UpdateProfileRequest;
import com.communication.dto.UserDto;

public interface DashboardService {

    DashboardStatsDto getStats(String username);

    UserDto updateProfile(String username, UpdateProfileRequest request);

    UserDto updateAvatar(String username, String avatarUrl);
}
