# ** AWS Certified Associate Developer **

# section4: IAM&AWS CLI (0-36)

![1](C:\Users\10602\Desktop\java学习\amazon_certificate\AWS Associate Developer\imgs\IAM\1.png)

![2](C:\Users\10602\Desktop\java学习\amazon_certificate\AWS Associate Developer\imgs\IAM\2.png)

![3](C:\Users\10602\Desktop\java学习\amazon_certificate\AWS Associate Developer\imgs\IAM\3.png)

# section5: EC2 Basics (36-69)

```bash
# bootstrapping 是在创建ec2的时候初始化ec2里所需的内容

# Compute Optimized适用于高性能，低延迟服务器等
# Memory Optimized适用于高性能数据库，分布式缓存等
# Storage Optimized 适用于高频在线事务处理，IO流读取写入等

# Secure Group的inbound traffic是默认blocked，outbound traffic是默认authorised的

# 可以添加其他安全组到安全组里，这样使用那个安全组的ec2也会被放行

# 3年计划永远是最省钱的，除了spot，Reserved最便宜其次saving plan
```

# section6: EC2 Instance Storage (69-92)

84页的好好记住区别

```bash
# EBS是network drive，存的一般不会再ec2终止的时候消失，除非选了，然后一般root volume会选这个。一般同时只能绑定1个instance，是指定AZ的

# Snapshots是备份EBS然后可以实现跨区复制。archive tier便宜75%，24 to 72 hours去restore从archive

#EC2 Instance Store是高性能hard disk，物理连接的。终止了就会丢失里面所有东西

# EBS的gp2/3是普通的那俩，1GB - 16TB，gp3是默认3000 IOPS和 125MB/s，最高提升到16000 IOPS和1000 MB/s; gp2的储存大小和IOPS挂钩，3 IOS/GB，16000IOS 最高

# io1/2是最高性能的，4GB - 16TB,  Nitro EC2最高64,000 PIOPS，普通最高32,000; PIOPS和储存大小不绑定。io2比1好。io2 Block Express是 4GB - 64TB，最大256,000 PIOPS, 再高就得用instance storage了，（IOPS ： GB = 1000 : 1 ），io1/2支持EBS多重挂载； io2的durability最高。

# HDD不能当boot disk，125GB - 16TB，st1是大数据，数据仓库用的，500 MB/s – max IOPS 500；sc1是对于 infrequently accessed的，速度和IOPS减半

# io1/2多重挂载同AZ挂载，最多挂载16个instance

# EFS是跨AZ挂载的，支持1000并发，10GB+/s流量，Bursting(1tb = 50mb/s,最高100mb/s), Provisioned(1GB/s for 1 TB), Elastic(最高3GB的read和 1GB的写)

# EFS有standard和(EFS-IA)两种，在EFS-IA上retrieve file的钱更少。可以选multi-AZ或者One Zone，默认启用backup，可使用One Zone-IA

# EFS比EBS贵
```



# section7: ELB+ASG (93-135)

```bash
# Scalability是指可以纵向或者横向拓展，其中横向又叫做elasticity，availability是指高稳定性和容错性高

# secure groups可以放其他的安全组，通过设置source为ALB的secure group可以只允许通过ALB访问。其次ALB的listener可以设置condition，规定什么样的请求发送到哪个target group里

# load balancer也会运行health check然后和ASG配合用健康的替换掉不健康的ec2

# ALB是layer7 http/https的，有固定的hostname，看不到用户IP，真实IP在X-Forwarded-For里

# NetworkLB是layer4 TCP/TLS/UDP的，在每个AZ里会有一个静态IP的，高性能的TCP或者UDP traffic用NLB，不免费

# Gateway LB是layer3的，用于流量在到达应用程序前先经过其他实例(检查)，在6081端口使用GENEVE protocol

# Sticky Sessions实现粘性会话，使得相同的客户端始终被重定向到负载平衡器后面的同一个实例。适用于所有LB，用cookie来实现保证。对于经典和ALB，cookies有过期日期可控

# ALB默认开启跨AZ，经典和ALB的AZ内数据不收费

# SSL证书允许您的客户端与负载平衡器之间的流量在传输过程中进行加密，TLS指的是传输层安全性Transport Layer Security，是一种更新的版本。如今，主要使用TLS证书，但人们仍然称其为SSL。公共SSL证书由证书颁发机构（CA）颁发。SSL证书具有过期日期（由您设置），必须定期更新。

# SNI是解决把多个SSL 证书加载到1个web server里的。他会在最开始SSL握手的时候让client指明target server中的hostname，这样的话server能找到正确的证书。只用于ALB&NLB和cloudFront

# CLB只能支持一个SSL，ALB和NLB 能支持多个listeners和多个SSL(使用SNI)

# Connection Draining在实例被注销或者标记为unhealthy时，将给实例一些时间（默认300s，1-3600s选择）来完成正在进行的请求或活动的请求，就是先把活儿干完再关闭

# scale out是increase的，scale in是decrease的. Auto Scaling Group是免费的。ELB查看健康状况并传送信息给ASG

# predicative scaling，就是根据过去数据用量预测什么时候需要拓展并且提前计划拓展

# cooldown期间ASG是不会添加或者减少instance的，默认是300秒

# instance refresh是更新instance用的，通过关闭instance再创建新的instance来逐步达成更新目的。会设置一个最少需要保留多少healthy instance的比例

# ELB有静态DNS的，不能用下面的当名字 AWSALB, AWSALBAPP, AWSALBTG
```

