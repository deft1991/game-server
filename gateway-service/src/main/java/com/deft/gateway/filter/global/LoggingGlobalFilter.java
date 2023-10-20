package com.deft.gateway.filter.global;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author Sergey Golitsyn
 * created on 13.10.2023
 */

@Slf4j
@Component
public class LoggingGlobalFilter implements GlobalFilter {

//  todo we can add diff params later
//    private static final String X_ORG_ID = "X-Org-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        Map<String, String> headers = request.getHeaders().toSingleValueMap();

        log.info("REQUEST ID: " + request.getId());
        log.info("METHOD: " + request.getMethod().name());
        log.info("PATH: " + request.getPath());
//        log.info("X-ORG-CODE: " + headers.get(X_ORG_ID));

        return chain.filter(exchange);
    }
}
