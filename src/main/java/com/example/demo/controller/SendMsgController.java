package com.example.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/ttl")
public class SendMsgController {


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMsg")
    public void sendMsg(String message) {
        log.info("当前时间：{},发送一条信息给两个 TTL 队列:{}", new Date(), message);
        /**
         * convertAndSend
         * 参数1：交换机名
         * 参数2：routingKey
         * 参数3：消息
         */
        rabbitTemplate.convertAndSend("X", "XA", "消息来自 ttl 为 10S 的队列: " + message);
        rabbitTemplate.convertAndSend("X", "XB", "消息来自 ttl 为 40S 的队列: " + message);
    }


    @GetMapping("/sendExpirationMsg/{message}/{ttlTime}")
    public void sendMsg(@PathVariable String message,@PathVariable String ttlTime) {

        rabbitTemplate.convertAndSend("X", "XC", message, correlationData ->{
            correlationData.getMessageProperties().setExpiration(ttlTime);
            return correlationData;
        });


//        rabbitTemplate.convertAndSend("X", "XC", message, new MessagePostProcessor() {
//            @Override
//            public Message postProcessMessage(Message message) throws AmqpException {
//                return null;
//            }
//        });

        log.info("当前时间：{},发送一条时长{}毫秒 TTL 信息给队列 C:{}", new Date(),ttlTime, message);
    }
}
