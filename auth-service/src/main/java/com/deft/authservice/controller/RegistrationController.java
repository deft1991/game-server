package com.deft.authservice.controller;

import com.deft.authservice.data.entity.AuthUser;
import com.deft.authservice.data.entity.Role;
import com.deft.authservice.data.redis.UserRedis;
import com.deft.authservice.repo.AuthUserRepository;
import com.deft.authservice.repo.RoleRepository;
import com.deft.authservice.repo.redis.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Sergey Golitsyn
 * created on 05.10.2023
 */

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserRepository userRepository;
    private final AuthUserRepository authUserRepository;
    private final RoleRepository roleRepository;

    private final List<String> defaultRoles = List.of("user");

    @PostMapping("/register")
    public String basicRegisterUser() {
        List<Role> roles = roleRepository.findByNameIn(defaultRoles);
        AuthUser authUser = AuthUser.builder()
                .username("email.here")
                // Encode the password before saving to the database
                .password("password.here") // todo add encoder
                .enabled(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .roles(roles)
                .build();

        authUser = authUserRepository.save(authUser);


        UserRedis u = new UserRedis();
        u.setUserId(authUser.getId());
        List<String> roleNames = authUser
                .getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toList());
        u.setRoles(roleNames);
        // Encode the password before saving to the database
        UserRedis save = userRepository.save(u);
        return save.getId();
    }

    @PostMapping("/sign-in")
    public @ResponseBody String signIn(@RequestParam String userName, @RequestParam String userPassword) {
        Optional<AuthUser>  authUserOptional = authUserRepository.findByUsernameAndPassword(userName, userPassword);
        if (authUserOptional.isEmpty()){
            throw new RuntimeException("User not found");
        }

        List<String> roles = authUserOptional
                .get()
                .getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toList());
        UserRedis userRedis = new UserRedis();
        userRedis.setRoles(roles);
        userRedis = userRepository.save(userRedis);
        return userRedis.getId();
    }
}
