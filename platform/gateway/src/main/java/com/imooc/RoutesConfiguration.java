package com.imooc;


import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.
        cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutesConfiguration {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return  builder.routes()
                .route(r -> r.path(
                        "/address/**",
                        "/passport/**",
                        "/userinfo/**",
                        "/center/**")
                        .uri("lb://FOODIE-USER-SERVICE")
                ).route(r -> r.path("/items/**")
                        .uri("lb://FOODIE-ITEM-SERVICE")
                ).route(r -> r.path("/shopcart/**")
                        .uri("lb://FOODIE-CART-SERVICE")
                ).route(r -> r.path(
                        "/orders/**",
                        "/myorders/**",
                        "/mycommons/**"
                        )
                        .uri("lb://FOODIE-ORDER-SERVICE")
                ).route(r -> r.path(
                        "/search/**"
                        )
                        .uri("lb://FOODIE-SEARCH-SERVICE")
                ).build();

    }



}
