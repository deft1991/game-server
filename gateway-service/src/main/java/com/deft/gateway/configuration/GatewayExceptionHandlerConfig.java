package com.deft.gateway.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.gateway.support.TimeoutException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

/**
 * @author Sergey Golitsyn
 * created on 20.10.2023
 */
@Configuration
public class GatewayExceptionHandlerConfig {

    @Bean
    @Qualifier("gatewayExceptionHandler")
    public WebExceptionHandler gatewayExceptionHandler(ErrorAttributes errorAttributes) {
        return new GatewayExceptionHandler();
    }

    @Bean
    public ErrorWebExceptionHandler errorWebExceptionHandler(
            @Qualifier("gatewayExceptionHandler") WebExceptionHandler gatewayExceptionHandler) {
        return (exchange, ex) -> {
            if (ex instanceof NotFoundException || ex instanceof TimeoutException) {
                return gatewayExceptionHandler.handle(exchange, ex);
            } else {
                return Mono.error(ex);
            }
        };
    }
}
