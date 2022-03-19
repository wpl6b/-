package com.imooc.pojo.bo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RegistLoginBO {

    @NotBlank(message = "手机号不能为空")
    private String mobile;
    @NotBlank(message = "短信验证码不能为空")
    private String smsCode;

}
