package com.example.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author zhangming
 * @date 2019/5/30 22:29
 * <p>
 * program arguments: hello...
 */
public class NewTask {

    private static final Logger logger = LoggerFactory.getLogger(NewTask.class);

    private final static String TASK_QUEUE_NAME = "task_queue";

    /**
     * {@link  MessageProperties#PERSISTENT_TEXT_PLAIN}
     * 消息持久化，但是这种保证不是绝对的，可能只是保存在缓存中，并且每个消息的保存是异步的，不一定有时间保存
     * <p>
     * {@code durable} 队列持久化
     */
    public static void main(String[] args) {
        String message = String.join(" ", args);
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection conn = factory.newConnection(); Channel channel = conn.createChannel()) {
//            队列持久化
            boolean durable = true;
            channel.queueDeclare(TASK_QUEUE_NAME, durable, false, false, null);

//            消息持久化
            channel.basicPublish("", TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            logger.info("[x] sent [{}]", message);

        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
