package com.imooc.admin.controller;

import com.imooc.admin.service.FriendLinkService;
import com.imooc.api.BaseController;
import com.imooc.api.controller.admin.FriendLinkControllerApi;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.bo.SaveFriendLinkBO;
import com.imooc.pojo.mo.FriendLinkMO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
public class FriendLinkController extends BaseController implements FriendLinkControllerApi {

    @Autowired
    private FriendLinkService friendLinkService;
    @Override
    public GraceJSONResult saveOrUpdateFriendLink(@Valid SaveFriendLinkBO saveFriendLinkBO, BindingResult result) {
        //校验参数是否完整
        if(result.hasErrors()){
            Map<String, String> errorMap = getErrors(result);
            return GraceJSONResult.errorMap(errorMap);
        }

        FriendLinkMO mo = new FriendLinkMO();
        BeanUtils.copyProperties(saveFriendLinkBO, mo);
        mo.setCreateTime(new Date());
        mo.setUpdateTime(new Date());
        friendLinkService.saveOrUpdateFriendLink(mo);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult getFriendLinkList() {
        return GraceJSONResult.ok(friendLinkService.queryAllFriendLink());
    }

    @Override
    public GraceJSONResult deleteFriendLink(String linkId) {
        friendLinkService.deleteFriendLink(linkId);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult queryPortalFriendLinkList() {
        List<FriendLinkMO> friendLinkMOList = friendLinkService.queryPortalFriendLinkList();
        return GraceJSONResult.ok(friendLinkMOList);
    }
}
