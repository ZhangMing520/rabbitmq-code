package com.example.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author zhangming
 * @date 2019/5/30 21:54
 * <p>
 * 生产者
 */
public class Send {

    private static final Logger logger = LoggerFactory.getLogger(Send.class);

    //    队列名称
    private final static String QUEUE_NAME = "hello";

    /**
     * {@link Connection} 就是socket连接，协议与认证都在这完成
     * {@link Channel} 完成大部分数据传输，接收任务
     *
     */
    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection conn = factory.newConnection(); Channel channel = conn.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "hello world";
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());

            logger.info("[x] sent [{}]", message);

        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }
}
