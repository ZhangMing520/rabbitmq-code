1. RabbitMQ
> 消息中间件，负责接收和转发消息

2. Queue（队列）
> 队列只受主机的内存和磁盘限制的约束，它本质上是一个大的消息缓冲区。 至少需要200M硬盘空间
- 生产者发消息
- 队列存储消息，是一个buffer
- 消费者接收消息

3. Work Queues（工作队列）
> 不适用于资源密集型任务和同步任务（必须等待完成才能执行后面）。将任务封装成消息，发送到队列。工作线程在后台拉取任务，处理任务，可以运行多个工作线程。适用于异步任务（在短时间内需要返回的http）。很容易并行工作，消息分发到多个worker默认是轮询分发

> 确认消息必须是同一个channel发送，否则会报错（a channel-level protocol exception）。没有设置消息自动确认情况，没有收到ack，那么会重新将消息放入队列。
消息处理没有超时。

- ack 保证任务完成  
- basicQos 保证工作线程之间负载平衡 
- durable  保证队列的持久化
- MessageProperties  保证消息的持久化

4. exchange
> 生产者并不是直接发送消息给队列，而是发给exchange。一方面exchange从生产者接收信息，
另一方面exchange将这些消息推送给队列。exchange必须明确消息如何处理。是应该推送给特殊队列，还是发送到很多队列，或者丢弃。exchange type 定义了这些处理规则。

- direct
- topic
- headers
- fanout   广播消息到所有队列


5. publish/subscribe
> 不同于work queue，每个任务发送给一个worker。这种模式发送消息给多个消费者。

6. routing
> 不再是广播消息给所有消费者。而是发送消息给其中一部分子集。
- fanout  将会忽略routingKey的值，只能进行广播
- direct  消息会被发送给具有一样routingKey的队列，exchange同样可以绑定多个相同routingKey的队列，这样就可以实现fanout模式

7. topics（正则匹配routingKey）
> topic exchange的routingKey是一系列的词，用.分隔（kern.*  *.critical），上限是255bytes。消息会被发送给具有一样routingKey的队列。server是正则，client具体值。
- \* 可以匹配一个单词
- \# 可以匹配0或者多个单词

> topics可以代替fanout和direct模式，routingKey是\#时候，类似fanout，没有\*或者\#时候，类似direct

8. rpc 
> 远程过程调用，同步过程。
- 方法一：每个请求设置一个回调队列（低效）
- 方法二：一个client设置一个回调队列，通过correlationId匹配请求和响应

9. Message properties
- deliveryMode     消息是否存储 
- contentType      mime-type（例如application/json）
- replyTo          声明回调的队列名称
- correlationId    用于关联rpc请求与响应


##### 注意
1. rabbitmq安装路径不能有空格（最新版本的3.7.4是无需理会安装空格的），这是一个坑

2. 激活管理页面
```
rabbitmq-plugins.bat enable rabbitmq_management

net stop RabbitMQ && net start RabbitMQ

# 页面验证
http://localhost:15672/#/

# 查看消息
rabbitmqctl list_queues

# 查看未确认消息
rabbitmqctl list_queues name messages_ready messages_unacknowledged

# 查看 exchange 
rabbitmqctl list_exchanges

# 查看队列和exchange的绑定
rabbitmqctl list_bindings
```

```JAVA
// 使用了默认 exchange，名称是 ""。
// default exchange 或者 nameless exchange：消息将会根据routingKey路由到队列
channel.basicPublish("", "hello", null, message.getBytes());
```

3. temporary queue（使用场景）
- 需要获取所有的消息，而不是某一个子集
- 只对当前的消息感兴趣，不需要理会之前的消息

> 当我们连接rabbit时候，server会生成一个随机队列；一旦所有消费者disconnect，队列将会自动删除

```java
// 创建一个未持久化，自动删除队列
String queueName = channel.queueDeclare().getQueue();
```
