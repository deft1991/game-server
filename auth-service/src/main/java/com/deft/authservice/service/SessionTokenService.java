package com.deft.authservice.service;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author Sergey Golitsyn
 * created on 05.10.2023
 */
public interface SessionTokenService {

    String generateToken(UserDetails userDetails);

    boolean isTokenValid(String token, UserDetails userDetails);

    String extractTokenFromBearer(String bearerString);
}
