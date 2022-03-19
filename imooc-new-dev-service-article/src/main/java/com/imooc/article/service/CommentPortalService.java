package com.imooc.article.service;

import com.imooc.utils.PagedGridResult;

public interface CommentPortalService {
    public void createComment(String articleId, String fatherId, String commentUserId, String content, String nickName, String face);

    public PagedGridResult getAllComments(String articleId, Integer page, Integer pageSize);
}
