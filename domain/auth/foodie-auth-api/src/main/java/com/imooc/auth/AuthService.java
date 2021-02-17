package com.imooc.auth;

import com.imooc.auth.pojo.Account;
import com.imooc.auth.pojo.AuthReponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("foodie-auth-service")
@RequestMapping("auth-service")
public interface AuthService {

    @PostMapping("token")
    public AuthReponse tokenize(@RequestParam("userId") String userId);

    @PostMapping("verify")
    public AuthReponse verify(@RequestBody Account account);

    @PostMapping("refresh")
    public AuthReponse refresh(@RequestParam("refresh") String refresh);

    @DeleteMapping("delete")
    public AuthReponse delete(@RequestBody Account account);

}
