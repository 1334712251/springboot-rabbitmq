package com.example.demo.Consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Slf4j
@Component
public class WarningConsumer {


    @RabbitListener(queues = "warning_queue")
    public void receiveWarningMsg(Message message, Channel channel) throws IOException {

        String msg = new String(message.getBody());
        log.info("当前时间：{},收到报警队列信息{}", new Date().toString(), msg);

    }
}
