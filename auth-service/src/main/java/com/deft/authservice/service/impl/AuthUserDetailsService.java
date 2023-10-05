package com.deft.authservice.service.impl;

import com.deft.authservice.data.entity.AuthUser;
import com.deft.authservice.repo.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author Sergey Golitsyn
 * created on 05.10.2023
 */
@Service
@RequiredArgsConstructor
public class AuthUserDetailsService implements UserDetailsService {

    private final AuthUserRepository authUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        AuthUser user =
                authUserRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found"));

        new AccountStatusUserDetailsChecker().check(user);
        return user;
    }
}
