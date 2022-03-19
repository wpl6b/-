package com.imooc.api.interceptors;

import com.imooc.utils.IPUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ArticleReadInterceptor extends BaseInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestIp = IPUtil.getRequestIp(request);
        boolean isExist = redis.keyIsExist(REDIS_ALREADY_READ + ":" + requestIp + ":" + request.getParameter("articleId"));
        if(isExist){
            return false;
        }
        return true;
    }
}
