package com.imooc.user.stream;

import com.imooc.auth.AuthService;
import com.imooc.auth.pojo.Account;
import com.imooc.auth.pojo.AuthCode;
import com.imooc.auth.pojo.AuthReponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;

@EnableBinding({
        ForceLogoutTopic.class
})
@Slf4j
public class UserMessageConsumer {

    @Autowired
    private AuthService authService;

    @StreamListener(ForceLogoutTopic.INPUT)
    public void consumeLogout(String payload) {
        log.info("Force Logout uid={}", payload);
        Account account = Account.builder()
                .userId(payload)
                .skipVerify(true)
                .build();
        AuthReponse reponse = authService.delete(account);
        if (!AuthCode.SUCCESS.equals(reponse.getCode())) {
            log.error("Error occured when deleting user session, uid={}", payload);
            throw new RuntimeException("Can't delete user session");
        }
    }

    // 1 重试 2死信队列 3降级
    @ServiceActivator(inputChannel = "force-logout-topic.force-logout-group.errors")
    public void fallback(Message message) {
        log.error("force logout failed");
        //
    }

}
