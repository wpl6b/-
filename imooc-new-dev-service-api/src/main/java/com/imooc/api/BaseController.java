package com.imooc.api;

import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.vo.BasicInfoVO;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class BaseController {

    @Autowired
    public RedisOperator redis;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${website.domain-name}")
    public String DOMAIN_NAME;

    public static final String MOBILE_SMSCODE ="mobile_smscode";
    public static final String REDIS_USER_TOKEN = "redis_user_token";
    public static final Integer COOKIE_MONTH = 30*24*60*60;
    public static final Integer COOKIE_DELETE = 0;
    public static final String REDIS_USER_INFO = "redis_user_info";
    public static final String REDIS_ADMIN_TOKEN = "redis_admin_token";
    public static final Integer COMMON_START_PAGE = 1;
    public static final Integer COMMON_PAGE_SIZE = 1;

    public static final String REDIS_ALL_CATEGORY = "redis_all_category";

    public static final String REDIS_WRITER_FANS_COUNTS = "redis_writer_fans_counts";
    public static final String REDIS_MY_FOLLOW_COUNTS = "redis_my_follow_counts";

    public static final String REDIS_ARTICLE_READ_COUNTS = "redis_article_read_counts";
    public static final String REDIS_ALREADY_READ = "redis_already_read";

    public static final String REDIS_ARTICLE_COMMENT_COUNTS = "redis_article_comment_counts";
    public  Map<String, String> getErrors(BindingResult result){
        HashMap<String, String> map = new HashMap<>();
        List<FieldError> fieldErrors = result.getFieldErrors();
        for (FieldError fieldError: fieldErrors) {
            String field = fieldError.getField();
            String msg = fieldError.getDefaultMessage();
            map.put(field, msg);
        }
        return map;
    }

    public void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, Integer maxAge){
        try {
            String encode = URLEncoder.encode(cookieValue, "UTF-8");
            setCookieValue(request,response,cookieName,encode, maxAge);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public void setCookieValue(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, Integer maxAge){
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setMaxAge(maxAge);
        cookie.setDomain(DOMAIN_NAME);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public void deleteCookie(HttpServletRequest request, HttpServletResponse response, String cookieName){
        String deleteValue = null;
        try {
            deleteValue = URLEncoder.encode("", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        setCookieValue(request,response,cookieName,deleteValue, COOKIE_DELETE);
    }

    public Integer getCountsFromRedis(String key){
        String countStr = redis.get(key);
        if(StringUtils.isBlank(countStr)){
            return 0;
        }
        return Integer.valueOf(countStr);
    }

    public List<BasicInfoVO> getBasicInfoVOList(Set ids) {
        String url = "http://user.imoocnews.com:8003/user/queryByIds?ids=" + JsonUtils.objectToJson(ids);
        ResponseEntity<GraceJSONResult> responseEntity = restTemplate.getForEntity(url, GraceJSONResult.class);
        GraceJSONResult responseEntityBody = responseEntity.getBody();

        List<BasicInfoVO> basicInfoVOList = null;
        if (responseEntityBody.getStatus() == 200) {
            String userJson = JsonUtils.objectToJson(responseEntityBody.getData());
            basicInfoVOList = JsonUtils.jsonToList(userJson, BasicInfoVO.class);
        }

        return basicInfoVOList;
    }
}
