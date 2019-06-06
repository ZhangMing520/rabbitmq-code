package com.example.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author zhangming
 * @date 2019/5/30 22:34
 */
public class Worker {

    private static final Logger logger = LoggerFactory.getLogger(Worker.class);

    private final static String TASK_QUEUE_NAME = "task_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection conn = factory.newConnection();
        Channel channel = conn.createChannel();

        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
        logger.info(" [*] Waiting for messages. To exit press CTRL+C");


//    同一时间只处理一个消息，轮询分发可能导致一个worker负载很重，一个很轻松
//        假设奇数任务很重，偶数任务很轻，basicQos平均了负载

        channel.basicQos(1);

        // true 消息被发送之后，不需要确认，就算worker失败未处理完成等情况，也不再次发送
//        false 确认明确的回复（ack）失败消息会重新入队列
        boolean autoAck = true;
        channel.basicConsume(TASK_QUEUE_NAME, autoAck, (consumerTag, message) -> {
            String msg = new String(message.getBody(), StandardCharsets.UTF_8);
            logger.info("[x] received [{}]", msg);

            try {
                doWork(msg);
            } finally {
                logger.info("[x] done");
            }
        }, consumerTag -> {

        });
    }

    /**
     * 模拟负载
     *
     * @param task
     */
    private static void doWork(String task) {
        for (char ch : task.toCharArray()) {
            if (ch == '.') {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
