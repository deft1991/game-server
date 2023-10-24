package com.deft.auth.controller.web;

import com.deft.auth.data.dto.UserRegisterDto;
import com.deft.auth.data.entity.AuthUser;
import com.deft.auth.data.entity.Role;
import com.deft.auth.data.redis.SessionToken;
import com.deft.auth.repo.postgres.AuthUserRepository;
import com.deft.auth.repo.postgres.RoleRepository;
import com.deft.auth.repo.redis.SessionTokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * @author Sergey Golitsyn
 * created on 23.10.2023
 */
@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SessionTokenRepository sessionTokenRepository;

    @MockBean
    private AuthUserRepository authUserRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        // Mock any necessary interactions with repositories and services
    }

    @Test
    void testBasicRegisterUser_ValidRequest() throws Exception {
        // Prepare a valid request body as a UserRegisterDto
        UserRegisterDto requestDto = new UserRegisterDto("testUser", "testPassword");
        AuthUser authUser = AuthUser.builder().build();
        SessionToken sessionToken = new SessionToken();
        sessionToken.setId("token");
        List<Role> defaultRoles = new ArrayList<>();
        // Mock repository and service interactions
        when(roleRepository.findByNameIn(anyList())).thenReturn(defaultRoles);
        when(authUserRepository.save(any())).thenReturn(authUser);
        when(sessionTokenRepository.save(any())).thenReturn(sessionToken);

        // Convert the requestDto to a JSON string
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // Send a POST request to the endpoint and assert the response
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/auth/register")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testLogin_ValidRequest() throws Exception {
        // Prepare a valid request body as a UserRegisterDto
        UserRegisterDto requestDto = new UserRegisterDto("testUser", "testPassword");
        AuthUser authUser = AuthUser.builder().password("hashedPassword").build();
        Optional<AuthUser> optionalUser = Optional.of(authUser);
        SessionToken sessionToken = new SessionToken();
        sessionToken.setId("token");
        // Mock repository and service interactions
        when(authUserRepository.findByUsername("testUser")).thenReturn(optionalUser);
        when(passwordEncoder.matches("testPassword", "hashedPassword")).thenReturn(true);
        when(sessionTokenRepository.save(any())).thenReturn(sessionToken);

        // Convert the requestDto to a JSON string
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // Send an HTTP POST request to the endpoint and assert the response
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/auth/login")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    // Add more web test cases for error handling and edge cases
}
