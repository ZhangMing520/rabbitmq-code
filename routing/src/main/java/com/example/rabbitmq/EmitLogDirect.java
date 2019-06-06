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
 * @date 2019/6/1 14:44
 *
 * info    infomsg
 * warning  warningmsg
 * error  errormsg
 */
public class EmitLogDirect {

    private static final Logger logger = LoggerFactory.getLogger(EmitLogDirect.class);

    private static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection conn = factory.newConnection(); Channel channel = conn.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

            String logLevel = args[0];
            String message = args[1];
            channel.basicPublish(EXCHANGE_NAME, logLevel, null, message.getBytes(Charset.forName("utf-8")));

            logger.info("[x] sent [{}]:[{}]", logLevel, message);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
