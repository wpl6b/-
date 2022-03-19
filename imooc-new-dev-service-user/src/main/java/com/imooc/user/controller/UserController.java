package com.imooc.user.controller;

import com.imooc.api.BaseController;
import com.imooc.api.controller.user.UserControllerApi;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.AppUser;
import com.imooc.pojo.bo.UpdateUserInfoBO;
import com.imooc.pojo.vo.AccountInfoVO;
import com.imooc.pojo.vo.BasicInfoVO;
import com.imooc.user.service.UserService;
import com.imooc.utils.JsonUtils;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@ToString
@RestController
public class UserController extends BaseController implements UserControllerApi {
    @Autowired
    private UserService userService;



    private AppUser getUser(String userId){

        AppUser user = null;
        if(redis.keyIsExist(REDIS_USER_INFO + ":" + userId)){
            user = JsonUtils.jsonToPojo(redis.get(REDIS_USER_INFO + ":" + userId), AppUser.class);
        }else {
            user = userService.getUser(userId);
            redis.set(REDIS_USER_INFO + ":" + userId, JsonUtils.objectToJson(user));
        }
        return user;
    }

    @Override
    public GraceJSONResult getBasicInfo(String userId) {
        if(StringUtils.isBlank(userId)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.UN_LOGIN);
        }

        //构建VO
        BasicInfoVO basicInfoVO = constructBasicInfoVO(userId);

        //关注数、粉丝数
        basicInfoVO.setMyFansCounts(getCountsFromRedis(REDIS_WRITER_FANS_COUNTS+ ":" + userId));
        basicInfoVO.setMyFollowCounts(getCountsFromRedis(REDIS_MY_FOLLOW_COUNTS + ":" + userId));

        return GraceJSONResult.ok(basicInfoVO);
    }

    @Override
    public GraceJSONResult getAccountInfo(String userId) {
        if(StringUtils.isBlank(userId)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.UN_LOGIN);
        }

        AppUser user = getUser(userId);
        AccountInfoVO accountInfoVO = new AccountInfoVO();
        BeanUtils.copyProperties(user, accountInfoVO);
        return GraceJSONResult.ok(accountInfoVO);
    }

    @Override
    public GraceJSONResult updateUserInfo(@Valid UpdateUserInfoBO updateUserInfoBO, BindingResult result) {
        if(result.hasErrors()){
            Map<String, String> errorMap = getErrors(result);
            return GraceJSONResult.errorMap(errorMap);
        }


        userService.updateUserInfo(updateUserInfoBO);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult queryByIds(String ids) {

        if(StringUtils.isBlank(ids))    return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_EXIST_ERROR);

        ArrayList<BasicInfoVO> basicInfoVOArrayList = new ArrayList<>();
        List<String> userIdList = (List<String>) JsonUtils.jsonToList(ids, String.class);

        for (String id :
                userIdList) {
            BasicInfoVO basicInfoVO = constructBasicInfoVO(id);

            basicInfoVOArrayList.add(basicInfoVO);
        }

        return GraceJSONResult.ok(basicInfoVOArrayList);
    }

    private BasicInfoVO constructBasicInfoVO(String userId){
        AppUser user = getUser(userId);
        BasicInfoVO basicInfoVO = new BasicInfoVO();
        BeanUtils.copyProperties(user, basicInfoVO);
        return basicInfoVO;
    }
}
