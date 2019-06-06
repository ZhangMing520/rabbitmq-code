package com.example.rabbitmq;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author zhangming
 * @date 2019/6/1 15:54
 */
public class RPCServer {

    private static final Logger logger = LoggerFactory.getLogger(RPCServer.class);

    private static final String RPC_QUEUE_NAME = "rpc_name";

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
            channel.queuePurge(RPC_QUEUE_NAME);

            channel.basicQos(1);

            logger.info("[x awaiting for rpc requests ]");

            Object monitor = new Object();
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                // 返回的消息属性
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties().builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .build();

                String response = "";
                try {
                    String message = new String(delivery.getBody(), "utf-8");
                    int n = Integer.parseInt(message);

                    logger.info("[.].fib({})", n);
                    response += fib(n);
                } catch (RuntimeException e) {
                    logger.error("[.] [{}]", e);
                } finally {
                    channel.basicPublish("", delivery.getProperties().getReplyTo()
                            , replyProps
                            , response.getBytes("utf-8"));
//                    确认消息被收到
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

//                    唤醒 rpc server 进程
                    synchronized (monitor) {
                        monitor.notify();
                    }
                }
            };

            channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, consumerTag -> {
            });

            // 等待回调函数执行完毕
            while (true) {
                synchronized (monitor) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int fib(int n) {
        if (n == 0) {
            return 0;
        }

        if (n == 1) {
            return 1;
        }

        return fib(n - 1) + fib(n - 2);
    }

}
