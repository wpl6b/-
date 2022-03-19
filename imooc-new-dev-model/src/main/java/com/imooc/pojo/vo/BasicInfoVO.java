package com.imooc.pojo.vo;

import lombok.Data;

//对应AppUserVO
@Data
public class BasicInfoVO {
    private String id;
    private String nickname;
    private String face;
    private Integer activeStatus;

    private Integer myFollowCounts;
    private Integer myFansCounts;


}
