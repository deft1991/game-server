package com.deft.gateway.filter.global;

import com.deft.gateway.data.SessionToken;
import com.deft.gateway.service.RouterValidator;
import com.deft.gateway.service.SessionTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.StringTokenizer;

/**
 * @author Sergey Golitsyn
 * created on 16.10.2023
 */
@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter {

    private final RouterValidator routerValidator;//custom route validator
    private final SessionTokenService sessionTokenService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (routerValidator.isSecured.test(request)) {
            if (isAuthMissing(request)) {
                return onError(exchange, "Authorization header is missing in request", HttpStatus.UNAUTHORIZED);
            }

            final String token = extractBearerToken(getAuthHeader(request));

            // Get the sessionToken by ID from the cache service
            return sessionTokenService.getSessionToken(token)
                    .flatMap(sessionToken -> {
                        if (sessionToken != null && sessionToken.getUserId() != null) {
                            // User exists, populate request with headers
                            populateRequestWithHeaders(exchange, sessionToken);
                            // Continue the filter chain
                            return chain.filter(exchange);
                        } else {
                            // User is empty, return an error response
                            return onError(exchange, "Authorization token is invalid", HttpStatus.UNAUTHORIZED);
                        }
                    })
                    .then(Mono.empty()); // Return an empty Mono, indicating the filter chain has completed
        }

        return chain.filter(exchange);
    }


    /*PRIVATE*/

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        DataBufferFactory bufferFactory = response.bufferFactory();
        return response.writeWith(Mono.just(bufferFactory.wrap(err.getBytes())));
    }

    private String getAuthHeader(ServerHttpRequest request) {
        return request.getHeaders().getOrEmpty("Authorization").get(0);
    }

    private boolean isAuthMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization");
    }

    private void populateRequestWithHeaders(ServerWebExchange exchange, SessionToken token) {
        exchange.getRequest()
                .mutate()
                .header("userId", String.valueOf(token.getUserId()))
                // todo add roles here
//                .header("role", String.valueOf(claims.get("role")))
                .build();
    }

    /**
     * Extract token from Authorization header
     */
    public static String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader != null) {
            StringTokenizer tokenizer = new StringTokenizer(authorizationHeader);
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if ("Bearer".equals(token) && tokenizer.hasMoreTokens()) {
                    return tokenizer.nextToken();
                }
            }
        }
        // Handle the case where the header is not in the expected format
        return null;
    }
}
