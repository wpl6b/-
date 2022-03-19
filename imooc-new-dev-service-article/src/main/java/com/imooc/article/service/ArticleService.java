package com.imooc.article.service;

import com.imooc.pojo.bo.NewArticleBO;
import com.imooc.utils.PagedGridResult;

import java.util.Date;

public interface ArticleService {

    public void createArticle(NewArticleBO newArticleBO);

    public void updateAppointToPublish();

    public void  updateArticleToPublish(String articleId);

    public PagedGridResult queryMyList(String userId, String keyword, Integer status, Date startDate, Date endDate, Integer page, Integer pageSize);

    public PagedGridResult queryAllList(Integer status, Integer page, Integer pageSize);

    //用于审核
    public void updateArticleStatus(String articleId, Integer newStatus);

    public void deleteArticle(String userId, String articleId);

    public void withdrawArticle(String userId, String articleId);

    public void updateArticleToGridFS(String articleId, String articleMongoId);
}
