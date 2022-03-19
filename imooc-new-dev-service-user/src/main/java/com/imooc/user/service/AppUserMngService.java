package com.imooc.user.service;

import com.imooc.utils.PagedGridResult;

import java.util.Date;

public interface AppUserMngService {
    public PagedGridResult queryAll(String nickname, Integer status, Date startDate, Date endDate, Integer page, Integer pageSize);

    public void freezeUserOrNot(String userId, Integer doStatus);


}
