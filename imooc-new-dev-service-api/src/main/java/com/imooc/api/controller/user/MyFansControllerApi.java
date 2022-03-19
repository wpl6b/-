package com.imooc.api.controller.user;

import com.imooc.grace.result.GraceJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Api(value = "粉丝相关Controller", tags = {"粉丝相关Controller"})
@RequestMapping("fans")
public interface MyFansControllerApi {
    @ApiOperation(value = "查询当前用户是否关注作家", notes = "查询当前用户是否关注作家", httpMethod = "POST")
    @PostMapping("isMeFollowThisWriter")
    public GraceJSONResult isMeFollowThisWriter(@RequestParam String writerId, @RequestParam String fanId);

    @ApiOperation(value = "用户关注作家,成为粉丝", notes = "用户关注作家,成为粉丝", httpMethod = "POST")
    @PostMapping("follow")
    public GraceJSONResult follow(@RequestParam String writerId, @RequestParam String fanId);

    @ApiOperation(value = "取消关注,作家减少粉丝", notes = "取消关注,作家减少粉丝", httpMethod = "POST")
    @PostMapping("unfollow")
    public GraceJSONResult unfollow(@RequestParam String writerId, @RequestParam String fanId);

    @ApiOperation(value = "查询粉丝列表", notes = "查询粉丝列表", httpMethod = "POST")
    @PostMapping("queryAll")
    public GraceJSONResult queryAll(@RequestParam String writerId, @RequestParam Integer page, @RequestParam Integer pageSize);

    @ApiOperation(value = "查看粉丝男女比例", notes = "查看粉丝男女比例", httpMethod = "POST")
    @PostMapping("queryRatio")
    public GraceJSONResult queryRatio(@RequestParam String writerId);

    @ApiOperation(value = "查看粉丝地域分布", notes = "查看粉丝地域分布", httpMethod = "POST")
    @PostMapping("queryRatioByRegion")
    public GraceJSONResult queryRatioByRegion(@RequestParam String writerId);
}
