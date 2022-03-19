package com.imooc.user.controller;

import com.imooc.api.BaseController;
import com.imooc.api.controller.user.AppUserMngControllerApi;
import com.imooc.enums.UserStatus;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.AppUser;
import com.imooc.user.service.AppUserMngService;
import com.imooc.user.service.UserService;
import com.imooc.utils.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;


@RestController
public class AppUserMngController extends BaseController implements AppUserMngControllerApi {

    @Autowired
    private AppUserMngService appUserMngService;

    @Autowired
    private UserService userService;



    @Override
    public GraceJSONResult getUserList(String nickname, Integer status, Date startDate, Date endDate, Integer page, Integer pageSize) {
        if (page == null) page = COMMON_START_PAGE;
        if (pageSize == null) pageSize = COMMON_PAGE_SIZE;

        PagedGridResult pagedGridResult = appUserMngService.queryAll(nickname, status, startDate, endDate, page, pageSize);
        return GraceJSONResult.ok(pagedGridResult);
    }

    @Override
    public GraceJSONResult userDetail(String userId) {

        AppUser user = userService.getUser(userId);
        return GraceJSONResult.ok(user);
    }

    @Override
    public GraceJSONResult freezeUserOrNot(String userId, Integer doStatus) {
        if (!UserStatus.isUserStatusValid(doStatus))
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_STATUS_ERROR);


        appUserMngService.freezeUserOrNot(userId, doStatus);
        return GraceJSONResult.ok();
    }
}
