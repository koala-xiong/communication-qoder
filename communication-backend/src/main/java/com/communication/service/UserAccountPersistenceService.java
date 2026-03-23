package com.communication.service;

import com.communication.dto.RegisterRequest;
import com.communication.entity.User;
import com.communication.exception.BadRequestException;
import com.communication.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 注册时单独事务持久化用户：先提交到当前数据源（如 MySQL），再返回给上层签发 JWT，
 * 避免签发失败导致整笔事务回滚、用户未写入数据库。
 */
@Service
public class UserAccountPersistenceService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountPersistenceService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public User persistNewUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        return userRepository.saveAndFlush(user);
    }
}
