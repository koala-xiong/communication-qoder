package com.communication.service;

import com.communication.dto.RegisterRequest;
import com.communication.entity.User;
import com.communication.exception.BadRequestException;
import com.communication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAccountPersistenceServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserAccountPersistenceService persistenceService;

    private RegisterRequest registerRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("new@example.com");
        registerRequest.setPassword("password123");

        savedUser = User.builder()
                .id(1L)
                .username("newuser")
                .email("new@example.com")
                .password("encoded")
                .build();
    }

    @Test
    @DisplayName("persistNewUser - saves and flushes to database")
    void persistNewUser_Success() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(savedUser);

        User result = persistenceService.persistNewUser(registerRequest);

        assertThat(result).isEqualTo(savedUser);
        verify(userRepository).saveAndFlush(any(User.class));
    }

    @Test
    @DisplayName("persistNewUser - username taken")
    void persistNewUser_UsernameExists() {
        when(userRepository.existsByUsername("newuser")).thenReturn(true);

        assertThatThrownBy(() -> persistenceService.persistNewUser(registerRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Username already exists");

        verify(userRepository, never()).saveAndFlush(any(User.class));
    }

    @Test
    @DisplayName("persistNewUser - email taken")
    void persistNewUser_EmailExists() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(true);

        assertThatThrownBy(() -> persistenceService.persistNewUser(registerRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Email already exists");

        verify(userRepository, never()).saveAndFlush(any(User.class));
    }
}