# section8: RDS+Aurora+ElastiCache (136-161)

```bash
# RDS可以用Postgre，Mysql，MariaDB，Oracle，Microsoft SQL Server, 是可以自动拓展的，不能SSH进入instance

# RDS和Aurora最多有15个Read Replicas, 可以是同AZ, 跨AZ和跨Region。异步复制，只读，并且可以把Read Replicas提升为正常DB，异步导致信息同步的没那么快。只有跨region的Read replicas需要花钱

# 对于灾难恢复的备用RDS来说，是同步的

# Aurora是severless的，支持mysql和postgre，可以最多有15个复制副本，是high available的，比RDS贵但比传统RDS速度快很多，10GB - 128TB

# Aurora会在写任何东西的时候通过3个AZ储存6份副本。但只有1个有write功能，剩下都是read，master不工作的话30秒就会启动故障转移。最多有15个复制副本，默认1个master。储存将被复制，自我修复和自动拓展

# Aurora的Read replicas是自动拓展的。writer endpoint用来往master里写入数据。reader endpoint和负载均衡连接，自动连接所有Read replicas，所以每当客户端连接到reader endpoint时他都会连接到其中一个Read replica，并且完成负载均衡功能，负载均衡发生在connection level

# 静态加密用AWS KMS，需加密master才能加密副本。加密未加密的DB需要备份然后备份恢复时选择加密。传输加密，必须用AWS TSL root certificate client side; 可以用IAM连接数据库。安全组控制数据访问，没SSH 权限，除非用RDS自定义服务，可以添加audit logs并且可以发送到cloudWatch里

# RDS Proxy是完全托管的数据库代理，适用于 RDS。应用连 proxy 连RDS DB。提高efficiency，减少cpu和ram的压力以及最小化连接数和超时。是serverless的，自动扩展，多AZ， 减少RDS&Aurora的故障转移时间。支持mysql，postgre，Maria DB，MS sql server和Aurora。强制执行IAM验证，credentials存在secret manager里.只能从VPC里访问

# ElastiCache用来托管redis或者Memcached的。可以储存session，切换instance不需要再次登录。redis是复制，Memcached是共享节点。redis是高可用性的， 但是Memcached不行，所以得把那些能承受损失的东西放进去

# Lazy Loading 数据存在就从缓存里读，不存在从DB里找然后放到缓存里。没缓存的可能会有延迟因为对EC进行了3次trip（miss, read, write）而且数据在RDS里更新了不一定会在缓存里更新，得有人调用才会查看

# write Through：数据库更新的时候就会一并更新缓存里的，write penalty是call 2次(update, write)比上边的少一次，可能会有缺失data的问题。

# 缓存可以设置TTL

# menoryDB for redis是一个拥有redis兼容的API数据库，特别快的性能，有多AZ的事务日志的持久数据存储，无缝扩展，可用于网络或者应用程序，网络游戏和媒体数据流，多用于microservice的

# ElastiCache Redis Cluster with Cluster-Mode Disabled中最多可添加5个只读副本
```

# section9: Route 53 (162-193)

