package com.example.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TtlQueueConfig {
    public static final String X_EXCHANGE = "X";
    public static final String QUEUE_A = "QA";
    public static final String QUEUE_B = "QB";
    public static final String Y_DEAD_LETTER_EXCHANGE = "Y";
    public static final String DEAD_LETTER_QUEUE = "QD";
    public static final String QUEUE_C = "QC";




    // 声明 xExchange
    @Bean("xExchange")
    public DirectExchange xExchange() {
        return new DirectExchange(X_EXCHANGE);
    }

    // 声明 yExchange
    @Bean("yExchange")
    public DirectExchange yExchange() {
        return new DirectExchange(Y_DEAD_LETTER_EXCHANGE);
    }

    //队列 A ttl 为 10s ，并绑定到对应的死信交换机
    @Bean("queueA")
    public Queue queueA() {
        Map<String, Object> args = new HashMap<>(3);
        //声明当前队列绑定的死信交换机，设置DLX
        args.put("x-dead-letter-exchange", Y_DEAD_LETTER_EXCHANGE);
        //声明当前队列的死信路由 key
        args.put("x-dead-letter-routing-key", "YD");     ////设置DLX的路由键
        //设置队列上所有的消息的有效期,单位为毫秒
        args.put("x-message-ttl", 10000);
        //设置队列有效期，单位为毫秒
//        args.put("x-expires", 10000);
        //durable持久化
        return QueueBuilder.durable(QUEUE_A).withArguments(args).build();
    }


    //声明队列 B ttl 为 40s ，并绑定到对应的死信交换机
    @Bean("queueB")
    public Queue queueB() {
        Map<String, Object> args = new HashMap<>(3);
        //声明当前队列绑定的死信交换机，设置DLX
        args.put("x-dead-letter-exchange", Y_DEAD_LETTER_EXCHANGE);
        //声明当前队列的死信路由 key
        args.put("x-dead-letter-routing-key", "YD");  //设置DLX的路由键
        //声明队列消息的 TTL
        args.put("x-message-ttl", 40000);
        return QueueBuilder.durable(QUEUE_B).withArguments(args).build();
    }


    @Bean("queueC")
    public Queue queueC(){
        Map<String,Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", Y_DEAD_LETTER_EXCHANGE);
        args.put("x-dead-letter-routing-key", "YD");
        return QueueBuilder.durable(QUEUE_C).withArguments(args).build();
    }





    //死信队列 QD
    @Bean("queueD")
    public Queue queueD() {
        return new Queue(DEAD_LETTER_QUEUE);
    }


    //队列 A 绑定 X 交换机
    @Bean
    public Binding queueABindingX(@Qualifier("queueA") Queue queueA,
                                  @Qualifier("xExchange") DirectExchange xExchange) {
        return BindingBuilder.bind(queueA).to(xExchange).with("XA");
    }


    //队列 B 绑定 X 交换机
    @Bean
    public Binding queueBBindingX(@Qualifier("queueB") Queue queue1B,
                                  @Qualifier("xExchange") DirectExchange xExchange) {
        return BindingBuilder.bind(queue1B).to(xExchange).with("XB");
    }

    //队列 C 绑定 X 交换机
    @Bean
    public Binding queueCBindingX(@Qualifier("queueC") Queue queue1B,
                                  @Qualifier("xExchange") DirectExchange xExchange) {
        return BindingBuilder.bind(queue1B).to(xExchange).with("XC");
    }

    //死信队列 QD 绑定 Y 交换机
    @Bean
    public Binding queueDBindingY(@Qualifier("queueD") Queue queueD,
                                        @Qualifier("yExchange") DirectExchange yExchange) {
        return BindingBuilder.bind(queueD).to(yExchange).with("YD");
    }
}
