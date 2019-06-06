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
 * @date 2019/6/1 11:41
 */
public class ReceiveLogs {

    private static final Logger logger = LoggerFactory.getLogger(ReceiveLogs.class);

    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection conn = factory.newConnection();
        Channel channel = conn.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        String queue = channel.queueDeclare().getQueue();
        channel.queueBind(queue, EXCHANGE_NAME, "");

        logger.info("[*] waiting for messages. To exit press CTRL+C");

        channel.basicConsume(queue, true, (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");

            logger.info("[x] received [{}]", message);
        }, consumerTag -> {

        });
    }
}
