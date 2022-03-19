package com.imooc.article.controller;

import com.imooc.api.BaseController;
import com.imooc.api.controller.article.CommentControllerApi;
import com.imooc.article.service.CommentPortalService;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.bo.CommentReplyBO;
import com.imooc.utils.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Map;

@RestController
public class CommentController extends BaseController implements CommentControllerApi {

    @Autowired
    private CommentPortalService commentPortalService;



    @Override
    public GraceJSONResult createComment(@Valid CommentReplyBO commentReplyBO, BindingResult result) {
        //校验参数是否完整
        if (result.hasErrors()) {
//            GraceException.display(ResponseStatusEnum.SMS_CODE_ERROR);
            Map<String, String> errorMap = getErrors(result);
            return GraceJSONResult.errorMap(errorMap);
        }

        HashSet<String> ids = new HashSet<>();
        ids.add(commentReplyBO.getCommentUserId());
        String userNickName = getBasicInfoVOList(ids).get(0).getNickname();  //评论发布者的昵称
        String userFace = getBasicInfoVOList(ids).get(0).getFace();  //评论发布者的头像
        commentPortalService.createComment(commentReplyBO.getArticleId(),
                commentReplyBO.getFatherId(),
                commentReplyBO.getCommentUserId(),
                commentReplyBO.getContent(),
                userNickName,
                userFace);

        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult commentCounts(String articleId) {

        return GraceJSONResult.ok(getCountsFromRedis(REDIS_ARTICLE_COMMENT_COUNTS + ":" + articleId));
    }

    @Override
    public GraceJSONResult getAllComments(String articleId, Integer page, Integer pageSize) {
        if(page == null)  page = COMMON_START_PAGE;

        if(pageSize == null)    pageSize = COMMON_PAGE_SIZE;
        PagedGridResult pagedGridResult = commentPortalService.getAllComments(articleId, page, pageSize);
        return GraceJSONResult.ok(pagedGridResult);
    }
}
