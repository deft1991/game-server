package com.deft.gateway.configuration;

import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

/**
 * @author Sergey Golitsyn
 * created on 20.10.2023
 */
public class GatewayExceptionHandler implements WebExceptionHandler {

    @Override
    public @NonNull Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable ex) {
        if (ex instanceof java.net.ConnectException) {
            return handleConnectException(exchange, (java.net.ConnectException) ex);
        }
        // Handle other exceptions if needed

        // If no match, delegate to the default error handler
        return Mono.error(ex);
    }

    private Mono<Void> handleConnectException(ServerWebExchange exchange, java.net.ConnectException ex) {
        ServerHttpResponse response = exchange.getResponse();

        // Customize the response message and status code for Connection refused
        response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        String errorMessage = "Service is temporarily unavailable due to a connection issue.";
        DataBufferFactory bufferFactory = response.bufferFactory();
        return response.writeWith(Mono.just(bufferFactory.wrap(errorMessage.getBytes())));
    }
}
