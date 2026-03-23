package com.communication.service.impl;

import com.communication.dto.*;
import com.communication.entity.User;
import com.communication.exception.ResourceNotFoundException;
import com.communication.repository.UserRepository;
import com.communication.service.UserAccountPersistenceService;
import com.communication.service.UserService;
import com.communication.util.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserAccountPersistenceService userAccountPersistenceService;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            UserAccountPersistenceService userAccountPersistenceService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userAccountPersistenceService = userAccountPersistenceService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        User user = userAccountPersistenceService.persistNewUser(request);
        String token = jwtUtil.generateToken(user.getUsername());
        return AuthResponse.of(token, UserDto.fromEntity(user));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsernameOrEmail())
                .or(() -> userRepository.findByEmail(request.getUsernameOrEmail()))
                .orElseThrow(() -> new BadCredentialsException("用户不存在"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("密码错误");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return AuthResponse.of(token, UserDto.fromEntity(user));
    }

    @Override
    public UserDto getCurrentUser(String username) {
        User user = findByUsername(username);
        return UserDto.fromEntity(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
