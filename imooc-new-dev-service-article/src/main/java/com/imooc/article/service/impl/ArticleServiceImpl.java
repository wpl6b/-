package com.imooc.article.service.impl;

import com.github.pagehelper.PageHelper;
import com.imooc.api.BaseService;
import com.imooc.api.config.RabbitDelayMQConfig;
import com.imooc.article.mapper.ArticleMapper;
import com.imooc.article.mapper.ArticleMapperCustom;
import com.imooc.article.service.ArticleService;
import com.imooc.enums.ArticleAppointType;
import com.imooc.enums.ArticleReviewStatus;
import com.imooc.enums.YesOrNo;
import com.imooc.exception.GraceException;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.Article;
import com.imooc.pojo.bo.NewArticleBO;
import com.imooc.utils.DateUtil;
import com.imooc.utils.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class ArticleServiceImpl extends BaseService implements ArticleService
{
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleMapperCustom articleMapperCustom;

    @Autowired
    private Sid sid;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public void createArticle(NewArticleBO newArticleBO) {
        String articleId = sid.nextShort();

        Article article = new Article();
        BeanUtils.copyProperties(newArticleBO, article);
        article.setId(articleId);
        article.setCategoryId(newArticleBO.getCategoryId());
        article.setArticleStatus(ArticleReviewStatus.REVIEWING.type);
        article.setCommentCounts(0);
        article.setReadCounts(0);
        article.setIsDelete(YesOrNo.NO.type);
        article.setCreateTime(new Date());
        article.setUpdateTime(new Date());

        if(newArticleBO.getIsAppoint() == ArticleAppointType.TIMING.type){
            article.setPublishTime(newArticleBO.getPublishTime());
        }else if(newArticleBO.getIsAppoint() == ArticleAppointType.IMMEDIATELY.type){
            article.setPublishTime(new Date());
        }

        int insert = articleMapper.insert(article);
        if(insert != 1){
            GraceException.display(ResponseStatusEnum.ARTICLE_CREATE_ERROR);
        }

        //发送延迟消息
        if(newArticleBO.getIsAppoint() == ArticleAppointType.TIMING.type){
            Date endDate = article.getPublishTime();
            Date startDate = new Date();
            int timeGap = (int)(endDate.getTime() - startDate.getTime());
//            int timeGap = 10000;

            System.out.println("timeBetween :" + DateUtil.timeBetween(startDate, endDate));



            MessagePostProcessor messagePostProcessor = message -> {
                MessageProperties messageProperties = message.getMessageProperties();
                messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                messageProperties.setDelay(timeGap);
                return message;
            };
            rabbitTemplate.convertAndSend(RabbitDelayMQConfig.EXCHANGE_DELAY, "publish.delay.display", articleId, messagePostProcessor);
            System.out.println("延迟定时发布文章 " + new Date());

        }
    }


    @Override
    @Transactional
    public void  updateAppointToPublish() {
        articleMapperCustom.updateAppointToPublish();
    }

    @Transactional
    @Override
    public void updateArticleToPublish(String articleId) {
        Article article = new Article();
        article.setId(articleId);
        article.setIsAppoint(ArticleAppointType.IMMEDIATELY.type);
        articleMapper.updateByPrimaryKeySelective(article);
    }

    @Override
    public PagedGridResult queryMyList(String userId, String keyword, Integer status, Date startDate, Date endDate, Integer page, Integer pageSize) {
        Example example = new Example(Article.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("publishUserId", userId)
                .andEqualTo("isDelete", YesOrNo.NO.type);

        if(StringUtils.isNotBlank(keyword))     criteria.andEqualTo("title", "%" + keyword + "%");


        if(ArticleReviewStatus.isArticleStatusValid(status))    criteria.andEqualTo("articleStatus", status);

        //前端代码中定义12为机审或人审
        if(status != null && status == 12){
            criteria.andEqualTo("articleStatus", ArticleReviewStatus.REVIEWING.type)
                    .orEqualTo("articleStatus", ArticleReviewStatus.WAITING_MANUAL.type);
        }

        if(startDate != null)   criteria.andGreaterThanOrEqualTo("publishTime", startDate);

        if(endDate != null)     criteria.andLessThanOrEqualTo("publishTime", endDate);

        PageHelper.startPage(page, pageSize);
        List<Article> articles = articleMapper.selectByExample(example);

        return setterPagedGrid(articles, page);
    }

    @Override
    public PagedGridResult queryAllList(Integer status, Integer page, Integer pageSize) {
        Example example = new Example(Article.class);
        example.orderBy("createTime").desc();

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("isDelete", YesOrNo.NO.type);

        if (ArticleReviewStatus.isArticleStatusValid(status)) {
            criteria.andEqualTo("articleStatus", status);
        }

        //前端代码中定义12为机审或人审
        if(status != null && status == 12){
            criteria.andEqualTo("articleStatus", ArticleReviewStatus.REVIEWING.type)
                    .orEqualTo("articleStatus", ArticleReviewStatus.WAITING_MANUAL.type);
        }

        PageHelper.startPage(page, pageSize);
        List<Article> articles = articleMapper.selectByExample(example);

        return setterPagedGrid(articles, page);
    }

    @Override
    @Transactional
    public void updateArticleStatus(String articleId, Integer newStatus) {
        Example example = new Example(Article.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", articleId);

        Article article = new Article();
        article.setArticleStatus(newStatus);
        articleMapper.updateByExampleSelective(article, example);
    }

    @Transactional
    @Override
    public void deleteArticle(String userId, String articleId) {
        Example example = makeExampleCriteria(userId, articleId);

        Article article = new Article();
        article.setIsDelete(YesOrNo.YES.type);

        int result = articleMapper.updateByExampleSelective(article, example);
        if(result != 1) GraceException.display(ResponseStatusEnum.ARTICLE_DELETE_ERROR);
    }

    @Override
    public void withdrawArticle(String userId, String articleId) {
        Example example = makeExampleCriteria(userId, articleId);

        Article article = new Article();
        article.setArticleStatus(ArticleReviewStatus.WITHDRAW.type);

        int result = articleMapper.updateByExampleSelective(article, example);
        if(result != 1) GraceException.display(ResponseStatusEnum.ARTICLE_WITHDRAW_ERROR);
    }


    private Example makeExampleCriteria(String userId, String articleId) {
        Example articleExample = new Example(Article.class);
        Example.Criteria criteria = articleExample.createCriteria();
        criteria.andEqualTo("publishUserId", userId);
        criteria.andEqualTo("id", articleId);
        return articleExample;
    }

    @Transactional
    @Override
    public void updateArticleToGridFS(String articleId, String articleMongoId) {
        Article article = new Article();
        article.setId(articleId);
        article.setMongoFileId(articleMongoId);
        articleMapper.updateByPrimaryKeySelective(article);
    }
}
