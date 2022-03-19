package com.imooc.api.interceptors;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminTokenInterceptor extends BaseInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String adminUserId = request.getHeader("adminUserId");
        String adminUserToken = request.getHeader("adminUserToken");
        boolean run = verifyUserIdToken(adminUserId, adminUserToken, REDIS_ADMIN_TOKEN);

        System.out.println("=====================================================================");
        System.out.println("AdminTokenInterceptor - adminUserId = " + adminUserId);
        System.out.println("AdminTokenInterceptor - adminUserToken = " + adminUserToken);
        System.out.println("=====================================================================");

        return run;
    }
}
