package com.deft.authservice.repo;

import com.deft.authservice.data.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Sergey Golitsyn
 * created on 05.10.2023
 */
@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, String> {
    Optional<AuthUser> findByUsername(String username);
    Optional<AuthUser> findByUsernameAndPassword(String username, String password);
}
