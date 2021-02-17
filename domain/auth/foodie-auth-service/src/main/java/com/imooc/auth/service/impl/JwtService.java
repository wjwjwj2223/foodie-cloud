package com.imooc.auth.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.imooc.auth.pojo.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class JwtService {

    //生产环境不能这么用
    private static final String KEY = "changedIt";
    private static final String ISSUER = "yao";

    private static final long TOKEN_EXP_TIME = 6000000;
    private static final String USER_ID = "userId";

    // 生产token
    public String token(Account account) {

        //这里提供了很多的加密算法 生产环境可以使用更高等级的加密算法
        //[最常用] 使用非对称密钥加密 auth-service 只负责生产jwt-token
        // 由各个业务方(或者网关层)在自己的代码里使用public key检验 token的正确性
        // 优点符合 规范 节约了一个 http call

        Date now = new Date();
        Algorithm algorithm = Algorithm.HMAC256(KEY);
        String token = JWT.create()
                .withIssuer(ISSUER)
                .withIssuedAt(now)
                .withExpiresAt(new Date(now.getTime() + TOKEN_EXP_TIME))
                .withClaim(USER_ID, account.getUserId())
                .sign(algorithm);
        log.info("jwt generated user={}", account.getUserId());
        return token;
    }

    // 校验token
    public boolean verify(String token, String userId) {
        log.info("jwt verify userId={}", userId);
        try {
            Algorithm algorithm = Algorithm.HMAC256(KEY);
            JWTVerifier jwtVerifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .withClaim(USER_ID, userId)
                    .build();
            jwtVerifier.verify(token);
            return true;
        } catch (Exception e) {
            log.error("auth failed", e);
            return false;
        }
    }

}
