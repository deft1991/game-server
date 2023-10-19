package com.deft.gateway.filter;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * @author Sergey Golitsyn
 * created on 13.10.2023
 */

@Slf4j
@Component
@Validated
public class BeforeRedirectionFilter extends AbstractGatewayFilterFactory<BeforeRedirectionFilter.Config> {

    public BeforeRedirectionFilter() {
        super(Config.class);
    }
    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter((exchange, chain) -> {
            log.info("BEFORE REDIRECTION-------------------");
            return chain.filter(exchange);
        }, 0);
    }

    @Getter
    @Setter
    public static class Config {
        @NotEmpty
        private String name;

        @NotEmpty
        private String value;
    }
}
