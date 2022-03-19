package com.imooc.article.controller;

import com.imooc.api.BaseController;
import com.imooc.api.controller.article.ArticlePortalControllerApi;
import com.imooc.article.service.ArticlePortalService;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.Article;
import com.imooc.pojo.vo.ArticleDetailVO;
import com.imooc.pojo.vo.BasicInfoVO;
import com.imooc.pojo.vo.IndexArticleVO;
import com.imooc.utils.IPUtil;
import com.imooc.utils.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@RestController
public class ArticlePortalController extends BaseController implements ArticlePortalControllerApi {

    @Autowired
    private ArticlePortalService articlePortalService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public GraceJSONResult queryIndexArticleList(String keyword, Integer category, Integer page, Integer pageSize) {
        if (page == null) page = COMMON_START_PAGE;

        if (pageSize == null) pageSize = COMMON_PAGE_SIZE;

        PagedGridResult pagedGridResult = articlePortalService.queryIndexArticleList(keyword, category, page, pageSize);

        rebuildArticleGrid(pagedGridResult);
//        //START ---------显示文章发布者的一些信息  basicInfoVO构成的list
//        List<Article> articleList = (List<Article>)pagedGridResult.getRows();
//        HashSet<String> ids = new HashSet<>();
//        for (Article article:
//             articleList) {
//            ids.add(article.getPublishUserId());
//        }
//
//            //远程调用User服务
//        String url = "http://user.imoocnews.com:8003/user/queryByIds?ids=" + JsonUtils.objectToJson(ids);
//        ResponseEntity<GraceJSONResult> responseEntity = restTemplate.getForEntity(url, GraceJSONResult.class);
//        GraceJSONResult responseEntityBody = responseEntity.getBody();
//
//        List<BasicInfoVO> basicInfoVOList = null;
//        if(responseEntityBody.getStatus() == 200){
//           String userJson = JsonUtils.objectToJson(responseEntityBody.getData());
//           basicInfoVOList = JsonUtils.jsonToList(userJson, BasicInfoVO.class);
//        }
//
//
//        //拼接 articleList和basicInfoVOList重组文章列表
//
//        ArrayList<IndexArticleVO> indexArticleVOArrayList = new ArrayList<>();
//        for (Article article:
//             articleList) {
//            IndexArticleVO indexArticleVO = new IndexArticleVO();
//            BeanUtils.copyProperties(article, indexArticleVO);
//
//            for (BasicInfoVO basicInfoVO:
//                 basicInfoVOList) {
//                if(article.getPublishUserId().equalsIgnoreCase(basicInfoVO.getId())){
////                    indexArticleVO.setBasicInfoVO(basicInfoVO);
//                    indexArticleVO.setPublisherVO(basicInfoVO);
//                }
//            }
//
//            indexArticleVOArrayList.add(indexArticleVO);
//        }
//
//        pagedGridResult.setRows(indexArticleVOArrayList);
//        //END  ----------

        return GraceJSONResult.ok(pagedGridResult);
    }

    @Override
    public GraceJSONResult hotArticleList() {
        List<Article> hotArticleList = articlePortalService.queryHotArticleList();
//        return GraceJSONResult.ok(articlePortalService.queryHotArticleList());
        return GraceJSONResult.ok(hotArticleList);
    }


    @Override
    public GraceJSONResult queryArticleListOfWriter(String writerId, Integer page, Integer pageSize) {
        if (page == null) page = COMMON_START_PAGE;

        if (pageSize == null) pageSize = COMMON_PAGE_SIZE;

        PagedGridResult pagedGridResult = articlePortalService.queryUserArticleList(writerId, page, pageSize);
        rebuildArticleGrid(pagedGridResult);
        return GraceJSONResult.ok(pagedGridResult);
    }

    @Override
    public GraceJSONResult queryGoodArticleListOfWriter(String writerId) {
        PagedGridResult pagedGridResult = articlePortalService.queryGoodArticleList(writerId);
        return GraceJSONResult.ok(pagedGridResult);
    }