```bash
# DNS 是Domain Name System，将网址转换成ip地址，比如www.google.com转换成正经ip地址。地址的问询机制是从root开始问，然后TLD然后SLD

# Route53是一个域名管理的，也可以注册域名。route53支持A/AAAA/CNAME/NS 这些DNS record types

# record type中A代表ipv4，AAAA代表ipv6，CNAME将几个主机名指向一个别名，其实跟指向IP地址是一样的，因为这个别名也要做一个A记录的。NS控制流量转发到域名的

# Hosted Zones是定义如何将流量转发到域名和子域名的，每个host zone每个月收费0.5刀。public HZ是互联网的，Private是VPC的

# High TTL和Low TTL的区别是在cache存储的时间不同（比如说60s和24hrs），但是如果在TTL到期前更改record里的IP，变化会在TTL到期之后发生

# Alias可以特意用于aws服务的，CNAME不能用于root name的URL但alias可以，还能自动识别适配ip更改

# routing policy里面，simple是随机返回一个，weight是按比重返回，latency-based是根据到aws的延迟

# http健康检查只对public开放，monitor an endpoint中会有15个global的health checkers. 可设置健康比率。可以通过识别first 5120 bytes就来定义 健康检查是fail还是pass。calculated health checkers可以检测最多256个child。

# 对private vpc来说得用 CloudWatch Metric和CloudWatch Alarm来检测health check

# Geolocation根据地理位置来的，而不是延迟

# Geoproximity通过bias来一定程度改变用户所应该使用的region，将更多的流量转移到特定的区域

# traffic flow进行可视化的控制dns分流方式的

# Multi-Value可以routing traffic到多个ip从1个domain name，可以拥有8个health check
```

# section10: VPC (194-211)

```bash
# VPC是regional范围的，subnets是AZs范围内的，公共子网可以通过internet访问/访问internet，private的不允许通过internet访问

# Internet Gateways允许公共子网访问internet，NAT Gateway允许private子网通过在public subnet里的NAT Gateway访问internet

# NACL是子网层面的防火墙（控制进出子网），可以allow或者deny rules，是stateless的, Return traffic must be explicitly allowed by rules(返回流量必须得到规则明确允许)

# 安全组是控制进出instance流量的，只允许allow rules， 是Stateful的，Return traffic is auto allowed , regardless of any rules.

# VPC flog logs是获取所有进出流量的log的东西，vpc log的data可以发送去S3, CloudWatch logs， Kinesis Data Firehose

# VPC peering是通过aws网络连接不同vpc的，每两个vpc都需要连接

# Endpoint是允许VPC通过私有网络连AWS服务的，VPC Endpoint Gateway只服务S3和DynamoDB, VPC Endpoint Interface服务于其他的aws服务

# Site to Site VPN 是用来连接on-premises data center到AWS的，通过public互联网加密连

# Direct Connect(DX)是用来建物理连接on-premises data center到AWS的
```

# section11: Amazon S3(212-236) 

```bash
# S3的bucket名字必须全局唯一，不能以xn--开头 -s3alias 结尾。Objects key是完整路径。bucket是regional level的，可以放metadata，就是key/value键值对

# 可以用IAM Policies，Bucket Policies，Object/Bucket Access Control List(ACL)来做权限控制

# explicit DENY是再bucket policy级别之上的。ec2用IAM，cross account用bucket policy。

# S3复制是异步的，可以同region或者跨region，必须开启version

# s3 standard用于经常访问的

# s3 standard-IA用于备份，灾难恢复

# s3 oneZone-IA有高耐用，用于数据丢了也可以轻易恢复的

# s3 glacier instant retrieval用于快速retrieval的，最少90天

# s3 glacier flexible retrieval(expedited 1-5 mins,  standard 3-5 mins,  bulk 5-12 mins)最少90天

# s3 glacier deep archive（standard 12h，bulk 48h）最少180天
```

```bash
# S3 Intelligent-Tiering:

# 1. freq access tier: 默认的

# 2. infreq access tier：30天没有使用的

# 3. archive Instant access tier：90天没有使用的

# 4. archive access tier：90-700+天没有使用的

# 5. deep archive access tier：180-700+天没有使用的
```

# section12: Developing on AWS(237-249)

