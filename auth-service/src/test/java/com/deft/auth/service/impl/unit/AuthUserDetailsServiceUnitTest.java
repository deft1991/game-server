package com.deft.auth.service.impl.unit;

import com.deft.auth.data.entity.AuthUser;
import com.deft.auth.repo.postgres.AuthUserRepository;
import com.deft.auth.service.impl.AuthUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class AuthUserDetailsServiceUnitTest {

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        AuthUserRepository authUserRepository = mock(AuthUserRepository.class);
        AuthUserDetailsService service = new AuthUserDetailsService(authUserRepository);

        AuthUser user = new AuthUser();
        user.setUsername("testUser");
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setAccountNonLocked(true);
        when(authUserRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        UserDetails userDetails = service.loadUserByUsername("testUser");

        verify(authUserRepository, times(1)).findByUsername("testUser");
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("testUser");
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        AuthUserRepository authUserRepository = mock(AuthUserRepository.class);
        AuthUserDetailsService service = new AuthUserDetailsService(authUserRepository);

        when(authUserRepository.findByUsername("nonExistentUser")).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("nonExistentUser"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void loadUserByUsername_Disabled_ThrowsException() {
        AuthUserRepository authUserRepository = mock(AuthUserRepository.class);
        AuthUserDetailsService service = new AuthUserDetailsService(authUserRepository);

        AuthUser user = new AuthUser();
        user.setUsername("testUser");
        user.setEnabled(false);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setAccountNonLocked(true);
        when(authUserRepository.findByUsername("testUser")).thenReturn(Optional.of(user));


        assertThatThrownBy(() -> service.loadUserByUsername("testUser"))
                .isInstanceOf(DisabledException.class)
                .hasMessage("User is disabled");
    }
}
