package com.imooc.filter;

import com.imooc.auth.AuthService;
import com.imooc.auth.pojo.Account;
import com.imooc.auth.pojo.AuthCode;
import com.imooc.auth.pojo.AuthReponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component("authFilter")
@Slf4j
public class AuthFilter implements GatewayFilter, Ordered {

    private static final String AUTH = "Authorization";
    private static final String USER_ID = "imooc-user-id";

    @Autowired
    private AuthService authService;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {
        log.info("auth start");
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        String token = headers.getFirst(AUTH);
        String userId = headers.getFirst(USER_ID);

        ServerHttpResponse response = exchange.getResponse();
        if (StringUtils.isBlank(token)) {
            log.error("token not found");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        Account account = Account.builder()
                .userId(userId)
                .token(token)
                .build();
        AuthReponse verify = authService.verify(account);
        if (verify.getCode() != AuthCode.SUCCESS) {
            log.error("invalid token");
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }
        // TODO 将用户信息放在header 中 传递给下游业务
        ServerHttpRequest.Builder mutate = request.mutate();
        mutate.header(USER_ID, userId);
        ServerHttpRequest build = mutate.build();
        // TODO  如果响应中也需要放数据 也可以把数据放到响应中
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().add(USER_ID, userId);
        return chain.filter(exchange.mutate()
                            .request(build)
                            .response(response)
                            .build());
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
