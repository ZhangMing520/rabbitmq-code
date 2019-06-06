package com.example.rabbitmq;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

/**
 * @author zhangming
 * @date 2019/6/1 16:11
 */
public class RPCClient implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(RPCClient.class);

    private String requestQueueName  = "rpc_name";
    private Connection connection;
    private Channel channel;

    public RPCClient() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        this.connection = factory.newConnection();
        this.channel = connection.createChannel();
    }

    public static void main(String[] args) {
        try (RPCClient fibonacciRpc = new RPCClient()) {
            for (int i = 0; i < 32; i++) {
                String i_str = Integer.toString(i);
                logger.info("[x] requesting fib({})", i_str);

                String response = fibonacciRpc.call(i_str);
                logger.info("[.]Got [{}]", response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String call(String message) throws IOException, InterruptedException {
        final String corrId = UUID.randomUUID().toString();

        // 设置 replyTo  correlationId
        String replyQueueName = channel.queueDeclare().getQueue();
        AMQP.BasicProperties properties = new AMQP.BasicProperties().builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish("", requestQueueName, properties, message.getBytes("utf-8"));

        final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

        String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response.offer(new String(delivery.getBody(), "utf-8"));
            }
        }, consumerTag -> {
        });

        // 阻塞
        String result = response.take();
        channel.basicCancel(ctag);
        return result;
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
