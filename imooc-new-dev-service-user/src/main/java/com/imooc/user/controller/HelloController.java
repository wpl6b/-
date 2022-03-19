package com.imooc.user.controller;

import com.imooc.api.controller.user.HelloControllerApi;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.utils.RedisOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController implements HelloControllerApi {
    final static Logger logger = LoggerFactory.getLogger(HelloController.class);

    @Autowired
    private RedisOperator redis;

    public Object hello(){

        logger.debug("1");
        logger.info("2");
        logger.warn("3");
        logger.error("4");
//        return "hello";
        return GraceJSONResult.ok();
    }

    //测试redis
    @GetMapping("/redis")
    public Object redis(){
        redis.set("lancelot", "23");
        return GraceJSONResult.ok(redis.get("lancelot"));
    }
}