```bash
# Metadata = 获取ec2信息的，但不能获取policy具体内容什么的, Userdata = launch script of the EC2 instance, 用profile来管理多个aws账户

# 对CLI用MFA得用 STS GetSessionToken API 

# 连续调用API的限制次数。EC2 describe Instances是100个call每秒，可以提升。GetObjects: s3有5500get/s限制。

# ThrottlingException意思是流量太高了，可以用指数的recall方法。1 2 4 8分钟recall

# AWS CLI 是基于python的，

# AWS CLI Credentials Provider Chain会根据优先级赋予权限，command line -> Environment variables -> CLI credentials -> CLI configuration file -> Container credentials –> Instance profile

# AWS SDK是这个顺序 java system -> Environment variables -> The default credential -> Amazon ECS -> Instance profile

# call the AWS HTTP API的时候需要用SigV4去sign一下
```

# section13: Advanced Amazon S3(250-263)

```bash
# Storage Class Analysis可以用于推荐什么时候需要把object转移到正确的storage class

# Transition Actions是用于在不同储存class中移动，Expiration actions是删除过期的老旧文件的

# s3上传是多文件平行上传，transfer acceleration是先把文件传到edge location再用aws专用网络传到s3，必须用于大于5G的文件

# Byte-Range Fetches是同时并行下载文件部分内容加快速度

# S3 Select是可以选择性用sql下载部分内容的
```

# section14: Amazon S3 Security(263-284)

```bash
# 可以用server side加密， 也可以用client side加密。server side custom必须用HTTPS。client side 的情况下不需要server有任何解码功能。

# S3服务器端用AES-256加密，默认开启。

# 用SSE-KMS的话需要在header里开启，但用S3用KMS可以减少对KMS api的调用

# Force Encryption in Transit需要在condition中使用aws:SecureTransport

# bucket policy在默认加密策略之前

# CORs是用于当不同的file存在不同的s3中的时候，假如说你访问的网页需要同时向2个s3获取文件，那么CORs是用于向其他s3 get file用的

# Pre-Signed URLs可以给其他人一些下载上传等的权限，比如百度云会员那个加速服务，CSDN会员的那个允许复制，onlyfan的付费观看

# 指定Access Point可以指定user能访问哪个root下的内容
```

# section15: CloudFront(285-313)

```bash
# 通过在不同的edge缓存静态内容来提升性能。是CDN，能防止ddos。216个站点，enhanced security是OAC origin access control

# cache存在edge location里

#  CloudFront Invalidation可以让缓存失效，用来更新存在edge location里的缓存的内容

# 通过不同路径可以route到不同的origins里，比如/api/* 就去load balance，/*去S3。default cache behaviors （/*）永远是最后被执行的那个。可以用来做登录限制

# Geo Restriction可以用allow list和block list来限制国家内的登录，跟墙一样

# Signed URL / Signed Cookies去指定什么人可以访问，就onlyfan付费观看那个一样。url是对单用户的，cookies是对多用户的。用trusted key groups

# origin group是用来高availablity和failover的，一个挂了还可以用另一个，1个primary的和1个secondary的origin。可以用于ec2和s3

# field level encryption，举例来说信用卡加密，是指从client到edge location这段用进行field加密，然后edge location到cloudfront用公钥对信用卡加密，然后到最后的web servers(ec2)那块再用private key解密获取信息
```

# section16: ECS, ECR & Fargate - Docker in AWS(314-355)

```bash
# Fargate是serverless的。Fargate + EFS = Serverless

# IAM role task role赋予了一个task definition(json file)之后这个task创建的service都有相应的权力，比如调用s3或者其他啥的

# ECS Service Auto Scaling是扩展task的，Auto Scaling Group Scaling/ECS Cluster Capacity Provider是拓展ec2的，ECS Cluster Capacity Provider更推荐使用

# ECS的Dynamic Host Port Mapping因为container的port是不确定的，所以用ALB得允许ALB访问所有port，这是针对EC2的

# fargate的话因为container的port是固定的，新增container的话会分配新的IP，所以不需要访问全部port

# 每个task 需要有自己的task role

# binpack将根据用量往cpu剩余最少的放，减少instance使用数量的； random是随即放的； spread是根据指定值均匀放置，可以混合使用这三个

# distinctInstance是每个任务都应该放到不同的container instance上，一个instance就1个任务。memberOf 将任务放在满足表达式的instnace上，比如都放t2上

# ECR是放docker image的
```

# section17: Elastic Beanstalk(355-381)

