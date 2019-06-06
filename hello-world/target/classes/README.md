1. RabbitMQ
> 消息中间件，负责接收和转发消息

2. Queue（队列）
> 队列只受主机的内存和磁盘限制的约束，它本质上是一个大的消息缓冲区。 至少需要200M硬盘空间

##### 注意
1. rabbitmq安装路径不能有空格（最新版本的3.7.4是无需理会安装空格的），这是一个坑

2. 激活管理页面
```
rabbitmq-plugins.bat enable rabbitmq_management

net stop RabbitMQ && net start RabbitMQ

# 页面验证
http://localhost:15672/#/
```