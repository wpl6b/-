package com.imooc.api.interceptors;

import com.imooc.exception.GraceException;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseInterceptor {

    public static final String REDIS_USER_TOKEN = "redis_user_token";
    public static final String REDIS_USER_INFO = "redis_user_info";
    public static final String REDIS_ADMIN_TOKEN = "redis_admin_token";
    public static final String REDIS_ADMIN_INFO = "redis_admin_info";
    public static final String REDIS_ALREADY_READ = "redis_already_read";

    @Autowired
    public RedisOperator redis;

    public boolean verifyUserIdToken(String userId, String userToken, String prefix){
        if(StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)){
            String redisUserToken = redis.get(prefix + ":" + userId);
            if(StringUtils.isNotBlank(redisUserToken)){
                if(!redisUserToken.equalsIgnoreCase(userToken)){
                    GraceException.display(ResponseStatusEnum.TICKET_INVALID);
                    return false;
                }

            }else {
                GraceException.display(ResponseStatusEnum.UN_LOGIN);
                return false;
            }
        }else{
            GraceException.display(ResponseStatusEnum.UN_LOGIN);
            return false;
        }
        return true;
    }
}
