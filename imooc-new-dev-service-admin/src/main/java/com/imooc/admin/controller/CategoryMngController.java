package com.imooc.admin.controller;

import com.imooc.admin.service.CategoryService;
import com.imooc.api.BaseController;
import com.imooc.api.controller.admin.CategoryMngControllerApi;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.Category;
import com.imooc.pojo.bo.SaveCategoryBO;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class CategoryMngController extends BaseController implements CategoryMngControllerApi {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisOperator redis;

    @Override
    public GraceJSONResult saveOrUpdateCategory(@Valid SaveCategoryBO saveCategoryBO, BindingResult result){

//    {
//        //校验参数是否完整
//        if (result.hasErrors()) {
//            Map<String, String> errorMap = getErrors(result);
//            return GraceJSONResult.errorMap(errorMap);
//        }

        Category category = new Category();
        BeanUtils.copyProperties(saveCategoryBO, category);


        if (category.getId() == null) {
            if (!categoryService.queryCatIsExist(category.getName(), null)) {

                categoryService.createCategory(category);

            } else {

                return GraceJSONResult.errorCustom(ResponseStatusEnum.CATEGORY_EXIST_ERROR);

            }
            categoryService.createCategory(category);

        } else {
            if (!categoryService.queryCatIsExist(category.getName(), saveCategoryBO.getOldName())) {

                categoryService.modifyCategory(category);

            } else {

                return GraceJSONResult.errorCustom(ResponseStatusEnum.CATEGORY_EXIST_ERROR);

            }

        }

        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult getCatList() {
        return GraceJSONResult.ok(categoryService.queryCategoryList());
    }

    @Override
    public GraceJSONResult getCats() {
        List<Category> catList = null;

        String allCategory = redis.get(REDIS_ALL_CATEGORY);
        if(StringUtils.isBlank(allCategory)){
           catList = categoryService.queryCategoryList();
           redis.set(REDIS_ALL_CATEGORY, JsonUtils.objectToJson(catList));
        }else {
            catList = JsonUtils.jsonToList(allCategory, Category.class);
        }

        return GraceJSONResult.ok(catList);
    }
}