```bash
# Beanstalk会自动拓展ec2，自动alb，sg啥的但仍能访问这些配置, 用Application Version控制版本

# Immutable是创三个新的拿新的替换然后删掉旧的

# Blue / Green是同时又蓝绿， 然后流量同时分给两个，新版本测试的没问题了就全分给新的版本。

# Canary Testing是发送少量流量到新版本的新ASG上测试，成功的话就流量全转过去

# 新版本以.zip上传并deploy

# 如果想更换load balancer的话就需要migrate环境，创建一个完全一样的配置，除了load balancer，然后将程序部署到新环境里，然后用cname swap或者rout53 更新去变更流量
```

# section18:  CloudFormation(382-425)

```bash
# 使用cloudFormation的话需要先上传到S3。Beanstalk和SAM 依赖CloudFormation来配置资源

#  Fn::Ref / !Ref 是只想parameters的

# mapping和!FindInMap一起用，!FindInMap [ MapName, TopLevelKey, SecondLevelKey ]

# Fn::ImportValue用于引用外部参考

# Fn::join 是把variable join到一起，sub是拼接字符串

# stack Creation Fails的时候需要手动删除那个，再新建一个stack

# 如果cloudFormation的文件被手动修改了，Drift会提示
```

# section19: AWS Integration & Messaging(426-489)

```bash
# SQS Message retention保留的时间:默认4天，最高14天。传输加密HTTPS，静态KMS，还有client side

# SQS 单信息最大256kb

# Message Visibility Timeout是指这消息被人拿走以后多久才能再被看到如果没有被处理

# SQS FIFO有300 msg/s without batching, 3000 msg/s with的限制

# DLQ就是如果消息读了好几次都没被处理删除，就会进入DLQ，debug用的，bug改完之后DLQ的消息还能回到正常queue里. FIFO sqs的DLQ必须也是FIFO

# long poll就是当队列里没信息的话，可以等待信息，减少API的调用次数

# Extended client是发大型message用的，原理是把message发到s3去，然后从s3 retrieve信息

# PurgeQueue是delete所有message用的

# SQS FIFO – Deduplication 是5分钟，5分钟内发送相同信息的话会被拒绝；MessageGroupID会指定message发到哪个group然后都谁能看到

# SNS可以实现信息过滤，订阅者可以设置filter过滤接收信息

# SNS + SQS = FAN OUT;SNS – FIFO只能选SQS FIFO当信息订阅者

# Kinesis Data Streams: 捕获、处理和存储数据流, 由很多shared组成; 保留的时间 1- 365天； 可以replay data；data插入之后就没法删除了；Consumers 有 Lambda，Analytics 和 Firehose

# Kinesis Client Library (KCL)中一个shard最多被一个instance读取

# 如果一个shard太hot就需要split，一个拆成俩，老的删除；1个shard最多拆成俩；还可以2个合并成1个，最多2合1；

# 可以用Partition Key来管理data到底进入到哪个shard

# Kinesis Data Firehose：将数据流加载到 AWS 数据存储中，是near real-time的
```

# section20: AWS Monitoring, Troubleshooting & Audit (490-547)

```bash
# CloudWatch Metrics中Metrics属于namespaces，每个metrics最多有30个attributes。正常ec2的 metrics是5分钟1次，detailed monitoring是1分钟1次

# putMetricData可以custom metrics，比如看ram usage。Accepts data从去2周到未来2小时

# Log groups代表application，Log stream代表instance with application，log可以被发到s3，lambda，Kinesis Data Streams/Firehose

# cloudwatch logs insight是query engine，所以他只查询历史数据；

# s3 export发到s3最多得等12小时才available，API是createExportTask，或者用subscriptions filter

# Aggregation是做multi account或者multi region的，发到Kinesis Data Streams -> Kinesis Data Firehose之后再发到s3

# ec2的log需要用cloudWatch agent才能推送上去

# EventBridge可以 replay archived events

# X-Ray是可视化分析问题的，可以看出哪一步有问题，溯源。 Lambda，Beanstalk,ECS, ELB, API gateway, EC2都行 

# 用X-Ray的话code里必须有AWS X-Ray SDK或者安装X-Ray daemon（ec2需要）/开启X-Ray AWS Integration

# cloud trail是记录所有api调用记录的，是用来audit for aws account的

# Cloudtrail Insight是用来检测不寻常的activity的，event正常会在Cloudtrail 里存90天，之后use Athena然后存S3
```

# section21: AWS Lambda(548-623)

