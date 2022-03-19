package com.imooc.api.controller.admin;

import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.bo.AdminLoginBO;
import com.imooc.pojo.bo.NewAdminBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@Api(value = "管理员admin维护", tags = {"管理员admin维护的Controller"})
@RequestMapping("adminMng")
public interface AdminMngControllerApi {
    @ApiOperation(value = "管理员登陆", notes = "管理员登陆", httpMethod = "POST")
    @PostMapping("/adminLogin")
    public GraceJSONResult adminLogin(@RequestBody @Valid AdminLoginBO adminLoginBO,BindingResult result, HttpServletRequest request, HttpServletResponse response);

    @ApiOperation(value = "管理员是否存在", notes = "管理员是否存在", httpMethod = "POST")
    @PostMapping("/adminIsExist")
    public GraceJSONResult adminIsExist(String username);

    @ApiOperation(value = "创建admin", notes = "创建admin", httpMethod = "POST")
    @PostMapping("/addNewAdmin")
    public GraceJSONResult addNewAdmin(@RequestBody @Valid NewAdminBO newAdminBO,BindingResult result,
                                       HttpServletRequest request,
                                       HttpServletResponse response);

    @ApiOperation(value = "查询admin列表", notes = "查询admin列表", httpMethod = "POST")
    @PostMapping("/getAdminList")
    public GraceJSONResult getAdminList(@ApiParam(name = "page", value = "查询第几页") @RequestParam Integer page,
                                        @ApiParam(name = "pageSize", value = "每一页显示的条数") @RequestParam Integer pageSize);

    @ApiOperation(value = "管理员退出登录", notes = "管理员退出登录", httpMethod = "POST")
    @PostMapping("/adminLogout")
    public GraceJSONResult adminLogout(@RequestParam String adminId, HttpServletRequest request, HttpServletResponse response);

    @ApiOperation(value = "管理员人脸登录", notes = "管理员人脸登录", httpMethod = "POST")
    @PostMapping("/adminFaceLogin")
    public GraceJSONResult adminFaceLogin(@RequestBody AdminLoginBO adminLoginBO,
                                          HttpServletRequest request,
                                          HttpServletResponse response);
}
