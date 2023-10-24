package com.deft.auth.controller.unit;

import com.deft.auth.controller.RegistrationController;
import com.deft.auth.data.dto.UserLoginDto;
import com.deft.auth.data.dto.UserRegisterDto;
import com.deft.auth.data.entity.AuthUser;
import com.deft.auth.data.redis.SessionToken;
import com.deft.auth.repo.postgres.AuthUserRepository;
import com.deft.auth.repo.postgres.RoleRepository;
import com.deft.auth.repo.redis.SessionTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class RegistrationControllerUnitTest {

    @Mock
    private SessionTokenRepository sessionTokenRepository;

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistrationController registrationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBasicRegisterUser() {
        // Create and set up mock data and behavior
        when(roleRepository.findByNameIn(anyList())).thenReturn(Collections.emptyList());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(authUserRepository.save(any())).thenReturn(new AuthUser());
        SessionToken sessionToken = new SessionToken();
        sessionToken.setId("token");
        when(sessionTokenRepository.save(any())).thenReturn(sessionToken);

        // Call the method and assert the result
        String token = registrationController.basicRegisterUser(new UserRegisterDto("testUser", "testPassword"));
        assertNotNull(token);
    }

    @Test
    void testLogin() {
        // Create and set up mock data and behavior
        AuthUser authUser = AuthUser.builder().username("testUser").password("password").build();
        when(authUserRepository.findByUsername(anyString())).thenReturn(Optional.of(authUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        SessionToken sessionToken = new SessionToken();
        sessionToken.setId("sessionToken");
        when(sessionTokenRepository.save(any())).thenReturn(sessionToken);

        // Call the method and assert the result
        String token = registrationController.login(new UserLoginDto("testUser", "testPassword"));
        assertNotNull(token);
    }

    // Add more test cases for error handling and edge cases
}