```bash
# 同步Invocations：ALB, API Gateway, CloudFront, S3, Cognito, Step Functions

# ALB to Lambda: HTTP to JSON,反过来是JSON to HTTP

# 异步Invocations：S3, SNS, CloudWatch Events

# Event Source Mapping: Kinesis Data Streams, SQS & SQS FIFO queue, DynamoDB Streams

# 有error的话会重复尝试，直到batch过期，或者手动丢弃，丢弃到Destination。destination的作用是将异步调用或者source mapping的结果发到某个地方，success的和fail的发到不同的destination，很像DLQ

# SQS & SQS FIFO的话会用long pull，batch size是1-10，有DLQ

# 可以用lambda平行处理shard里的东西，一个shard可以有最多10个batches processor

# 用event source mapping去invoke function时用的是lambda execution role

# cloudFront有2个type，CloudFrontfunctions & Lambda@Edge； CloudFrontfunctions特别快速度，但不能看origin view

# 默认来说lambda在aws的vpc launched的，所以没法访问自己vpc的东西，需要进行配置来让lambda访问自己的vpc，vpc里的lambda需要ENI role 的权限。默认下vpc内的lambda不能访问互联网，只有NAT gateway/instance才能让lambda联网，或者用vpc endpoint来私密访问aws服务而不通过联网

# layer就是把依赖提前通过layer加进去这样就不用每次调用了，也不需要在code的文件夹内添加依赖了

# /temp有10G；layer最多5layer 250mb，静态的，内容不能改，访问速度这俩都最快

# 用Provisioned Concurrency解决cold start

# lambda最多支持1000并发，reserved concurrency可以限制一个function可以拥有的最高并发数，超出的话如果是同步会触发error 429，异步的话会直接进DLQ。对于因为高并发导致的异步429 和500系列error，lambda会把这些event放回到queue里并重复尝试最多6小时，重试的间隔时间会指数型的增长，从1s到5mins

# s3里面的lambda code如果想实现多帐号复用，记得给cloudFormation 开execution role的权限

# lambda container images允许打包lambda function成container images，最大10GB for ECR

# 使用 Aliase(别名) 来指定指向的版本，test，dev，prod 等

# codeGuru是深入了解 Lambda 函数运行时的性能，需要加AmazonCodeGuruProfilerAgentAccess这个policy

# Lambda的限制：(regions范围内的)
# execution：
# memory 128mb - 10gb
# 最大执行时间: 15mins
# 环境变量 4kb
# disk容量 in the "/tmps" 512mb - 10gb
# 最高并发: 1000

# deployment:
# lambda function deployment size就是 .zip文件大小: 50 mb
# code + dependencies未压缩的文件大小 : 250 mb
# Can use the /tmp directory to load other files at startup
# 环境变量 4kb
```

# section22: DynamoDB(624-676)

```bash
# item最大400kb

# WCU是 1 wcu= 1 item/s with size 1kb

# RCU是 1 strong read或者2 event read 每秒；size最大是4kb。 event如果写完立马读，可能会有一些陈旧的数据在复制区里, strong不会

# 超出limit的原因可能是：hot keys；hot partitions；very large items

# projectExpression只检索指定的值

# FilterExpression功能跟上面这个一样，但是不能用于HASH or RANGE的key

# BatchWrite中1个call里最多25个put/delete，最大16mb，单个最大400k，UnprocessedItems处理写失败的

# BatchGet中1个call里最多100个items，最大16mb，UnprocessedKeys处理读取失败的

# partiQL是在DynamoDB上用sql的

# LSI是给一个Alternative Sort Key，创table的时候就必须defined

# GSI是给一个Alternative Primary Key，table创建以后也可以改。如果 GSI 上的写入受到限制，那么主表也会受到限制！

# DynamoDB有乐观锁

# DAX是Fully-managed, highly available, seamless in-memory cache; 默认的TTL过期时间是5分钟,最多10个node, multi AZ的，1个AZ推荐最少3个node, 是可以被静态加密保护安全的

# DAX是给单个obj的cache， elastiCache一般是储存聚合结果的

# DynamoDB stream的作用是记录对数据库的操作, 不能追溯enable stream之前的records

# DynamoDB的内容会在TTL过期之后48小时后才删除

# 事务会用2倍的WCU和RCU

# DynamoDB可以当作session state的缓存（登录状态那些东西），是severless的，但是ElastiCache是存在memory里

# DynamoDB Write Sharding可以有效地控制流量; 并发：晚得覆盖前面的 ; 原子性：都成功，结果基于所有成功的; 有条件的：只有条件通过的才成功，乐观锁相似; batch：一个用户一次性写入多个数据

# 大文件储存在s3中，然后DynamoDB 中储存url

# 复制DynamoDB可以用AWS Data Pipeline，或者备份和restore，或者自己去scan然后重新创建
```

