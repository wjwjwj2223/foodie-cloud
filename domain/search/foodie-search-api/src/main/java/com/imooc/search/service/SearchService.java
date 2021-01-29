package com.imooc.search.service;

import com.imooc.pojo.PagedGridResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("search-api")
public interface SearchService {

    /**
     * 搜索商品列表
     * @param keywords
     * @param sort
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("itemsByKeyword")
    public PagedGridResult searhItems(@RequestParam("keywords") String keywords,
                                      @RequestParam(value = "sort", required = false) String sort,
                                      @RequestParam(value = "page", required = false) Integer page,
                                      @RequestParam(value = "pageSize", required = false) Integer pageSize);

    /**
     * 根据分类id搜索商品列表
     * @param catId
     * @param sort
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("itemsById")
    public PagedGridResult searhItems(@RequestParam("catId") Integer catId,
                                      @RequestParam(value = "sort", required = false) String sort,
                                      @RequestParam(value = "page", required = false) Integer page,
                                      @RequestParam(value = "pageSize", required = false) Integer pageSize);

}
