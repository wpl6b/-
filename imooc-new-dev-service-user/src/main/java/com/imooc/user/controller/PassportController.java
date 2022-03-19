package com.imooc.user.controller;

import com.imooc.api.BaseController;
import com.imooc.api.controller.user.PassportControllerApi;
import com.imooc.enums.UserStatus;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.AppUser;
import com.imooc.pojo.bo.RegistLoginBO;
import com.imooc.user.service.UserService;
import com.imooc.utils.IPUtil;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.SMSUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;

@RestController
public class PassportController extends BaseController implements PassportControllerApi {
    final static Logger logger = LoggerFactory.getLogger(PassportController.class);

    @Autowired
    private SMSUtils smsUtils;

    @Autowired
    private UserService userService;


    @Override
    public GraceJSONResult doLogin(@Valid RegistLoginBO registLoginBO, BindingResult result, HttpServletRequest request, HttpServletResponse response) {

        //校验参数是否完整
        if(result.hasErrors()){
//            GraceException.display(ResponseStatusEnum.SMS_CODE_ERROR);
            Map<String, String> errorMap = getErrors(result);
            return GraceJSONResult.errorMap(errorMap);
        }

        //比对验证码
        String mobile = registLoginBO.getMobile();
        String smsCode = registLoginBO.getSmsCode();
        String redisSMSCode = redis.get(MOBILE_SMSCODE + ":" + mobile);
        if(StringUtils.isBlank(redisSMSCode) || !redisSMSCode.equals(smsCode)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }

        //查看用户是否存在、状态码
        AppUser user = userService.queryMobileIsExist(mobile);
        if(user != null && user.getActiveStatus() == UserStatus.FROZEN.type){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_FROZEN);
        }else if(user == null)  user = userService.createUser(mobile);

        //创建cookie
        Integer userActiveStatus = user.getActiveStatus();
        if(userActiveStatus != UserStatus.FROZEN.type){

            //redis中存入token
            String uToken = UUID.randomUUID().toString().trim();
            redis.set(REDIS_USER_TOKEN + ":" + user.getId(), uToken);
            redis.set(REDIS_USER_INFO + ":" + user.getId(), JsonUtils.objectToJson(user));

            //客户端存入cookies
            setCookieValue(request, response, "utoken", uToken, COOKIE_MONTH);
            setCookieValue(request, response, "uid", user.getId(), COOKIE_MONTH);
        }

        redis.del(MOBILE_SMSCODE + ":" + mobile);
        return GraceJSONResult.ok(userActiveStatus);
    }

    @Override
    public GraceJSONResult getSMSCode(String mobile, HttpServletRequest request) {
        String userIp = IPUtil.getRequestIp(request);

        redis.set(MOBILE_SMSCODE + ":" + userIp, userIp, 60);
//        redis.setnx60s(MOBILE_SMSCODE + ":" + userIp, userIp);

        String random = (int)(Math.random() * 10000) + "";

//        smsUtils.sendSMS(mobile, random);
        redis.set(MOBILE_SMSCODE + ":" + mobile, random, 5*60);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult logout(String userId, HttpServletRequest request, HttpServletResponse response) {
        redis.del(REDIS_USER_TOKEN + ":" + userId);
        setCookie(request, response,"utoken", "", COOKIE_DELETE);
        setCookie(request, response,"uid", "", COOKIE_DELETE);

        return GraceJSONResult.ok();


    }
}
