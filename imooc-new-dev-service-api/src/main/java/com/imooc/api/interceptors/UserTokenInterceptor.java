package com.imooc.api.interceptors;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserTokenInterceptor  extends BaseInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //仅h5页面可用 request.getCookies() 找到userId和userToken

        String userId = request.getHeader("headerUserId");
        String userToken = request.getHeader("headerUserToken");
        boolean run = verifyUserIdToken(userId, userToken, REDIS_USER_TOKEN);

//        System.out.println(run);
        return run;
    }
}
