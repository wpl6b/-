package com.imooc.article.html;

import com.imooc.api.config.RabbitMQConfig;
import com.imooc.article.html.controller.ArticleHTMLComponent;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumer {

    @Autowired
    private ArticleHTMLComponent articleHTMLComponent;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_DOWNLOAD_HTML)
    public void watchQueue(String payload, Message message){
        System.out.println(payload);
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        if(routingKey.equalsIgnoreCase("article.download.do")){
            String articleId = payload.split(",")[0];
            String articleMongoId = payload.split(",")[1];
            try {
                articleHTMLComponent.download(articleId, articleMongoId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
