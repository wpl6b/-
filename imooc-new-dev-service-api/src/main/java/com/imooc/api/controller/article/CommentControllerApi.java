package com.imooc.api.controller.article;

import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.bo.CommentReplyBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "评论相关业务的controller", tags = {"评论相关业务的controller"})
@RequestMapping("comment")
public interface CommentControllerApi {

    @PostMapping("createComment")
    @ApiOperation(value = "用户评论", notes = "用户评论", httpMethod = "POST")
    public GraceJSONResult createComment(@RequestBody @Valid CommentReplyBO commentReplyBO, BindingResult result);


    @GetMapping("counts")
    @ApiOperation(value = "用户评论数查询", notes = "用户评论数查询", httpMethod = "GET")
    public GraceJSONResult commentCounts(@RequestParam String articleId);

    @GetMapping("list")
    @ApiOperation(value = "获得文章的评论列表", notes = "获得文章的评论列表", httpMethod = "GET")
    public GraceJSONResult getAllComments(@RequestParam String articleId, @RequestParam Integer page, @RequestParam Integer pageSize);
}
