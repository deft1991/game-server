package com.deft.authservice.controller;

import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> helloWorld() {
        return new ResponseEntity<>("Hello World", HttpStatus.OK);
    }@GetMapping("/admin")
    public ResponseEntity<String> helloWorldAdmin() {
        return new ResponseEntity<>("Hello World Admin", HttpStatus.OK);
    }
}
