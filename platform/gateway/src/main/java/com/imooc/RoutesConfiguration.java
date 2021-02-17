package com.imooc;


import com.imooc.filter.AuthFilter;
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
    public RouteLocator routes(RouteLocatorBuilder builder, AuthFilter authFilter) {

        return  builder.routes()
                //auth 在网关层有很多种做法
                //1.【最常用】 网关层或者微服务自己本地校验jwt token有效性 不向auth-service 发起远程调用
                //2.【路由配置最简单】可以吧authfilter注册为global filter 然后在authfilter
                //    中配置需要过滤的url pattern(可以从config-server配置)
                //3.【路由配置也简单】可以采用interceptor(拦截器) 的方式
                //4.【下面这种方式，最丑的方式】
                //将其他需要登录校验的接口添加到下面
                .route(r -> r.path("/address/list",
                        "/address/add",
                        "/address/update",
                        "/address/setDefault",
                        "/address/delete")
                .filters(f -> f.filter(authFilter))
                .uri("lb://FOODIE-USER-SERVICE")
                ).route(r -> r.path("/auth-service/refresh")
                        .uri("lb://FOODIE-AUTH-SERVICE")
                ).route(r -> r.path(
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
