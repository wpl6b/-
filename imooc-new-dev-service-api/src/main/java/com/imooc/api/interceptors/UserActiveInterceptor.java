package com.imooc.api.interceptors;

import com.imooc.enums.UserStatus;
import com.imooc.exception.GraceException;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.AppUser;
import com.imooc.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserActiveInterceptor extends BaseInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("headerUserId");
        String redisUserInfo = redis.get(REDIS_USER_INFO + ":" + userId);


        AppUser user = null;
        if(StringUtils.isNotBlank(redisUserInfo)){
            user = JsonUtils.jsonToPojo(redisUserInfo, AppUser.class);
        }else  {
            GraceException.display(ResponseStatusEnum.UN_LOGIN);
            return false;
        }

        Integer activeStatus = user.getActiveStatus();
        if(activeStatus != null && activeStatus != UserStatus.ACTIVE.type){
            GraceException.display(ResponseStatusEnum.USER_INACTIVE_ERROR);
            return false;
        }
        return true;
    }
}
