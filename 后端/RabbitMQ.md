# RabbitMQ

## 1. 下载和安装

[RabbitMQ官网](https://www.rabbitmq.com/download.html)

**erlang**

参考地址：https://www.erlang-solutions.com/downloads/

```bash
wget https://packages.erlang-solutions.com/erlang-solutions-2.0-1.noarch.rpm
rpm -Uvh erlang-solutions-2.0-1.noarch.rpm
yum install -y erlang

erl -v
```

**socat**

```bash
yum install -y socat
```

**rabbitmq**

参考地址：https://www.rabbitmq.com/download.html

![img](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/kuangstudy6323e2bf-a056-4aec-a124-13dd814dc9c2.png)

```bash
wget https://github.com/rabbitmq/rabbitmq-server/releases/download/v3.9.13/rabbitmq-server-3.9.13-1.el8.noarch.rpm
rpm -Uvh rabbitmq-server-3.9.13-1.el8.noarch.rpm
yum install rabbitmq-server -y
```

```bash
# 启动服务
systemctl start rabbitmq-server
# 查看服务状态
systemctl status rabbitmq-server
# 停止服务
systemctl stop rabbitmq-server

# 重启服务
systemctl restart rabbitmq-server
# 开机启动服务
systemctl enable rabbitmq-server
```

**docker中安装**

```bash
# 运行
docker run -di --name=myrabbit -p 15672:15672 rabbitmq:management
# 配置用户密码运行
docker run -di --name myrabbit -e RABBITMQ_DEFAULT_USER=admin -e RABBITMQ_DEFAULT_PASS=admin -p 15672:15672 -p 5672:5672 -p 25672:25672 -p 61613:61613 -p 1883:1883 rabbitmq:management
# 查看日志
docker logs -f myrabbit
```

## 2. 管理界面

**开启管理界面插件**

```bash
rabbitmq-plugins enable rabbitmq_management
```

rabbitmq有一个默认账号和密码是：`guest` 默认情况只能在localhost本机下访问，所以需要添加一个远程登录的用户

重启服务

```bash
systemctl restart rabbitmq-server
```

http://47.100.36.90:15672/

**授权账号和密码**

```bash
rabbitmqctl add_user 账号 密码
rabbitmqctl set_user_tags 账号 administrator
rabbitmqctl change_password Username Newpassword 修改密码
rabbitmqctl delete_user Username 删除用户
rabbitmqctl list_users 查看用户清单
rabbitmqctl set_permissions -p / 用户名 ".*" ".*" ".*" 为用户设置administrator角色
rabbitmqctl set_permissions -p / root ".*" ".*" ".*"
```

**角色分类**

**none**：

- 不能访问management plugin

**management**：查看自己相关节点信息

- 列出自己可以通过AMQP登入的虚拟机
- 查看自己的虚拟机节点 virtual hosts的queues,exchanges和bindings信息
- 查看和关闭自己的channels和connections
- 查看有关自己的虚拟机节点virtual hosts的统计信息。包括其他用户在这个节点virtual hosts中的活动信息。

**Policymaker**

- 包含management所有权限
- 查看和创建和删除自己的virtual hosts所属的policies和parameters信息。

**Monitoring**

- 包含management所有权限
- 罗列出所有的virtual hosts，包括不能登录的virtual hosts。
- 查看其他用户的connections和channels信息
- 查看节点级别的数据如clustering和memory使用情况
- 查看所有的virtual hosts的全局统计信息。

**Administrator**

- 最高权限
- 可以创建和删除virtual hosts
- 可以查看，创建和删除users
- 查看创建permisssions
- 关闭所有用户的connections

**具体操作的界面**

![img](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/kuangstudy8888eeb0-3146-432b-9201-4f9622ca9c74.png)

## 3. 快速入门

导入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

生产者

```java
public class Producer {
    public static void main(String[] args) {
        // 1: 创建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        // 2: 设置连接属性
        connectionFactory.setHost("47.100.36.90");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("Ytc19980211..");
        Connection connection = null;
        Channel channel = null;
        try {
            // 3: 从连接工厂中获取连接
            connection = connectionFactory.newConnection("生产者");
            // 4: 从连接中获取通道channel
            channel = connection.createChannel();
            // 5: 申明队列queue存储消息
            /*
             *  如果队列不存在，则会创建
             *  Rabbitmq不允许创建两个相同的队列名称，否则会报错。
             *
             *  @params1： queue 队列的名称
             *  @params2： durable 队列是否持久化
             *  @params3： exclusive 是否排他，即是否私有的，如果为true,会对当前队列加锁，其他的通道不能访问，并且连接自动关闭
             *  @params4： autoDelete 是否自动删除，当最后一个消费者断开连接之后是否自动删除消息。
             *  @params5： arguments 可以设置队列附加参数，设置队列的有效期，消息的最大长度，队列的消息生命周期等等。
             * */
            String queueName = "queue1";
            channel.queueDeclare(queueName, true, false, false, null);
            // 6： 准备发送消息的内容
            String message = "你好，RabbitMQ！！！";
            // 7: 发送消息给中间件rabbitmq-server
            // @params1: 交换机exchange
            // @params2: 队列名称/routing
            // @params3: 属性配置
            // @params4: 发送消息的内容
            channel.basicPublish("", queueName, null, message.getBytes());
            System.out.println("消息发送成功!");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("出现异常...");
        } finally {
            // 7: 释放连接关闭通道
            if (channel != null && channel.isOpen()) {
                try {
                    channel.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
```

消费者

```java
public class Consumer {
    public static void main(String[] args) {
        // 1: 创建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        // 2: 设置连接属性
        connectionFactory.setHost("47.100.36.90");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("Ytc19980211..");
        Connection connection = null;
        Channel channel = null;
        try {
            // 3: 从连接工厂中获取连接
            connection = connectionFactory.newConnection("消费者");
            // 4: 从连接中获取通道channel
            channel = connection.createChannel();
            // 5: 
            channel.basicConsume("queue1", true, new DeliverCallback() {
                @Override
                public void handle(String consumerTag, Delivery message) throws IOException {
                    System.out.println(new String(message.getBody(), "UTF-8"));
                }
            }, new CancelCallback() {
                @Override
                public void handle(String consumerTag) throws IOException {
                    System.out.println("消息接受失败!");
                }
            });
            System.out.println("开始接受消息!");
            System.in.read();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("出现异常...");
        } finally {
            // 7: 释放连接关闭通道
            if (channel != null && channel.isOpen()) {
                try {
                    channel.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
```

![img](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/kuangstudy51240e70-79d7-4b3f-9a83-6febb3499a42.png)

![img](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/kuangstudydf396d39-9059-49fd-aaaa-a41b027c2a4b.png)

![img](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/kuangstudyed656121-1e27-4c0e-84e7-70ae48f3b0f1.png)

![img](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/kuangstudydc7c7bf4-bffe-4821-92da-c1c8563631d3.png)

## 4. AMQP

AMQP全称：Advanced Message Queuing Protocol(高级消息队列协议)。是应用层协议的一个开发标准，为面向消息的中间件设计。

**AMQP生产者流转过程**

![img](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/kuangstudy7c8a41b8-e3bf-4821-a1f1-a18860277663.png)

**AMQP消费者流转过程**

![img](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/kuangstudy081077ba-eced-43f9-b148-6f63987f1d2f.png)

**核心组成部分**

![img](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/kuangstudy62a1f9e3-027d-408a-8fb4-a176bd184d23.png)

核心概念：
**Server**：又称Broker ,接受客户端的连接，实现AMQP实体服务。 安装rabbitmq-server
**Connection**：连接，应用程序与Broker的网络连接 TCP/IP/ 三次握手和四次挥手
**Channel**：网络信道，几乎所有的操作都在Channel中进行，Channel是进行消息读写的通道，客户端可以建立对各Channel，每个Channel代表一个会话任务。
**Message** :消息：服务与应用程序之间传送的数据，由Properties和body组成，Properties可是对消息进行修饰，比如消息的优先级，延迟等高级特性，Body则就是消息体的内容。
**Virtual Host** 虚拟地址，用于进行逻辑隔离，最上层的消息路由，一个虚拟主机理由可以有若干个Exhange和Queueu，同一个虚拟主机里面不能有相同名字的Exchange
**Exchange**：交换机，接受消息，根据路由键发送消息到绑定的队列。(==不具备消息存储的能力==)
**Bindings**：Exchange和Queue之间的虚拟连接，binding中可以保护多个routing key.
**Routing key**：是一个路由规则，虚拟机可以用它来确定如何路由一个特定消息。
**Queue**：队列：也成为Message Queue,消息队列，保存消息并将它们转发给消费者。

**整体架构**？

![img](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/kuangstudy23e6e571-d661-4f4b-b4f4-4d4efb766bc3.png)

**运行流程**

![img](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/kuangstudy2704cee9-3595-45de-892d-ee658e848806.png)

rabbitmq发送消息一定有一个交换机

如果队列没有指定交换机会默认绑定一个交换机
![img](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/kuangstudye28575ea-17f4-41a8-ac32-133727fd63ae.png)

![img](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/kuangstudyd23fdb11-89c8-4883-a027-76d93d257138.png)

可以在管理界面创建交换机指定类型，绑定队列，设置匹配的routing keys

## 5. Fanout

通过直接指定queue发送

**生产者**

```java
public class Producer {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("47.100.36.90");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("Ytc19980211..");
        Connection connection = null;
        Channel channel = null;
        try {
            connection = connectionFactory.newConnection("生产者");
            channel = connection.createChannel();
            String message = "你好，RabbitMQ！！！";
            String exchangeName = "fanout-exchange";
            String routingKey = "";
            channel.basicPublish(exchangeName, routingKey, null, message.getBytes());
            System.out.println("消息发送成功!");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("出现异常...");
        } finally {
            if (channel != null && channel.isOpen()) {
                try {
                    channel.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
```

**消费者**

```java
public class Consumer {
    public static void main(String[] args) {
        new Thread(runnable, "queue1").start();
        new Thread(runnable, "queue2").start();
        new Thread(runnable, "queue3").start();
    }

    private static Runnable runnable = () -> {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("47.100.36.90");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("Ytc19980211..");
        final String queueName = Thread.currentThread().getName();
        Connection connection = null;
        Channel channel = null;
        try {
            connection = connectionFactory.newConnection("消费者");
            channel = connection.createChannel();
            channel.basicConsume(queueName, true, new DeliverCallback() {
                @Override
                public void handle(String consumerTag, Delivery message) throws IOException {
                    System.out.println(new String(message.getBody(), "UTF-8"));
                }
            }, new CancelCallback() {
                @Override
                public void handle(String consumerTag) throws IOException {
                    System.out.println("消息接受失败!");
                }
            });
            System.out.println("开始接受消息!");
            System.in.read();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("出现异常...");
        } finally {
            if (channel != null && channel.isOpen()) {
                try {
                    channel.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    };
}
```

## 6. Direct

通过指定确切的routing key发送

**生产者**

```java
public class Producer {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("47.100.36.90");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("Ytc19980211..");
        Connection connection = null;
        Channel channel = null;
        try {
            connection = connectionFactory.newConnection("生产者");
            channel = connection.createChannel();
            String message = "你好，RabbitMQ！！！";
            String exchangeName = "direct-exchange";
            String routingKey = "q1";
            channel.basicPublish(exchangeName, routingKey, null, message.getBytes());
            System.out.println("消息发送成功!");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("出现异常...");
        } finally {
            if (channel != null && channel.isOpen()) {
                try {
                    channel.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
```

**消费者**

```java
public class Consumer {
    public static void main(String[] args) {
        new Thread(runnable, "queue1").start();
        new Thread(runnable, "queue2").start();
        new Thread(runnable, "queue3").start();
    }

    private static Runnable runnable = () -> {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("47.100.36.90");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("Ytc19980211..");
        final String queueName = Thread.currentThread().getName();
        Connection connection = null;
        Channel channel = null;
        try {
            connection = connectionFactory.newConnection("消费者");
            channel = connection.createChannel();
            channel.basicConsume(queueName, true, new DeliverCallback() {
                @Override
                public void handle(String consumerTag, Delivery message) throws IOException {
                    System.out.println(new String(message.getBody(), "UTF-8"));
                }
            }, new CancelCallback() {
                @Override
                public void handle(String consumerTag) throws IOException {
                    System.out.println("消息接受失败!");
                }
            });
            System.out.println("开始接受消息!");
            System.in.read();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("出现异常...");
        } finally {
            if (channel != null && channel.isOpen()) {
                try {
                    channel.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    };
}
```

## 7. Topic

通过匹配的routing key发送

`#` : 匹配0个或者多个，例如com.#，可以是com、com.aaa、com.aaa.bbb、com.aaa.bbb.ccc.ddd.eee等

`*` : 匹配1个且必须是一个，例如com.*，必须是com.aaa、com.bbb等

**生产者**

```java
public class Producer {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("47.100.36.90");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("Ytc19980211..");
        Connection connection = null;
        Channel channel = null;
        try {
            connection = connectionFactory.newConnection("生产者");
            channel = connection.createChannel();
            String message = "你好，RabbitMQ！！！";
            String exchangeName = "topic-exchange";
            String routingKey = "com.asd";
            channel.basicPublish(exchangeName, routingKey, null, message.getBytes());
            System.out.println("消息发送成功!");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("出现异常...");
        } finally {
            if (channel != null && channel.isOpen()) {
                try {
                    channel.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
```

**消费者**

```java
public class Consumer {
    public static void main(String[] args) {
        new Thread(runnable, "queue1").start();
        new Thread(runnable, "queue2").start();
        new Thread(runnable, "queue3").start();
    }

    private static Runnable runnable = () -> {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("47.100.36.90");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("Ytc19980211..");
        final String queueName = Thread.currentThread().getName();
        Connection connection = null;
        Channel channel = null;
        try {
            connection = connectionFactory.newConnection("消费者");
            channel = connection.createChannel();
            channel.basicConsume(queueName, true, new DeliverCallback() {
                @Override
                public void handle(String consumerTag, Delivery message) throws IOException {
                    System.out.println(new String(message.getBody(), "UTF-8"));
                }
            }, new CancelCallback() {
                @Override
                public void handle(String consumerTag) throws IOException {
                    System.out.println("消息接受失败!");
                }
            });
            System.out.println("开始接受消息!");
            System.in.read();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("出现异常...");
        } finally {
            if (channel != null && channel.isOpen()) {
                try {
                    channel.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    };
}
```

## 8. Headers

通过参数arg进行匹配发送

**生产者**

```java
public class Producer {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("47.100.36.90");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("Ytc19980211..");
        Connection connection = null;
        Channel channel = null;
        try {
            connection = connectionFactory.newConnection("生产者");
            channel = connection.createChannel();
            String message = "你好，RabbitMQ！！！";
            String exchangeName = "headers-exchange";
            String routingKey = "";
            Map<String, Object> map = new HashMap<>();
            map.put("x", "1");
            AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
            builder.headers(map);
            channel.basicPublish(exchangeName, routingKey, builder.build(), message.getBytes());
            System.out.println("消息发送成功!");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("出现异常...");
        } finally {
            if (channel != null && channel.isOpen()) {
                try {
                    channel.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
```

**消费者**

```java
public class Consumer {
    public static void main(String[] args) {
        new Thread(runnable, "queue1").start();
        new Thread(runnable, "queue2").start();
        new Thread(runnable, "queue3").start();
    }

    private static Runnable runnable = () -> {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("47.100.36.90");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("Ytc19980211..");
        final String queueName = Thread.currentThread().getName();
        Connection connection = null;
        Channel channel = null;
        try {
            connection = connectionFactory.newConnection("消费者");
            channel = connection.createChannel();
            channel.basicConsume(queueName, true, new DeliverCallback() {
                @Override
                public void handle(String consumerTag, Delivery message) throws IOException {
                    System.out.println(new String(message.getBody(), "UTF-8"));
                }
            }, new CancelCallback() {
                @Override
                public void handle(String consumerTag) throws IOException {
                    System.out.println("消息接受失败!");
                }
            });
            System.out.println("开始接受消息!");
            System.in.read();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("出现异常...");
        } finally {
            if (channel != null && channel.isOpen()) {
                try {
                    channel.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    };
}
```

## 9. 声明创建

```java
//声明交换机 参数1:交换机名称 参数2:交换机类型 参数3:是否持久化
channel.exchangeDeclare(exchangeName,type,true);

//声明队列 参数1: 队列名 参数2:是否持久化 参数3:是否具有排他性  参数4:是否自动删除  参数5:附加参数 headers模式中根据参数绑定队列
channel.queueDeclare(queueName,true,false,false,null);

//绑定队列 参数1:队列名 参数2:交换机名 参数3:路由Key direct中是需要绑定路由Key的
channel.queueBind(queueName,exchangeName,"com.#");
```

## 10. Work

**轮询模式**

一个消费者—条，按均分配

1.默认情况下就是轮询模式

2.多个消费者消费同一个队列

3.消费者消费消息需设置成自动应答

![在这里插入图片描述](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NjY4NDA5OQ==,size_16,color_FFFFFF,t_70.png)

**公平分发**

根据消费者的消费能力进行公平分发，处理快的处理的多，处理慢的处理的少，按劳分配

1.消费者需要设置手动应答false

2.设置Qos及每次取出多少条数据处理

![sfdgffdfgrfdggf](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/sfdgffdfgrfdggf.png)

扩展Qos的设置

1.Qos参数值究竟设置多大核实需要根据当前电脑内存,磁盘空间和处理消息数量进行综合考虑

2.通过图型化界面进行参考,如设置1000,运行看内存和磁盘会不会爆掉,如果能运行很久则说明值是合理的

3.一般建议值不用取的太大

![asdfafffvdgebgfghfgdgffrfdgf](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/asdfafffvdgebgfghfgdgffrfdgf.png)

## 11. Springboot整合

**生产者**

创建项目勾选web和rabbitmq依赖或者手动导入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

配置

```yaml
server:
  port: 8080
spring:
  rabbitmq:
    username: root # 用户名
    password: Ytc19980211.. # 密码
    virtual-host: /  # 设置虚拟访问节点
    host: 47.100.36.90 # 设置 服务器地址
    port: 5672 # 端口号
```

编写service

```java
@Service
public class OrderService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    // 1: 定义交换机
    private String exchangeName = "fanout_order_exchange";
    // 2: 路由key
    private String routeKey = "";
    public void makeOrder(Long userId, Long productId, int num) {
        // 1： 模拟用户下单
        String orderNumer = UUID.randomUUID().toString();
        // 2: 根据商品id productId 去查询商品的库存
        // int numstore = productSerivce.getProductNum(productId);
        // 3:判断库存是否充足
        // if(num >  numstore ){ return  "商品库存不足..."; }
        // 4: 下单逻辑
        // orderService.saveOrder(order);
        // 5: 下单成功要扣减库存
        // 6: 下单完成以后
        System.out.println("用户 " + userId + ",订单编号是：" + orderNumer);
        // 发送订单信息给RabbitMQ fanout
        rabbitTemplate.convertAndSend(exchangeName, routeKey, orderNumer);
    }
}
```

编写RabbitmqConfig，这个类可以在两边都放，队列和绑定最好放在消费者那边

```java
@Configuration
public class RabbitmqConfig {
    //队列 起名：TestDirectQueue
    @Bean
    public Queue emailQueue() {
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，当没有生产者或者消费者使用此队列，该队列会自动删除。
        //   return new Queue("TestDirectQueue",true,true,false);
        //一般设置一下队列的持久化就好,其余两个就是默认false
        return new Queue("email.fanout.queue", true);
    }
    @Bean
    public Queue smsQueue() {
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，当没有生产者或者消费者使用此队列，该队列会自动删除。
        //   return new Queue("TestDirectQueue",true,true,false);
        //一般设置一下队列的持久化就好,其余两个就是默认false
        return new Queue("sms.fanout.queue", true);
    }
    @Bean
    public Queue weixinQueue() {
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，当没有生产者或者消费者使用此队列，该队列会自动删除。
        //   return new Queue("TestDirectQueue",true,true,false);
        //一般设置一下队列的持久化就好,其余两个就是默认false
        return new Queue("weixin.fanout.queue", true);
    }
    //Direct交换机 起名：TestDirectExchange
    @Bean
    public DirectExchange fanoutOrderExchange() {
        //  return new DirectExchange("TestDirectExchange",true,true);
        return new DirectExchange("fanout_order_exchange", true, false);
    }
    //绑定  将队列和交换机绑定, 并设置用于匹配键：TestDirectRouting
    @Bean
    public Binding bindingDirect1() {
        return BindingBuilder.bind(weixinQueue()).to(fanoutOrderExchange()).with("");
    }
    @Bean
    public Binding bindingDirect2() {
        return BindingBuilder.bind(smsQueue()).to(fanoutOrderExchange()).with("");
    }
    @Bean
    public Binding bindingDirect3() {
        return BindingBuilder.bind(emailQueue()).to(fanoutOrderExchange()).with("");
    }
}
```

测试

```java
@SpringBootTest
class ProducerApplicationTests {
    @Autowired
    OrderService orderService;

    @Test
    public void contextLoads() throws Exception {
        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000);
            Long userId = 100L + i;
            Long productId = 10001L + i;
            int num = 10;
            orderService.makeOrder(userId, productId, num);
        }
    }
}
```

**消费者**

创建项目勾选web和rabbitmq依赖或者手动导入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

配置

```yaml
server:
  port: 8081
spring:
  rabbitmq:
    username: root # 用户名
    password: Ytc19980211.. # 密码
    virtual-host: /  # 设置虚拟访问节点
    host: 47.100.36.90 # 设置 服务器地址
    port: 5672 # 端口号
```

编写service，添加@RabbitListener和@RabbitHandler注解

```java
@Service
@RabbitListener(queues = "email.fanout.queue")
public class EmailService {
    @RabbitHandler
    public void reviceMessage(String message){
        System.out.println("RabbitMQ消费消息,sms:"+message);
    }
}
```

也可以在这边用注解绑定

```java
@RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = "sms.topic.queue",durable = "true",autoDelete = "false"),
        exchange = @Exchange(value = "topic-order-exchage",type = ExchangeTypes.TOPIC),
        key = "#.sms.#"))
```

启动项目收取消息

## 12. 设置TTL

**队列TTL**

在声明队列时

```java
@Bean
    public Queue smsQueue(){
        //TTL 设置过期时间
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("x-message-ttl",5000);//这里一定是int类型
        //参数1: 队列名 参数2:是否持久化 参数3:排他性 参数4:自动删除  参数5:附加参数
        return new Queue("sms.direct.queue",true,false,false,hashMap);
    }
```

**消息TTL**

在生产者编写service方法中

```java
//模拟用户下单
public void makeOrder(String userId,String productid,int num){
    // 根据商品id查询是否充足
    //保存订单
    String orderId = UUID.randomUUID().toString();
    System.out.println("订单生成成功");
    //通过MQ完成消息分发

    //准备交换机
    String fanoutExchange = "fanout-order-exchange";

    //准备路由key
    String routingKey = "";

    //给消息设置过期时间
    MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
        @Override
        public Message postProcessMessage(Message message) throws AmqpException {
            message.getMessageProperties().setExpiration("5000");//这里是一个字符串
            message.getMessageProperties().setContentEncoding("UTF-8");
            return message;
        }
    };

    //参数1:交换机 参数2:路由Key/队列名 参数3:消息内容
    rabbitTemplate.convertAndSend(fanoutExchange,routingKey,orderId,messagePostProcessor);
}
```

**死信队列**

设置一个队列

```java
@Configuration
public class DeadExchangeConfig {
    @Bean
    public DirectExchange dead(){
        return new DirectExchange("dead-order-exchange",true,false);
    }

    //声明队列
    @Bean
    public Queue deadQueue(){
        //参数1: 队列名 参数2:是否持久化 参数3:排他性 参数4:自动删除  参数5:附加参数
        return new Queue("sms.dead.queue",true);
    }
    
    //完成绑定关系
    @Bean
    public Binding smsBinding(){
        // 参数1:队列 参数2:交换机
        return BindingBuilder.bind(deadQueue()).to(dead()).with("sms");
    }
}
```

在TTL队列中绑定死信

```java
@Configuration
public class RabbitmqConfig {
    //声明组成fanout交换机
    @Bean
    public DirectExchange directExchange(){
        //参数1:队列名 参数2:是否持久化 参数3:是否自动删除
        return new DirectExchange("ttl-order-exchange",true,false);
    }

    //声明队列
    @Bean
    public Queue smsQueue(){
        //TTL 设置过期时间
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("x-message-ttl",5000);//这里一定是int类型  5000=5秒
        //配置死信队列
        hashMap.put("x-dead-letter-exchange","dead-order-exchange");//死信队列名
        hashMap.put("x-dead-letter-routing-key","sms");//fanout模式不需要配置路由Key
        //参数1: 队列名 参数2:是否持久化 参数3:排他性 参数4:自动删除  参数5:附加参数
        return new Queue("sms.ttl.queue",true,false,false,hashMap);
    }
    //完成绑定关系
    @Bean
    public Binding smsBinding(){
        // 参数1:队列 参数2:交换机
        return BindingBuilder.bind(smsQueue()).to(directExchange()).with("sms");
    }
}
```

## 13. 内存分配

命令

```bash
# 内存
rabbitmqctl set_vm_memory_high_watermark 0.7
rabbitmqctl set_vm_memory_high_watermark absolute 2GB

# 磁盘
rabbitmqctl set_disk_free_limit  <disk_limit>
rabbitmqctl set_disk_free_limit memory_limit  <fraction>
disk_limit：固定单位 KB MB GB
fraction ：是相对阈值，建议范围在:1.0~2.0之间。（相对于内存）
```

配置文件`/etc/rabbitmq/rabbitmq.conf`

```bash
#默认
#vm_memory_high_watermark.relative = 0.4
# 使用relative相对值进行设置fraction,建议取值在04~0.7之间，不建议超过0.7.
vm_memory_high_watermark.relative = 0.5
# 使用absolute的绝对值的方式，但是是KB,MB,GB对应的命令如下
vm_memory_high_watermark.absolute = 2GB

# 内存换页 磁盘换内存的比例
vm_memory_high_watermark_paging_ratio = 0.7

# 磁盘预警 默认50mb
disk_free_limit.relative = 3.0
disk_free_limit.absolute = 50mb
```

## 14. 集群

在不同服务器上启动

```bash
RABBITMQ_NODE_PORT=5672 RABBITMQ_NODENAME=rabbit-1 rabbitmq-server start &
RABBITMQ_NODE_PORT=5672 RABBITMQ_NODENAME=rabbit-2 rabbitmq-server start &
```

rabbit-1操作作为主节点

```bash
#停止应用
rabbitmqctl -n rabbit-1 stop_app
#目的是清除节点上的历史数据（如果不清除，无法将节点加入到集群）
rabbitmqctl -n rabbit-1 reset
#启动应用
rabbitmqctl -n rabbit-1 start_app
```

rabbit2操作为从节点

```bash
# 停止应用
rabbitmqctl -n rabbit-2 stop_app
# 目的是清除节点上的历史数据（如果不清除，无法将节点加入到集群）
rabbitmqctl -n rabbit-2 reset
# 将rabbit2节点加入到rabbit1（主节点）集群当中【Server-node服务器的主机名】
rabbitmqctl -n rabbit-2 join_cluster rabbit-1@'Server-node'
# 启动应用
> sudo rabbitmqctl -n rabbit-2 start_app
```

验证集群状态

```bash
rabbitmqctl cluster_status -n rabbit-1

//集群有两个节点：rabbit-1@Server-node、rabbit-2@Server-node
[{nodes,[{disc,['rabbit-1@Server-node','rabbit-2@Server-node']}]},
 {running_nodes,['rabbit-2@Server-node','rabbit-1@Server-node']},
 {cluster_name,<<"rabbit-1@Server-node.localdomain">>},
 {partitions,[]},
 {alarms,[{'rabbit-2@Server-node',[]},{'rabbit-1@Server-node',[]}]}]
```

Web监控

![img](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/kuangstudy38859ae4-0723-45b7-ac1f-32095b2e28da.png)

> 注意在访问的时候：web结面的管理需要给所有的rabbitmq设置用户名和密码。如下:

```bash
rabbitmqctl -n rabbit-1 add_user admin admin
rabbitmqctl -n rabbit-1 set_user_tags admin administrator
rabbitmqctl -n rabbit-1 set_permissions -p / admin ".*" ".*" ".*"
rabbitmqctl -n rabbit-2 add_user admin admin
rabbitmqctl -n rabbit-2 set_user_tags admin administrator
rabbitmqctl -n rabbit-2 set_permissions -p / admin ".*" ".*" ".*"
```

在Springboot中的配置

```yaml
 rabbitmq:
    addresses: 127.0.0.1:6605,127.0.0.1:6606,127.0.0.1:6705 #指定client连接到的server的地址，多个以逗号分隔(优先取addresses，然后再取host)
#    port:
    ##集群配置 addresses之间用逗号隔开
    # addresses: ip:port,ip:port
    password: admin
    username: 123456
    virtual-host: / # 连接到rabbitMQ的vhost
    requested-heartbeat: #指定心跳超时，单位秒，0为不指定；默认60s
    publisher-confirms: #是否启用 发布确认
    publisher-reurns: # 是否启用发布返回
    connection-timeout: #连接超时，单位毫秒，0表示无穷大，不超时
    cache:
      channel.size: # 缓存中保持的channel数量
      channel.checkout-timeout: # 当缓存数量被设置时，从缓存中获取一个channel的超时时间，单位毫秒；如果为0，则总是创建一个新channel
      connection.size: # 缓存的连接数，只有是CONNECTION模式时生效
      connection.mode: # 连接工厂缓存模式：CHANNEL 和 CONNECTION
    listener:
      simple.auto-startup: # 是否启动时自动启动容器
      simple.acknowledge-mode: # 表示消息确认方式，其有三种配置方式，分别是none、manual和auto；默认auto
      simple.concurrency: # 最小的消费者数量
      simple.max-concurrency: # 最大的消费者数量
      simple.prefetch: # 指定一个请求能处理多少个消息，如果有事务的话，必须大于等于transaction数量.
      simple.transaction-size: # 指定一个事务处理的消息数量，最好是小于等于prefetch的数量.
      simple.default-requeue-rejected: # 决定被拒绝的消息是否重新入队；默认是true（与参数acknowledge-mode有关系）
      simple.idle-event-interval: # 多少长时间发布空闲容器时间，单位毫秒
      simple.retry.enabled: # 监听重试是否可用
      simple.retry.max-attempts: # 最大重试次数
      simple.retry.initial-interval: # 第一次和第二次尝试发布或传递消息之间的间隔
      simple.retry.multiplier: # 应用于上一重试间隔的乘数
      simple.retry.max-interval: # 最大重试时间间隔
      simple.retry.stateless: # 重试是有状态or无状态
    template:
      mandatory: # 启用强制信息；默认false
      receive-timeout: # receive() 操作的超时时间
      reply-timeout: # sendAndReceive() 操作的超时时间
      retry.enabled: # 发送重试是否可用
      retry.max-attempts: # 最大重试次数
      retry.initial-interval: # 第一次和第二次尝试发布或传递消息之间的间隔
      retry.multiplier: # 应用于上一重试间隔的乘数
      retry.max-interval: #最大重试时间间隔
```

![img](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/kuangstudy1433dcb6-80f9-4592-a57e-2cbb15863fb8.png)

![img](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/kuangstudyba7682ed-fc98-4948-b12e-229c5381d3c0.png)
