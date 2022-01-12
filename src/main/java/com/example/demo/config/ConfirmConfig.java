package com.example.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfirmConfig {

    public static final String CONFIRM_EXCHANGE_NAME = "confirm.exchange";
    public static final String CONFIRM_QUEUE_NAME = "confirm.queue";
    public static final String BACKUP_EXCHANGE_NAME = "backup_exchange";
    public static final String WARNING_QUEUE_NAME = "warning_queue";
    public static final String BACKUP_QUEUE_NAME = "backup_queue";


//    //声明确认交换机 Exchange
//    @Bean("confirmExchange")
//    public DirectExchange confirmExchange() {
//        return new DirectExchange(CONFIRM_EXCHANGE_NAME);
//    }


    //声明确认 Exchange 交换机的备份交换机
    @Bean("confirmExchange")
    public DirectExchange confirmExchange() {
        ExchangeBuilder exchangeBuilder = ExchangeBuilder.directExchange(CONFIRM_EXCHANGE_NAME)
                .durable(true)
                //设置该交换机的备份交换机
                .withArgument("alternate-exchange", BACKUP_EXCHANGE_NAME);
        return exchangeBuilder.build();
    }


    // 声明确认队列
    @Bean("confirmQueue")
    public Queue confirmQueue() {
        return QueueBuilder.durable(CONFIRM_QUEUE_NAME).build();
    }


    // 声明确认队列绑定关系
    @Bean
    public Binding queueBinding(@Qualifier("confirmQueue") Queue queue,
                                @Qualifier("confirmExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("key1");
    }

    //声明备份 Exchange
    @Bean("backupExchange")
    public FanoutExchange backupExchange() {

        //备份交换机是扇出类型，扇出类型不用写routingkey
        return new FanoutExchange(BACKUP_EXCHANGE_NAME);
    }



    // 声明警告队列
    @Bean("warningQueue")
    public Queue warningQueue() {
        return QueueBuilder.durable(WARNING_QUEUE_NAME).build();
    }

    // 报警队列与备份交换机绑定关系
    @Bean
    public Binding warningBinding(@Qualifier("warningQueue") Queue queue,
                                  @Qualifier("backupExchange") FanoutExchange backupExchange) {

        return BindingBuilder.bind(queue).to(backupExchange);   //没有绑定routingkey
    }

    // 声明备份队列
    @Bean("backQueue")
    public Queue backQueue() {

        return QueueBuilder.durable(BACKUP_QUEUE_NAME).build();
    }

    // 备份队列与备份交换机绑定关系
    @Bean
    public Binding backupBinding(@Qualifier("backQueue") Queue queue,
                                 @Qualifier("backupExchange") FanoutExchange backupExchange) {

        return BindingBuilder.bind(queue).to(backupExchange); //没有绑定routingkey
    }
}