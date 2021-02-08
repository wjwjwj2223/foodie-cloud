package com.imooc.order.fallback.itemservice;

import com.imooc.item.service.ItemCommentsService;
import org.springframework.cloud.openfeign.FeignClient;

/*
* 对于需要在调用端指定降级业务的场景，由于@RequestMapping 和 @xxxMapping 注解 可以从原始
* 接口上继承，因此不能配置两个完全一样的路径 否则启动报错
*
* 在我们的实际案例中  ItemCommentsService 定义了RequestMapping 同时 ItemCommentsFeignClient
* 继承自ItemCommentsService 因此相等于在Spring上下文中加载了两个访问路径一样的方法 会报错"Ambiguous mapping"
*
* 解决方案
*
* 1) 在启动类扫包的时候 不要把原始的feign接口扫描进来
* 具体做法：可以使用EnableFeignClients注解的clients属性  只加载需要的feign接口
* 优点 服务提供者和服务调用者  都不需要做额外的配置
* 缺点 启动的时候配置麻烦一点 要指定加载每一个用到的接口
*
* 2） 原始的feign 接口不要定义requestMapping
* 优点 启动的时候直接扫包
* 缺点1.服务提供者要额外配置路径访问的注解
*     2. 任何情况下  即时不需要在调用端定义fallback类  服务调用者都需要声明一个
*
* 3）原始feign接口 不要定义@FenClients 注解 这样就不会加载到上下文当中
* 优点： 启动的时候直接扫包 不需要指定加载接口 服务提供者不需要额外配置
* 缺点 任何情况下 服务调用者都需要声明一个额外的@FeignClient 接口
* */

//@FeignClient(value = "foodie-item-service", fallback = ItemCommentsFallback.class)
@FeignClient(value = "foodie-item-service", fallbackFactory = ItemCommentsFallbackFactory.class)
public interface ItemCommentsFeignClient extends ItemCommentsService {

}
