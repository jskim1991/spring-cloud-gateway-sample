package io.jay.gateway;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import static io.jay.gateway.AuthorizationHeaderFilterFactory.*;

@Component
public class AuthorizationHeaderFilterFactory extends AbstractGatewayFilterFactory<Config> {

    public AuthorizationHeaderFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter((exchange, chain) -> {

            System.out.println("AuthorizationHeaderFilterFactory pre-filter with order of 9876");

            return chain.filter(exchange);
        }, 9876);
    }

    public static class Config {

    }
}
