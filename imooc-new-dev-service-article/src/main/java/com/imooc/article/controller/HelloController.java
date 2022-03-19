package com.imooc.article.controller;

import com.imooc.api.config.RabbitDelayMQConfig;
import com.imooc.api.config.RabbitMQConfig;
import com.imooc.grace.result.GraceJSONResult;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("producer")
public class HelloController{
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("hello")
    public Object hello() {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_ARTICLE, "article.hello", "这是生产者发送的消息");
        return GraceJSONResult.ok();
    }

    @GetMapping("delay")
    public Object delay() {
        MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                MessageProperties messageProperties = message.getMessageProperties();
                messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                messageProperties.setDelay(5000);
                return message;
            }
        };
        rabbitTemplate.convertAndSend(RabbitDelayMQConfig.EXCHANGE_DELAY, "publish.delay", "这是生产者发送的延迟消息", messagePostProcessor);
        System.out.println("发送时间" + new Date());
        return GraceJSONResult.ok();
    }
}
