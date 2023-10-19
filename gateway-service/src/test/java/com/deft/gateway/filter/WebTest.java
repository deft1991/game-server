package com.deft.gateway.filter;

import com.deft.gateway.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @author Sergey Golitsyn
 * created on 18.10.2023
 * <p>
 * Example of web test
 */

public class WebTest extends BaseTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testCustomGlobalFilterGateway() {
        webTestClient
                .get().uri("/actuator")
                .exchange()
                .expectStatus().isOk() // Assert the expected HTTP status
        ;
//                .expectBody(String.class).isEqualTo("Expected Response"); // Assert the expected response
    }
}
