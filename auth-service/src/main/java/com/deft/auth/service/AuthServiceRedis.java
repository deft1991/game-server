package com.deft.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import java.util.Optional;

/**
 * @author Sergey Golitsyn
 * created on 09.10.2023
 */
public interface AuthServiceRedis {

    Optional<Authentication> authenticate(HttpServletRequest request);
}
