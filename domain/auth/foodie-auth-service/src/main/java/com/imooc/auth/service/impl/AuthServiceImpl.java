package com.imooc.auth.service.impl;

import com.imooc.auth.AuthService;
import com.imooc.auth.pojo.Account;
import com.imooc.auth.pojo.AuthCode;
import com.imooc.auth.pojo.AuthReponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@ResponseBody
@Slf4j
public class AuthServiceImpl implements AuthService {

    private static final String USER_TOKEN = "user-token";

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public AuthReponse tokenize(String userId) {
        Account account = Account.builder().userId(userId).build();
        // 验证userName 和password
        String token = jwtService.token(account);
        account.setToken(token);
        account.setRefreshToken(UUID.randomUUID().toString());

        redisTemplate.opsForValue().set(USER_TOKEN + userId, account);
        redisTemplate.opsForValue().set(account.getRefreshToken(), account);

        return AuthReponse.builder()
                .code(AuthCode.SUCCESS)
                .account(account)
                .build();
    }

    @Override
    public AuthReponse verify(Account account) {
        boolean success = jwtService.verify(account.getToken(), account.getUserId());
        return AuthReponse.builder()
                //此处最好用invalid token 之类的错误信息
                .code(success ? AuthCode.SUCCESS : AuthCode.USER_NOT_FOUND)
                .build();
    }

    @Override
    public AuthReponse refresh(String refresh) {
        Account account = (Account) redisTemplate.opsForValue().get(refresh);
        if (account == null) {
            return AuthReponse.builder()
                    .code(AuthCode.USER_NOT_FOUND)
                    .build();
        }
        String token = jwtService.token(account);
        account.setToken(token);
        account.setRefreshToken(UUID.randomUUID().toString());
        redisTemplate.delete(refresh);
        redisTemplate.opsForValue().set(account.getRefreshToken(), account);
        return AuthReponse.builder()
                .code(AuthCode.SUCCESS)
                .account(account)
                .build();
    }

    @Override
    public AuthReponse delete(Account account) {
        AuthReponse verify = verify(account);
        AuthReponse reponse = new AuthReponse();
        if (verify.getCode() == AuthCode.SUCCESS) {
            redisTemplate.delete(account.getRefreshToken());
            redisTemplate.delete(USER_TOKEN + account.getUserId());
            reponse.setCode(AuthCode.SUCCESS);
        } else {
            reponse.setCode(AuthCode.USER_NOT_FOUND);
        }
        return reponse;
    }
}
