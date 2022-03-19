package com.imooc.admin.service.impl;

import com.imooc.admin.mapper.CategoryMapper;
import com.imooc.admin.service.CategoryService;
import com.imooc.api.BaseService;
import com.imooc.exception.GraceException;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.Category;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class CategoryServiceImpl extends BaseService implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Transactional
    @Override
    public void createCategory(Category category) {
        int result = categoryMapper.insert(category);
        if (result != 1) {
            GraceException.display(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }

        redis.del(REDIS_ALL_CATEGORY);
    }

    @Transactional
    @Override
    public void modifyCategory(Category category) {
        int result = categoryMapper.updateByPrimaryKey(category);
        if (result != 1) {
            GraceException.display(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }

        redis.del(REDIS_ALL_CATEGORY);

    }

    @Override
    public boolean queryCatIsExist(String catName, String oldCatName) {
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("name", catName);
        if(StringUtils.isNotBlank(oldCatName)){
            criteria.andNotEqualTo("name", oldCatName);
        }

        List<Category> categoryList = categoryMapper.selectByExample(example);

        if(categoryList != null  && !categoryList.isEmpty()){
            return true;
        }

        return false;
    }

    @Override
    public List<Category> queryCategoryList() {

        return  categoryMapper.selectAll();
    }
}
