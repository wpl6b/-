package com.imooc.api;

import com.github.pagehelper.PageInfo;
import com.imooc.utils.PagedGridResult;
import com.imooc.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class BaseService {

    @Autowired
    public RedisOperator redis;

    public static final String REDIS_ALL_CATEGORY = "redis_all_category";
    public static final String REDIS_WRITER_FANS_COUNTS = "redis_writer_fans_counts";
    public static final String REDIS_MY_FOLLOW_COUNTS = "redis_my_follow_counts";
    public static final String REDIS_ARTICLE_COMMENT_COUNTS = "redis_article_comment_counts";

    public PagedGridResult setterPagedGrid(List<?> list, Integer page){
        PageInfo<?> pageInfo = new PageInfo<>(list);
        PagedGridResult pagedGridResult = new PagedGridResult();
        pagedGridResult.setPage(page);
        pagedGridResult.setRows(list);
        pagedGridResult.setRecords(pageInfo.getTotal());
        pagedGridResult.setTotal(pageInfo.getPages());
        return  pagedGridResult;
    }
}
