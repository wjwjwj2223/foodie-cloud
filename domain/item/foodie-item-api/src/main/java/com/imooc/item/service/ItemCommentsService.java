package com.imooc.item.service;

import com.imooc.pojo.PagedGridResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/*
*  内部的降级 放到item-server 中实现
*  调用方的降级（订单中心调用商品中心的服务） 由订单中心定义降级逻辑
*
* */

@FeignClient("foodie-item-service")
@RequestMapping("item-comments-api")
public interface ItemCommentsService {

    /**
     * 我的评价查询 分页
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("myComments")
    public PagedGridResult queryMyComments(@RequestParam("userId") String userId,
                                           @RequestParam(value = "page", required = false) Integer page,
                                           @RequestParam(value = "pageSize", required = false) Integer pageSize);

    @PostMapping("saveComments")
    public void saveComments(@RequestBody Map<String, Object> map);

}
