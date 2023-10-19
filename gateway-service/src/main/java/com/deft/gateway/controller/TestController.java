package com.deft.gateway.controller;

import com.deft.gateway.data.SessionToken;
import com.deft.gateway.service.SessionTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author Sergey Golitsyn
 * created on 18.10.2023
 */

@Slf4j
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final SessionTokenService sessionTokenService;

    @GetMapping("/hello-world")
    public ResponseEntity<String> helloWorld() {
        return new ResponseEntity<>("Hello World", HttpStatus.OK);
    }

    @GetMapping("/get-value")
    public Mono<SessionToken> getValueFromRedis(@RequestParam String key) {
        return sessionTokenService.getSessionToken(key);
    }

    @GetMapping("/has-key")
    public Mono<Boolean> hasKey(@RequestParam String key) {
        return sessionTokenService.hasKey(key);
    }
}
