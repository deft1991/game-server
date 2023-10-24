package com.deft.auth.service.impl.unit;

import com.deft.auth.data.entity.AuthUser;
import com.deft.auth.data.redis.SessionToken;
import com.deft.auth.repo.postgres.AuthUserRepository;
import com.deft.auth.repo.redis.SessionTokenRepository;
import com.deft.auth.service.impl.AuthServiceRedisImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class AuthServiceRedisImplUnitTest {

    @Mock
    private SessionTokenRepository sessionTokenRepository;

    @Mock
    private AuthUserRepository authUserRepository;

    private AuthServiceRedisImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthServiceRedisImpl(sessionTokenRepository, authUserRepository);
    }

    @Test
    void testAuthenticate_ValidToken_ReturnsAuthentication() {
        // Create mock data and behavior for a valid authentication
        SessionToken sessionToken = new SessionToken();
        sessionToken.setId("token123");
        sessionToken.setUserId("user123");
        AuthUser authUser = new AuthUser();
        authUser.setId("user123");
        when(sessionTokenRepository.findById("token123")).thenReturn(Optional.of(sessionToken));
        when(authUserRepository.findById("user123")).thenReturn(Optional.of(authUser));

        // Call the authenticate method and assert the result
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer token123");
        Optional<Authentication> authentication = authService.authenticate(mockRequest);

        assertTrue(authentication.isPresent());
    }

    @Test
    void testAuthenticate_InvalidToken_ReturnsEmpty() {
        // Create mock data and behavior for an invalid token
        when(sessionTokenRepository.findById(any())).thenReturn(Optional.empty());

        // Call the authenticate method and assert the result
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer invalidToken");
        Optional<Authentication> authentication = authService.authenticate(mockRequest);

        assertFalse(authentication.isPresent());
    }

    // Add more test cases for error handling and edge cases
}
