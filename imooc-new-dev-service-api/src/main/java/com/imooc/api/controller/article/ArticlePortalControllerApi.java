package com.imooc.api.controller.article;

import com.imooc.grace.result.GraceJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Api(value = "index文章业务的controller", tags = {"index文章业务的controller"})
@RequestMapping("portal/article")
public interface ArticlePortalControllerApi {

    @GetMapping("list")
    @ApiOperation(value = "index文章列表", notes = "index文章列表", httpMethod = "GET")
    public GraceJSONResult queryIndexArticleList(@RequestParam String keyword,
                                        @RequestParam Integer category,
                                        @RequestParam Integer page,
                                        @RequestParam Integer pageSize);

    @GetMapping("hotList")
    @ApiOperation(value = "热门文章列表", notes = "热门文章列表", httpMethod = "GET")
    public GraceJSONResult hotArticleList();


    //同上面的接口解耦
    @GetMapping("queryArticleListOfWriter")
    @ApiOperation(value = "writer文章列表", notes = "writer文章列表", httpMethod = "GET")
    public GraceJSONResult queryArticleListOfWriter(@RequestParam String writerId,
                                                 @RequestParam Integer page,
                                                 @RequestParam Integer pageSize);

    @GetMapping("queryGoodArticleListOfWriter")
    @ApiOperation(value = "writer近期佳文", notes = "writer近期佳文", httpMethod = "GET")
    public GraceJSONResult queryGoodArticleListOfWriter(@RequestParam String writerId);

    @GetMapping("detail")
    @ApiOperation(value = "文章详情查询", notes = "文章详情查询", httpMethod = "GET")
    public GraceJSONResult detail(@RequestParam String articleId);

    @PostMapping("readArticle")
    @ApiOperation(value = "文章阅读量", notes = "文章阅读量", httpMethod = "POST")
    public GraceJSONResult readArticle(@RequestParam String articleId, HttpServletRequest request);

    @GetMapping("readCounts")
    @ApiOperation(value = "静态化页面获取文章阅读量", notes = "静态化页面获取文章阅读量", httpMethod = "GET")
    public Integer readCounts(@RequestParam String articleId);
}
