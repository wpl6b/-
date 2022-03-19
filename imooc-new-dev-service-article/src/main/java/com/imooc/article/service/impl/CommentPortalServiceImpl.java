package com.imooc.article.service.impl;

import com.github.pagehelper.PageHelper;
import com.imooc.api.BaseService;
import com.imooc.article.mapper.CommentsMapper;
import com.imooc.article.mapper.CommentsMapperCustom;
import com.imooc.article.service.ArticlePortalService;
import com.imooc.article.service.CommentPortalService;
import com.imooc.pojo.Comments;
import com.imooc.pojo.vo.ArticleDetailVO;
import com.imooc.pojo.vo.CommentsVO;
import com.imooc.utils.PagedGridResult;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class CommentPortalServiceImpl extends BaseService implements CommentPortalService {

    @Autowired
    private Sid sid;

    @Autowired
    private CommentsMapper commentsMapper;

    @Autowired
    private CommentsMapperCustom commentsMapperCustom;

    @Autowired
    private ArticlePortalService articlePortalService;

    @Override
    public void createComment(String articleId, String fatherId, String commentUserId, String content, String nickName, String face) {
        ArticleDetailVO articleDetailVO = articlePortalService.queryDetail(articleId);

        Comments comment = new Comments();

        comment.setId(sid.nextShort());
        comment.setArticleId(articleId);
        comment.setArticleCover(articleDetailVO.getCover());
        comment.setArticleTitle(articleDetailVO.getTitle());
        comment.setCommentUserId(articleDetailVO.getPublishUserId());
        comment.setCommentUserNickname(nickName);
        comment.setContent(content);
        comment.setCreateTime(new Date());
        comment.setFatherId(fatherId);
        comment.setWriterId(articleDetailVO.getPublishUserId());
        comment.setCommentUserFace(face);


        commentsMapper.insert(comment);
        redis.increment(REDIS_ARTICLE_COMMENT_COUNTS + ":" + articleId, 1);
    }

    @Override
    public PagedGridResult getAllComments(String articleId, Integer page, Integer pageSize) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("articleId", articleId);
        PageHelper.startPage(page, pageSize);

        List<CommentsVO> commentsVOList = commentsMapperCustom.queryArticleCommentList(map);
        return setterPagedGrid(commentsVOList, page);
    }
}
