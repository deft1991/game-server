package com.deft.authservice.controller;

import com.deft.authservice.data.entity.AuthUser;
import com.deft.authservice.data.entity.Role;
import com.deft.authservice.data.redis.SessionToken;
import com.deft.authservice.repo.postgres.AuthUserRepository;
import com.deft.authservice.repo.postgres.RoleRepository;
import com.deft.authservice.repo.redis.SessionTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author Sergey Golitsyn
 * created on 05.10.2023
 */

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class RegistrationController {

    private final SessionTokenRepository sessionTokenRepository;
    private final AuthUserRepository authUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private final List<String> defaultRoles = List.of("user");

    @PostMapping("/register")
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

    @PostMapping("/sign-in")
    public @ResponseBody String signIn(@RequestParam String userName, @RequestParam String userPassword) {
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
