package com.imooc.utils.extend;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:tencent.properties")
@ConfigurationProperties(prefix = "tencent")
public class TencentResource {
    private String secretID;
    private String secretKey;

    public String getSecretID() {
        return secretID;
    }

    public void setSecretID(String secretID) {
        this.secretID = secretID;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
