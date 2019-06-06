package com.example.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeoutException;

/**
 * @author zhangming
 * @date 2019/6/1 11:32
 */
public class EmitLog {

    private static final Logger logger = LoggerFactory.getLogger(EmitLog.class);

    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection conn = factory.newConnection(); Channel channel = conn.createChannel()) {

            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

            String message = args.length < 1 ? "info:hello world" : String.join(" ", args);
            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes(Charset.forName("UTF-8")));

            logger.info("[x] sent [{}]", message);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
