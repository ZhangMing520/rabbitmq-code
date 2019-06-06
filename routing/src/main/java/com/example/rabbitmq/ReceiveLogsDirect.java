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
 * @date 2019/6/1 14:53
 *
 * info warning error
 */
public class ReceiveLogsDirect {

    private static final Logger logger = LoggerFactory.getLogger(ReceiveLogsDirect.class);

    private static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection conn = factory.newConnection();
        Channel channel = conn.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        String queue = channel.queueDeclare().getQueue();

        if (args.length < 1) {
            logger.info("Usage:ReceiveLogsDirect [info] [warning] [error]");
            System.exit(1);
        }

        for (String logLevel : args) {
            channel.queueBind(queue, EXCHANGE_NAME, logLevel);
        }

        logger.info("[*] waiting for messages. To exit press CTRL+C");

        channel.basicConsume(queue, true, (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "utf-8");

            logger.info("[x] received [{}] :[{}] ", delivery.getEnvelope().getRoutingKey(), message);
        }, consumerTag -> {
        });
    }
}
