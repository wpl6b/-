package com.imooc.api.controller.article;

import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.bo.NewArticleBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Date;

@Api(value = "文章业务的controller", tags = {"文章业务的controller"})
@RequestMapping("article")
public interface ArticleControllerApi {

    @PostMapping("createArticle")
    @ApiOperation(value = "用户发文", notes = "用户发文", httpMethod = "POST")
    public GraceJSONResult createArticle(@RequestBody @Valid NewArticleBO newArticleBO,
                                         BindingResult result);

    @PostMapping("queryMyList")
    @ApiOperation(value = "查询用户的所有文章列表", notes = "查询用户的所有文章列表", httpMethod = "POST")
    public GraceJSONResult queryMyList(@RequestParam String userId,
                                       @RequestParam String keyword,
                                       @RequestParam Integer status,
                                       @RequestParam Date startDate,
                                       @RequestParam Date endDate,
                                       @RequestParam Integer page,
                                       @RequestParam Integer pageSize);

    @PostMapping("queryAllList")
    @ApiOperation(value = "管理员审核文章列表", notes = "管理员审核文章列表", httpMethod = "POST")
    public GraceJSONResult queryAllList(@RequestParam Integer status,
                                        @RequestParam Integer page,
                                        @RequestParam Integer pageSize);

    @PostMapping("doReview")
    @ApiOperation(value = "管理员审核文章列表", notes = "管理员审核文章列表", httpMethod = "POST")
    public GraceJSONResult doReview(@RequestParam String articleId, @RequestParam Integer passOrNot);

    @PostMapping("/delete")
    @ApiOperation(value = "用户删除文章", notes = "用户删除文章", httpMethod = "POST")
    public GraceJSONResult delete(@RequestParam String userId,
                                  @RequestParam String articleId);

    @PostMapping("/withdraw")
    @ApiOperation(value = "用户撤回文章", notes = "用户撤回文章", httpMethod = "POST")
    public GraceJSONResult withdraw(@RequestParam String userId,
                                    @RequestParam String articleId);
}
