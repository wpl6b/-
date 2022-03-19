package com.imooc.article.controller;

import com.imooc.api.BaseController;
import com.imooc.api.config.RabbitMQConfig;
import com.imooc.api.controller.article.ArticleControllerApi;
import com.imooc.article.service.ArticleService;
import com.imooc.enums.ArticleCoverType;
import com.imooc.enums.ArticleReviewStatus;
import com.imooc.enums.YesOrNo;
import com.imooc.exception.GraceException;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.Category;
import com.imooc.pojo.bo.NewArticleBO;
import com.imooc.pojo.vo.ArticleDetailVO;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.PagedGridResult;
import com.mongodb.client.gridfs.GridFSBucket;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ArticleController extends BaseController implements ArticleControllerApi {


    @Autowired
    private ArticleService articleService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Value("${freemarker.html.article}")
    private String articlePath;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Override
    public GraceJSONResult createArticle(@Valid NewArticleBO newArticleBO, BindingResult result) {

        if (newArticleBO.getArticleType() == ArticleCoverType.ONE_IMAGE.type) {
            if (StringUtils.isBlank(newArticleBO.getArticleCover())) {
                return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_COVER_NOT_EXIST_ERROR);
            }
        } else if (newArticleBO.getArticleType() == ArticleCoverType.WORDS.type) {

            newArticleBO.setArticleCover("");

        }


        Integer cId = newArticleBO.getCategoryId();
        String redisAllCat = redis.get(REDIS_ALL_CATEGORY);

        if(StringUtils.isBlank(redisAllCat)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }

        List<Category> categoryList = JsonUtils.jsonToList(redisAllCat, Category.class);
        boolean categoryExist = false;
        for (Category c :
                categoryList) {
            if (c.getId() == cId) {
                categoryExist = true;
                break;
            }

        }

        if(!categoryExist)  return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_CATEGORY_NOT_EXIST_ERROR);

        articleService.createArticle(newArticleBO);


        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult queryMyList(String userId, String keyword, Integer status, Date startDate, Date endDate, Integer page, Integer pageSize) {
        if(StringUtils.isBlank(userId))     return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_QUERY_PARAMS_ERROR);

        if(page == null)  page = COMMON_START_PAGE;

        if(pageSize == null)    pageSize = COMMON_PAGE_SIZE;

        PagedGridResult pagedGridResult = articleService.queryMyList(userId, keyword, status, startDate, endDate, page, pageSize);
        return GraceJSONResult.ok(pagedGridResult);
    }

    @Override
    public GraceJSONResult queryAllList(Integer status, Integer page, Integer pageSize) {

        if(page == null)  page = COMMON_START_PAGE;

        if(pageSize == null)    pageSize = COMMON_PAGE_SIZE;

        PagedGridResult pagedGridResult = articleService.queryAllList(status, page, pageSize);
        return GraceJSONResult.ok(pagedGridResult);
    }

    @Override
    public GraceJSONResult doReview(String articleId, Integer passOrNot) {
        if(StringUtils.isBlank(articleId))  return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);

        Integer newStatus;

        if(passOrNot == YesOrNo.YES.type)   newStatus = ArticleReviewStatus.SUCCESS.type;
        else if(passOrNot == YesOrNo.NO.type) newStatus = ArticleReviewStatus.FAILED.type;
        else return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);

        //注意 updateArticleStatus的实现
        articleService.updateArticleStatus(articleId, newStatus);
        if (newStatus == ArticleReviewStatus.SUCCESS.type) {
            // 审核成功，生成文章详情页静态html
            try {
//                createArticleHTML(articleId);
                String articleMongoId = createArticleHTMLToGridFS(articleId);
                // 存储到对应的文章，进行关联保存
                articleService.updateArticleToGridFS(articleId, articleMongoId);

//                // 调用消费端，执行下载html
//                doDownloadArticleHTML(articleId, articleMongoId);

                // 发送消息到mq队列，让消费者监听并且执行下载html
                doDownloadArticleHTMLByMQ(articleId, articleMongoId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return GraceJSONResult.ok();
    }

    // 文章生成HTML
    public void createArticleHTML(String articleId) throws Exception {

        Configuration cfg = new Configuration(Configuration.getVersion());
        String classpath = this.getClass().getResource("/").getPath();
        cfg.setDirectoryForTemplateLoading(new File(classpath + "templates"));

        Template template = cfg.getTemplate("detail.ftl", "utf-8");

        // 获得文章的详情数据
        ArticleDetailVO detailVO = getArticleDetail(articleId);
        Map<String, Object> map = new HashMap<>();
        map.put("articleDetail", detailVO);

        File tempDic = new File(articlePath);
        if (!tempDic.exists()) {
            tempDic.mkdirs();
        }

        String path = articlePath + File.separator + detailVO.getId() + ".html";

        Writer out = new FileWriter(path);
        template.process(map, out);
        out.close();
    }

    // 文章生成HTML
    public String createArticleHTMLToGridFS(String articleId) throws Exception {

        Configuration cfg = new Configuration(Configuration.getVersion());
        String classpath = this.getClass().getResource("/").getPath();
        cfg.setDirectoryForTemplateLoading(new File(classpath + "templates"));

        Template template = cfg.getTemplate("detail.ftl", "utf-8");

        // 获得文章的详情数据
        ArticleDetailVO detailVO = getArticleDetail(articleId);
        Map<String, Object> map = new HashMap<>();
        map.put("articleDetail", detailVO);

        String htmlContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
//        System.out.println(htmlContent);

        InputStream inputStream = IOUtils.toInputStream(htmlContent);
        ObjectId fileId = gridFSBucket.uploadFromStream(detailVO.getId() + ".html",inputStream);
        return fileId.toString();
    }

    private void doDownloadArticleHTML(String articleId, String articleMongoId) {

        String url =
                "http://html.imoocnews.com:8002/article/html/download?articleId="
                        + articleId +
                        "&articleMongoId="
                        + articleMongoId;
        ResponseEntity<Integer> responseEntity = restTemplate.getForEntity(url, Integer.class);
        int status = responseEntity.getBody();
        if (status != HttpStatus.OK.value()) {
            GraceException.display(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
        }
    }

    private void doDownloadArticleHTMLByMQ(String articleId, String articleMongoId) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_ARTICLE,
                "article.download.do",
                articleId + "," + articleMongoId);
    }

    // 发起远程调用rest，获得文章详情数据
    public ArticleDetailVO getArticleDetail(String articleId) {
        String url
                = "http://www.imoocnews.com:8001/portal/article/detail?articleId=" + articleId;
        ResponseEntity<GraceJSONResult> responseEntity
                = restTemplate.getForEntity(url, GraceJSONResult.class);
        GraceJSONResult bodyResult = responseEntity.getBody();
        ArticleDetailVO detailVO = null;
        if (bodyResult.getStatus() == 200) {
            String detailJson = JsonUtils.objectToJson(bodyResult.getData());
            detailVO = JsonUtils.jsonToPojo(detailJson, ArticleDetailVO.class);
        }
        return detailVO;
    }

    @Override
    public GraceJSONResult delete(String userId, String articleId) {
        articleService.deleteArticle(userId, articleId);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult withdraw(String userId, String articleId) {
        articleService.withdrawArticle(userId, articleId);
        return GraceJSONResult.ok();
    }
}