# section23: API Gateway(677-720)

```bash
# API GATE WAY主要是用来接收REST api请求然后转发的，就是通过API gateway暴露API供使用的, 默认都是Edge-Optimized

# 用${stageVariables.variableName} 来在API gateway里表示变量，比如用变量控制版本

# MOCK是压根不和后端连接传输的，HTTP / AWS是跟backend连接的

# AWS_PROXY(Lambda Proxy):只是个代理，把client的请求发送到需要的服务中；HTTP_PROXY可以加HTTP headers(比如API key做用户认证什么的)，这俩都没法mapping template

# Mapping Templates只用于 (AWS 和 HTTP Integration集成的)，可以更改req/resp，举例来说可以用SOAP API把JSON转成XML输出(Rest是json base的，soap是xml base的)

# open API spec是API的一个规范，可以把任何API export成open API spec（json或者yaml格式），然后载用SDK变成API用于自己的应用。可以用API Gateway做Request Validation来减少传到后端的非必要请求

# API Gateway也有缓存机制，默认TTL是300s，最长1h，capacity是0.5G - 237G，基于per stage的，stage就是/xxx 那些

# IntegrationLatency是API将请求发到后端并接收到回复 这期间的等待时间, Latency是指从客户发出请求到接收到回复的等待时间, API网关执行请求的最长时间是29秒

# 400 error是client side的，500 error是服务器的

# CORS是cross origin resource share，从其他domain接收api call需要cors

# resource policies适用于cross account access(和IAM security结合)

# http api比rest api便宜很多

# websocket API在建立连接以后，通过WebSocket接口可以反复的发送消息，服务器端和客户端都能主动向对方发送或接收数据，可以用于实现多个用户同时接收到来自其中用户发送的消息
```

# section24: AWS CICD(721-771)

```bash
# codePipeline就是一个artifacts，是一个自动化的流程，比如从本地push代码到repo，然后repo代码加载到s3，s3再把代码加载进codebuild，之后build好的code发给s3，再从s3加载到codeDeploy

# stage可以有多个action group。一般流程就是，再repo改变代码并push上去，然后就deploy了，然后需要review，review过了就提交到正式环境了

# event是启动codePipeline的默认和推荐的方式

# 在source中会有build instructions ，名字是buildspec.yml，在source code 的root里, 可以用来创建测试环境，在代码上传后先测试，然后再deploy

# appspec.yml来控制deployment的

# ec2上需要预先安装codeDeploy Agent

# InvalidSignatureException，需要确保AWS的时间和EC2上的一致

# cloud9是在线修改代码的

# AWS CodeStar是整合这几个CICD功能到一起的整合版

# codeArtifact是用来存dependancy的, 授权其他账户用你的CodeArtifact的话用resource policy，且要么不授权，要么就得授权所有dependancy

# 最多有10个upstream，1个external connection。这玩意相当于父依赖，子依赖没有的就去父依赖里拿，在没有的需要的就去连外部

# codeGuru是ml的，自动代码review 和 应用程序性能建议

# MaxStackDepth 代表function调用次数，比如a调用b，b调用c，c调用d，这就是深度为4
```

# section25: AWS Serverless Application Model (SAM)(772-783)

```bash
# SAM是部署severless服务的框架，全用yaml文件写配置

# 可以用第三方的cli比如idea，vscode来debug

# SAM 是通过 CloudFormation 建的

# 需要Transform and Resources sections

# sam build：获取依赖项并创建本地部署工件

# sam package：打包并上传到Amazon S3，生成CF模板

# sam deploy：部署到 CloudFormation
```

# section26: AWS Cloud Development Kit(784-796)

```bash
# 用CDK使用自己熟悉的语言配置aws服务，如果编译通过整个配置文档会被编译成CouldFormation template格式的

# CDK Assertions Module包含流行的test框架

# Template.fromStack是用CDK中有的模板去从测试

# Template.fromString是导入自己的去测试

#L1是所有directly available的source in CloudFormation，L2是AWS resources with higher level, L3是multiple related resources，帮助去完成aws task的
```

