package com.deft.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @author Sergey Golitsyn
 * created on 13.10.2023
 */

@Slf4j
@Component
public class PrePostFilter implements WebFilter {


    private static final String X_REQUEST_UUID = "X-REQUEST-UUID";

    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String UUID = java.util.UUID.randomUUID().toString();

        log.info("INSIDE INBOUND FILTER");

        ServerHttpRequest request = exchange.getRequest().mutate()
                .header(X_REQUEST_UUID, UUID)
                .build();

        log.info("X_PORTAL_ID Header included");

        return chain.filter(exchange.mutate().request(request).build())
                .transformDeferred((call) -> call.doFinally(signalType -> {
                    ServerHttpResponse response = exchange.getResponse();

                    log.info("Response status: " + response.getStatusCode());
                    log.info("AFTER REQUEST POST FILTER");
                }));
    }
}
