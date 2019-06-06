package com.example.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import javafx.scene.shape.QuadCurve;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author zhangming
 * @date 2019/5/30 22:06
 * <p>
 * 消费者
 */
public class Recv {

    private static final Logger logger = LoggerFactory.getLogger(Recv.class);

    //    队列名称
    private final static String QUEUE_NAME = "hello";


    /**
     * 这里不能使用 try() 关闭流，这是服务端，不需要关闭
     */
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection conn = factory.newConnection();
        Channel channel = conn.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);


        channel.basicConsume(QUEUE_NAME, true, (consumerTag, delivery) -> {

            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            logger.info("[x] received [{}]", message);

        }, consumerTag -> {
        });

        logger.info("[*] waiting for message.To exit press CTRL+C");
    }
}
