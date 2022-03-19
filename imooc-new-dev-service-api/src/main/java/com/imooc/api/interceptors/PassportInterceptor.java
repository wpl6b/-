package com.imooc.api.interceptors;

import com.imooc.exception.GraceException;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.utils.IPUtil;
import com.imooc.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PassportInterceptor implements HandlerInterceptor {
    @Autowired
    public RedisOperator redis;
    public static final String MOBILE_SMSCODE = "mobile_smscode";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userIP = IPUtil.getRequestIp(request);
//        String userIP = "127.0.0.1";
        boolean keyIsExist = redis.keyIsExist(MOBILE_SMSCODE + ":" + userIP);
//        long ttl = redis.ttl(MOBILE_SMSCODE + ":" + userIP);
//        System.out.println("ttl: " + ttl);
        if (keyIsExist) {
            GraceException.display(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
//            System.out.println("短信发送频率过快!");
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
