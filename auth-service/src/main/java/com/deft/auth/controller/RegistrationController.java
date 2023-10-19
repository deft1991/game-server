package com.deft.auth.controller;

import com.deft.auth.data.entity.AuthUser;
import com.deft.auth.data.entity.Role;
import com.deft.auth.data.redis.SessionToken;
import com.deft.auth.repo.postgres.AuthUserRepository;
import com.deft.auth.repo.postgres.RoleRepository;
import com.deft.auth.repo.redis.SessionTokenRepository;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author Sergey Golitsyn
 * created on 05.10.2023
 * <p>
 * Route /v1/auth is open and do not need security check
 * Use /register to register a new user --> return session token
 * Use /sign-in --> return session token
 */

@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class RegistrationController {

    private final SessionTokenRepository sessionTokenRepository;
    private final AuthUserRepository authUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private final List<String> defaultRoles = List.of("user");

    /**
     *
     * @param userName - user name or email
     * @param userPassword - user password
     * @return session token for user
     */
    @PostMapping("/register")
    @Observed(name = "registrationController_basicRegisterUser")
    public String basicRegisterUser(@RequestParam String userName, @RequestParam String userPassword) {
        List<Role> roles = roleRepository.findByNameIn(defaultRoles);
        AuthUser authUser = AuthUser.builder()
                .username(userName)
                // Encode the password before saving to the database
                .password(passwordEncoder.encode(userPassword))
                .enabled(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .roles(roles)
                .build();

        authUser = authUserRepository.save(authUser);


        SessionToken u = new SessionToken();
        u.setUserId(authUser.getId());
        // Encode the password before saving to the database
        SessionToken save = sessionTokenRepository.save(u);
        return save.getId();
    }

    /**
     *
     * @return session token for user
     */
    @PostMapping("/login")
    @Observed(name = "registrationController_login")
    public @ResponseBody String login(@RequestParam String userName, @RequestParam String userPassword) {
        Optional<AuthUser>  authUserOptional = authUserRepository.findByUsername(userName);
        if (authUserOptional.isEmpty()){
            throw new RuntimeException("User not found");
        }
        AuthUser authUser = authUserOptional.get();
        if (!passwordEncoder.matches(userPassword, authUser.getPassword())){
            throw new RuntimeException("Incorrect password");
        }

        SessionToken sessionToken = new SessionToken();
        sessionToken.setUserId(authUser.getId());
        sessionToken = sessionTokenRepository.save(sessionToken);
        return sessionToken.getId();
    }
}
