package io.jay.gateway;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Configuration
public class CustomFilterConfiguration {

    @Bean
    @Order(-1)
    public GlobalFilter preFilter() {
        return (exchange, chain) ->
                exchange.getPrincipal()
                        .map(Principal::getName)
                        .defaultIfEmpty("Jay")
                        .map(name -> {
                            exchange.getRequest()
                                    .mutate()
                                    .header("custom-user", name);
                            return exchange;
                        })
                        .map(serverWebExchange -> {
                            System.out.println("pre-filter with order of -1");

                            var request = serverWebExchange.getRequest();
                            // can access the request

                            return serverWebExchange;
                        })
                        .flatMap(chain::filter);
    }

    @Bean
    @Order(-1)
    public GlobalFilter postFilter() {
        return (exchange, chain) -> chain.filter(exchange)
                .then(Mono.just(exchange))
                .map(serverWebExchange -> {
                    serverWebExchange.getResponse()
                            .getHeaders()
                            .set("custom-response-header", serverWebExchange.getRequest().getHeaders().get("custom-user").get(0));
                    return serverWebExchange;
                })
                .map(serverWebExchange -> {
                    System.out.println("post-filter with order of -1");

                    var response = serverWebExchange.getResponse();
                    // can access the response

                    return serverWebExchange;
                })
                .then();
    }

    @Bean
    @Order(0)
    public GlobalFilter prePostFilter() {
        return ((exchange, chain) -> {
            System.out.println("pre-filter with order of 0");
            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        System.out.println("post-filter with order of 0");
                    }));
        });
    }
}