    private void rebuildArticleGrid(PagedGridResult pagedGridResult) {
        //START ---------显示文章发布者的一些信息  basicInfoVO构成的list
        List<Article> articleList = (List<Article>) pagedGridResult.getRows();
        HashSet<String> ids = new HashSet<>();
        ArrayList<String> redisReadCountKeys = new ArrayList<>();
        for (Article article :
                articleList) {
            ids.add(article.getPublishUserId());
            redisReadCountKeys.add(REDIS_ARTICLE_READ_COUNTS + ":" + article.getId());
        }

//        //远程调用User服务
//        String url = "http://user.imoocnews.com:8003/user/queryByIds?ids=" + JsonUtils.objectToJson(ids);
//        ResponseEntity<GraceJSONResult> responseEntity = restTemplate.getForEntity(url, GraceJSONResult.class);
//        GraceJSONResult responseEntityBody = responseEntity.getBody();
//
//        List<BasicInfoVO> basicInfoVOList = null;
//        if(responseEntityBody.getStatus() == 200){
//            String userJson = JsonUtils.objectToJson(responseEntityBody.getData());
//            basicInfoVOList = JsonUtils.jsonToList(userJson, BasicInfoVO.class);
//        }
        List<BasicInfoVO> basicInfoVOList = getBasicInfoVOList(ids);

        //拼接 articleList和basicInfoVOList重组文章列表

        ArrayList<IndexArticleVO> indexArticleVOArrayList = new ArrayList<>();
        List<String> redisReadCountsList = redis.mget(redisReadCountKeys);
//        for (Article article:
//                articleList) {
//            IndexArticleVO indexArticleVO = new IndexArticleVO();
//            BeanUtils.copyProperties(article, indexArticleVO);
//
//            for (BasicInfoVO basicInfoVO:
//                    basicInfoVOList) {
//                if(article.getPublishUserId().equalsIgnoreCase(basicInfoVO.getId())){
////                    indexArticleVO.setBasicInfoVO(basicInfoVO);
//                    indexArticleVO.setPublisherVO(basicInfoVO);
//                }
//            }
//
//            indexArticleVOArrayList.add(indexArticleVO);
//        }

        for (int i = 0; i < articleList.size(); i++) {
            Article article = articleList.get(i);
            IndexArticleVO indexArticleVO = new IndexArticleVO();
            BeanUtils.copyProperties(article, indexArticleVO);

            for (BasicInfoVO basicInfoVO :
                    basicInfoVOList) {
                if (article.getPublishUserId().equalsIgnoreCase(basicInfoVO.getId())) {
//                    indexArticleVO.setBasicInfoVO(basicInfoVO);
                    indexArticleVO.setPublisherVO(basicInfoVO);

                }
            }

            String readCountStr = redisReadCountsList.get(i);
            int readCount = 0;
            if (StringUtils.isNotBlank(readCountStr)) {
                readCount = Integer.valueOf(readCountStr);
            }
            indexArticleVO.setReadCounts(Integer.valueOf(readCount));

            indexArticleVOArrayList.add(indexArticleVO);
        }

        pagedGridResult.setRows(indexArticleVOArrayList);
        //END  ----------

    }

    //抽取出的远程调用的方法      后续继续抽取至BaseController
//    private List<BasicInfoVO> getBasicInfoVOList(Set ids) {
//        String url = "http://user.imoocnews.com:8003/user/queryByIds?ids=" + JsonUtils.objectToJson(ids);
//        ResponseEntity<GraceJSONResult> responseEntity = restTemplate.getForEntity(url, GraceJSONResult.class);
//        GraceJSONResult responseEntityBody = responseEntity.getBody();
//
//        List<BasicInfoVO> basicInfoVOList = null;
//        if (responseEntityBody.getStatus() == 200) {
//            String userJson = JsonUtils.objectToJson(responseEntityBody.getData());
//            basicInfoVOList = JsonUtils.jsonToList(userJson, BasicInfoVO.class);
//        }
//
//        return basicInfoVOList;
//    }

    @Override
    public GraceJSONResult detail(String articleId) {
        ArticleDetailVO articleDetailVO = articlePortalService.queryDetail(articleId);
        HashSet<String> set = new HashSet<>();
        set.add(articleDetailVO.getPublishUserId());
        List<BasicInfoVO> basicInfoVOList = getBasicInfoVOList(set);
        if (basicInfoVOList != null && !basicInfoVOList.isEmpty()) {
            articleDetailVO.setPublishUserName(basicInfoVOList.get(0).getNickname());
        }

        articleDetailVO.setReadCounts(getCountsFromRedis(REDIS_ARTICLE_READ_COUNTS + ":" + articleId));
        return GraceJSONResult.ok(articleDetailVO);
    }

    @Override
    public GraceJSONResult readArticle(String articleId, HttpServletRequest request) {
        String requestIp = IPUtil.getRequestIp(request);
        redis.setnx(REDIS_ALREADY_READ + ":" + requestIp + ":" + articleId, requestIp);

        redis.increment(REDIS_ARTICLE_READ_COUNTS + ":" + articleId, 1);
        return GraceJSONResult.ok();
    }

    @Override
    public Integer readCounts(String articleId) {
        return getCountsFromRedis(REDIS_ARTICLE_READ_COUNTS + ":" + articleId);
    }


}
