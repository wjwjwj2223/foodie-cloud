package com.imooc.order;

import com.imooc.order.fallback.itemservice.ItemCommentsFeignClient;
import com.imooc.user.service.AddressService;
import com.imooc.user.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
// 扫描 mybatis 通用 mapper 所在的包
@MapperScan(basePackages = "com.imooc.order.mapper")
// 扫描所有包以及相关组件包
@ComponentScan(basePackages = {"com.imooc", "org.n3r.idworker"})
@EnableDiscoveryClient
//TODO feign注解
@EnableFeignClients(
        basePackageClasses = {
                ItemCommentsFeignClient.class,
                UserService.class,
                AddressService.class
        }
//        basePackages = {
//            "com.imooc.user.service",
//            "com.imooc.item.service",
//            "com.imooc.order.fallback.itemservice"
//        }
)
@EnableScheduling
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

}
