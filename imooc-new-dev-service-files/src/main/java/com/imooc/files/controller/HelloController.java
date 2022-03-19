package com.imooc.files.controller;

import com.imooc.api.controller.user.HelloControllerApi;
import com.imooc.grace.result.GraceJSONResult;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController implements HelloControllerApi {


    public Object hello(){
        return GraceJSONResult.ok("hello files");
    }


}

