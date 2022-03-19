package com.imooc.article;

import com.imooc.api.config.RabbitDelayMQConfig;
import com.imooc.article.service.ArticleService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class RabbitDelayMQConsumer {

    @Autowired
    private ArticleService articleService;

    @RabbitListener(queues = RabbitDelayMQConfig.QUEUE_DELAY)
    public void watchQueue(String payload, Message message){
        System.out.println(payload);
        MessageProperties messageProperties = message.getMessageProperties();
        System.out.println("routingKey : " + messageProperties.getReceivedRoutingKey());
        System.out.println("延迟消息的接收时间" + new Date());
        String articleId = payload;
        articleService.updateArticleToPublish(articleId);

    }
}