# section27: Amazon Cognito Section(797-820)

```bash
# cognito user pool是一个可以用来专门做用户登录/验证/改密码和用户管理的这些功能的，Login sends back a JSON Web Token (JWT)，准确来说就是托管登陆服务给cognito，然后验证通过以后可以访问网站内容

# Hosted UI Custom Domain是可以支持账号登录用fb，google登录的那个，需要用ACM certificate在us-east-1

# CUP也可以对可以账号限制登录或者开启MFA

# ALB可以用Cognito Auth或者OIDC Auth.

# Cognito Identity Pools (Federated Identity):是提供使用拥有者的aws服务权限的

# Cognito User Pools (for authentication = identity verification), Cognito Identity Pools (for authorization = access control)
```

# section28: Step Function & AppSync(821-843)

```bash
# 设定流程分步执行的，Model your workflows as state machines (one per workflow)

# Choice State -Test for a condition to send to a branch

# 用 Retry 和 catch 去处理error

# BackoffRate是说每次retry之后等待时间需要按怎样的倍数增长

# Wait for Task Token是需要被同意才能进行下一步

# AppSync is a managed service that uses GraphQL

# Retrieve data in real-time with WebSocket or MQTT on WebSocket

# aws Amolify相当于移动端的beanstalk
```

# section29: Advanced Identity Section(844-865)

``` bash
# Security Token Service允许临时获得进入aws resource的权限，15分钟 - 1小时

# Define an IAM Role for another account to access

# 用STS去 retrieve credentials 和 模拟所拥有的IAM role

# 没有明确allow的都deny

# IAM 和其他policy一起用的话只要有明确的deny就deny，否则的话只要有一方允许allow就是允许

# IAM 可以用动态policy，${aws:username}代替指定的角色，省的创建一堆IAM role

# 可以吧IAM role pass给别人
```

# section30: AWS Security & Encryption(866-908)

```bash
# CMK是custom Master key

# SSM Parameter Store是存配置和秘密的，或者说存变量的，可以加密可以不加密
```

```bash
# KMS 和 couldHSM都支持对称加密（Symmetric）和非对称加密（Asymmetric）

# client side 需要client自己加密，server不需要任何加密程序。

# 用KMS就不需要自己再创建任何key了

# 被加密的东西如果通过共享AMI的话，必须得把加密用的CMK也共享

# 复制snapshot到其他账户的话。需要附加上KMS key policy，这个也是access KMS CMK的
```

```bash
# KMS Encrypt API 大小限制为 4kb

# 大于 4kb 的用 Envelope Encryption（GenerateDataKey API）

# datakey是对数据加密的

# Data Key Caching可以减少datakey的复用。

# GenerateDataKeyWithoutPlaintext这个不是immediately的
```

```bash
# CloudHSM和SSE-C一起用比较好
```

```bash
# SSM Parameter Store普通版的话是4kb，advanced是8kb，不回进行自动轮换
```

```bash
# AWS Secrets Manager是可以进行强制自动密钥自动轮换
```

```bash
# CloudWatch Logs中，associate-kms-key是对已有log加密，create-log-group是对创建的
```

```bash
# AWS Nitro Enclave是高隔离性的vm 
```

# section31: Other AWS Services(909)

```bash
# SES 只用于邮件

# OpenSearch可以进行基于任何字段的搜索（数据库），而不必被主键束缚

# Amazon Athena是无服务的数据分析，进行商业智能分析报告的，可以通过用columnar data，Compress data，Partition，Use larger files来进行性能提升

# Amazon Managed Streaming for Apache Kafka（MSK）是Amazon Kinesis的替换，数据在EBS里存多久都行

# MSK的message默认1MB，最多10MB，

# AWS Certificate Manager (ACM)进行配置，管理和部署公共的SSL/TLS Certificates的

# AWS Private Certificate Authority (CA)私有证书 颁发，管理的

# AWS Macie是用机器学习来发现并保护 拥有的敏感数据的

# AWS AppConfig 配置、验证和部署 动态配置 的

# CloudWatch Evidently 允许向少量%用户提供new features并测试的
```





重点要看API gateway，Lambda，S3，Dynamo DB， CI/CD，SQS, KMS, Beanstalk

次重要Cognito, Elasticache, Elastic Beanstalk, SAM
