package com.deft.auth.controller;

import io.micrometer.core.annotation.Timed;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sergey Golitsyn
 * created on 05.10.2023
 */

@Slf4j
@RestController
@RequestMapping("/hello-world")
@RequiredArgsConstructor
@Observed(name = "HelloWorldController")
public class HelloWorldController {

    @GetMapping
    @Observed(name = "helloWorldController_helloWorld")
    @Timed("helloWorldController.timed")
    public ResponseEntity<String> helloWorld() throws InterruptedException {
        double v = Math.random() * 100;
        Thread.sleep((long) v);
        return new ResponseEntity<>("Hello World", HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE_admin')")
    @GetMapping("/admin")
    @Observed(name = "helloWorldController_helloWorldAdmin")
    public ResponseEntity<String> helloWorldAdmin() {
        return new ResponseEntity<>("Hello World Admin", HttpStatus.OK);
    }
}
