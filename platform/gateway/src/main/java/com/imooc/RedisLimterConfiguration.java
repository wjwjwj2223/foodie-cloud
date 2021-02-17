package com.imooc;


import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

@Configuration
public class RedisLimterConfiguration {

    @Bean
    @Primary
    public KeyResolver remoteAddrKeyResolver() {
        return exchange -> Mono.just(
                exchange.getRequest()
                        .getRemoteAddress()
                        .getAddress()
                        .getHostAddress()
        );
    }

    @Bean("redisLimiterUser")
    @Primary
    public RedisRateLimiter redisLimiterUser() {
        return new RedisRateLimiter(1,2);
    }


    @Bean("redisLimiterItem")
    public RedisRateLimiter redisLimiterItem() {
        return new RedisRateLimiter(20,50);
    }
}
