package com.deft.auth.service.impl.integration;

import com.deft.auth.data.entity.AuthUser;
import com.deft.auth.repo.postgres.AuthUserRepository;
import com.deft.auth.service.impl.AuthUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Sergey Golitsyn
 * created on 23.10.2023
 */

@SpringBootTest
@Import(AuthUserDetailsService.class)
@ContextConfiguration(initializers = AuthUserDetailsServiceIntegrationTest.Initializer.class)
@Testcontainers
public class AuthUserDetailsServiceIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private AuthUserDetailsService authUserDetailsService;

    @BeforeEach
    void setUp() {
        AuthUser authUser = AuthUser.builder()
                .username("testUser")
                .password("password")
                .enabled(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .build();
        authUserRepository.save(authUser);
    }

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        UserDetails userDetails = authUserDetailsService.loadUserByUsername("testUser");
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("testUser");
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        assertThatThrownBy(() -> authUserDetailsService.loadUserByUsername("nonExistentUser"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
