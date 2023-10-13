package com.deft.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

/**
 * @author Sergey Golitsyn
 * created on 13.10.2023
 */

@Slf4j
@Component
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

    public static class Config {
        private String name;

        public Config(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
