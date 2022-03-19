package com.imooc.article.service.impl;

import com.github.pagehelper.PageHelper;
import com.imooc.api.BaseService;
import com.imooc.article.mapper.ArticleMapper;
import com.imooc.article.service.ArticlePortalService;
import com.imooc.enums.ArticleReviewStatus;
import com.imooc.enums.YesOrNo;
import com.imooc.pojo.Article;
import com.imooc.pojo.vo.ArticleDetailVO;
import com.imooc.utils.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class ArticlePortalServiceImpl extends BaseService implements ArticlePortalService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public PagedGridResult queryIndexArticleList(String keyword, Integer category, Integer page, Integer pageSize) {
        Example articleExample = new Example(Article.class);
        Example.Criteria criteria = setDefaultExampleCriteria(articleExample);

        if(StringUtils.isNotBlank(keyword)) criteria.andLike("title", "%" + keyword + "%");
        if(category != null)    criteria.andEqualTo("categoryId", category);

        PageHelper.startPage(page, pageSize);
        List<Article> articleList = articleMapper.selectByExample(articleExample);
        return setterPagedGrid(articleList, page);
    }

    @Override
    public List<Article> queryHotArticleList() {
        Example articleExample = new Example(Article.class);
        Example.Criteria criteria = setDefaultExampleCriteria(articleExample);

        PageHelper.startPage(1, 5);
        List<Article> hotArticleList = articleMapper.selectByExample(articleExample);

        return hotArticleList;
    }

//    public PagedGridResult queryHotArticleList() {
//        Example articleExample = new Example(Article.class);
//        Example.Criteria criteria = setDefaultExampleCriteria(articleExample);
//
//        PageHelper.startPage(1, 5);
//        List<Article> hotArticleList = articleMapper.selectByExample(articleExample);
//
//        return setterPagedGrid(hotArticleList, 1);
//    }

    private Example.Criteria setDefaultExampleCriteria(Example articleExample){
        articleExample.orderBy("publishTime").desc();
        Example.Criteria criteria = articleExample.createCriteria();
        //显示的文章必须是即时发布、未删除、已审核
        criteria.andEqualTo("isAppoint", YesOrNo.NO);
        criteria.andEqualTo("isDelete", YesOrNo.NO);
        criteria.andEqualTo("articleStatus", ArticleReviewStatus.SUCCESS.type);

        return criteria;
    }


    @Override
    public PagedGridResult queryUserArticleList(String writerId, Integer page, Integer pageSize) {
        Example example = new Example(Article.class);
        Example.Criteria criteria = setDefaultExampleCriteria(example);
        criteria.andEqualTo("publishUserId", writerId);

        PageHelper.startPage(page, pageSize);
        List<Article> userArticleList = articleMapper.selectByExample(example);

        return setterPagedGrid(userArticleList, page);
    }

    @Override
    public PagedGridResult queryGoodArticleList(String writerId) {
        Example example = new Example(Article.class);
        Example.Criteria criteria = setDefaultExampleCriteria(example);
        criteria.andEqualTo("publishUserId", writerId);
        PageHelper.startPage(1, 5);
        List<Article> goodArticleList = articleMapper.selectByExample(example);

        return setterPagedGrid(goodArticleList, 1);
    }

    @Override
    public ArticleDetailVO queryDetail(String articleId) {
        Article article = new Article();

        article.setId(articleId);
        article.setIsDelete(YesOrNo.NO.type);
        article.setArticleStatus(ArticleReviewStatus.SUCCESS.type);
        article.setIsAppoint(YesOrNo.NO.type);

        Article selectOne = articleMapper.selectOne(article);

        //因为Article中有关发布者的属性只有publishUserId 所以重新封装一个对象
        ArticleDetailVO articleDetailVO = new ArticleDetailVO();
        BeanUtils.copyProperties(selectOne, articleDetailVO);

        articleDetailVO.setCover(article.getArticleCover());

        return articleDetailVO;
    }
}
