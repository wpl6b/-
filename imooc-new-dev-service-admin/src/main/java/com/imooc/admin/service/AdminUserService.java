package com.imooc.admin.service;


import com.imooc.pojo.AdminUser;
import com.imooc.pojo.bo.NewAdminBO;
import com.imooc.utils.PagedGridResult;

public interface AdminUserService {
    public AdminUser queryAdminByUsername (String username);

    public void createAdminUser(NewAdminBO newAdminBO);

    public PagedGridResult queryAdminList(Integer page, Integer pageSize);
}
