package com.imooc.api.controller.user;

import com.imooc.grace.result.GraceJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Api(value = "用户管理维护", tags = {"用户管理维护的Controller"})
@RequestMapping("appUser")
public interface AppUserMngControllerApi {

    @ApiOperation(value = "获取用户列表", notes = "获取用户列表", httpMethod = "POST")
    @PostMapping("/queryAll")
    public GraceJSONResult getUserList(@RequestParam String nickname, @RequestParam Integer status, @RequestParam Date startDate, @RequestParam Date endDate, @RequestParam Integer page, @RequestParam Integer pageSize);

    @ApiOperation(value = "查看用户信息", notes = "查看用户信息", httpMethod = "POST")
    @PostMapping("/userDetail")
    public GraceJSONResult userDetail(@RequestParam String userId);

    @ApiOperation(value = "修改用户状态", notes = "修改用户状态", httpMethod = "POST")
    @PostMapping("/freezeUserOrNot")
    public GraceJSONResult freezeUserOrNot(@RequestParam String userId, @RequestParam Integer doStatus);
}
