package com.imooc.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.imooc.admin.mapper.AdminUserMapper;
import com.imooc.admin.service.AdminUserService;
import com.imooc.api.BaseService;
import com.imooc.exception.GraceException;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.AdminUser;
import com.imooc.pojo.bo.NewAdminBO;
import com.imooc.utils.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class AdminUserServiceImpl extends BaseService implements AdminUserService {

    @Autowired
    AdminUserMapper adminUserMapper;

    @Autowired
    Sid sid;
    @Override
    public AdminUser queryAdminByUsername(String username) {
        Example example = new Example(AdminUser.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username", username);
        AdminUser adminUser = adminUserMapper.selectOneByExample(example);
        return adminUser;
    }

    @Transactional
    @Override
    public void createAdminUser(NewAdminBO newAdminBO) {
        AdminUser adminUser = new AdminUser();
        //密码未加密 不能copy
        //BeanUtils.copyProperties(newAdminBO, adminUser);


        adminUser.setId(sid.nextShort());
        adminUser.setAdminName(newAdminBO.getAdminName());
        adminUser.setUsername(newAdminBO.getUsername());
        adminUser.setPassword(BCrypt.hashpw(newAdminBO.getPassword(), BCrypt.gensalt()));

        if(StringUtils.isNotBlank(newAdminBO.getFaceId())){
            adminUser.setFaceId(newAdminBO.getFaceId());
        }
        adminUser.setCreatedTime(new Date());
        adminUser.setUpdatedTime(new Date());

        int result = adminUserMapper.insert(adminUser);
        if(result != 1){
            GraceException.display(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
        }
    }

    @Override
    public PagedGridResult queryAdminList(Integer page, Integer pageSize) {
        Example example = new Example(AdminUser.class);
        example.orderBy("createdTime").desc();
        PageHelper.startPage(page, pageSize);
        List<AdminUser> adminUserList = adminUserMapper.selectByExample(example);
        PagedGridResult pagedGridResult = setterPagedGrid(adminUserList, page);
        return pagedGridResult;
    }


}
