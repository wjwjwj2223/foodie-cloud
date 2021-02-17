package com.imooc;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.
        cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.
        cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.
        cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class RoutesConfiguration {

    @Autowired
    private KeyResolver hostNameResolver;

    @Autowired
    @Qualifier("redisLimiterUser")
    private RateLimiter userRateLimiter;

    @Autowired
    @Qualifier("redisLimiterItem")
    private RateLimiter itemRateLimiter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return  builder.routes()
                .route(r -> r.path(
                        "/address/**",
                        "/passport/**",
                        "/userinfo/**",
                        "/center/**")
                        .filters(f ->
                                f.requestRateLimiter(c ->
                                        c.setKeyResolver(hostNameResolver)
                                                .setRateLimiter(userRateLimiter)
                                                .setStatusCode(HttpStatus.BAD_GATEWAY)
                                ))
                        .uri("lb://FOODIE-USER-SERVICE")
                ).route(r -> r.path("/items/**")
                        .filters(f ->
                                f.requestRateLimiter(c ->
                                        c.setKeyResolver(hostNameResolver)
                                        .setRateLimiter(itemRateLimiter)
                                        .setStatusCode(HttpStatus.BAD_GATEWAY)
                                ))
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
