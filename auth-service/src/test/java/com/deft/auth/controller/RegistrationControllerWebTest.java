package com.deft.auth.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

/**
 * @author Sergey Golitsyn
 * created on 23.10.2023
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegistrationControllerWebTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    static void startContainers() {
        postgresContainer.start();
    }

    @Test
    void testBasicRegisterUser() {
        // Create and set up data in the database
        // Send an HTTP request using the restTemplate
        // Assert the response
    }

    @Test
    void testLogin() {
        // Create and set up data in the database
        // Send an HTTP request using the restTemplate
        // Assert the response
    }

    // Add more web test cases
}
