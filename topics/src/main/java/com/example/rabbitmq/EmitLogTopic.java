package com.example.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author zhangming
 * @date 2019/6/1 15:19
 *
 * kern.critical  "A critical kernel error"
 */
public class EmitLogTopic {

    private static final Logger logger = LoggerFactory.getLogger(EmitLogTopic.class);

    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection conn = factory.newConnection(); Channel channel = conn.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

            String routingKey = args[0];
            String message = args[1];

            channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("utf-8"));

            logger.info("[x] sent [{}] : [{}]", routingKey, message);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
