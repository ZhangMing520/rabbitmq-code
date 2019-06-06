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
 * @date 2019/6/1 15:24
 *
 *
 * kern.*  *.critical
 */
public class ReceiveLogsTopic {

    private static final Logger logger = LoggerFactory.getLogger(ReceiveLogsTopic.class);

    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection conn = factory.newConnection();
        Channel channel = conn.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        String queue = channel.queueDeclare().getQueue();

        if (args.length < 1) {
            logger.info("Usage: ReceiveLogsTopic [binding key]...");
            System.exit(1);
        }

        for (String bindingKey : args) {
            channel.queueBind(queue, EXCHANGE_NAME, bindingKey);
        }

        logger.info("[*] Waiting for messages. To exit press CTRL+C");

        channel.basicConsume(queue, true, (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            logger.info(" [x] Received : [{}] : [{}] ", delivery.getEnvelope().getRoutingKey(), message);
        }, consumerTag -> {

        });
    }
}
