package com.imooc.article.service;

import com.imooc.pojo.Article;
import com.imooc.pojo.vo.ArticleDetailVO;
import com.imooc.utils.PagedGridResult;

import java.util.List;

public interface ArticlePortalService {
    public PagedGridResult queryIndexArticleList(String keyword, Integer category, Integer page, Integer pageSize);
    public List<Article> queryHotArticleList();
//    public PagedGridResult queryHotArticleList();


    public PagedGridResult queryUserArticleList(String writerId, Integer page, Integer pageSize);
    public PagedGridResult queryGoodArticleList(String writerId);

    public ArticleDetailVO queryDetail(String articleId);

}
