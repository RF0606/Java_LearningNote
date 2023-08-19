# 概念：





# IAM-Identity and Access Management:



# 1. EC2知识：

## 1.1 key-pair：

对于登录来说key-pair是必须的，一般用RSA加密就行，然后对于mac, linux和windows10用户来说，可以使用.pem格式文件。如果系统选的是windows的话可以用本机电脑自带的远程桌面连接去直连虚拟机，连接设置那里打开下载下来的key就行

## 1.2 Security group：

安全组可以创建很多，一个instance里也可以有好几个安全组，安全组分为入站和出战的，入站就是哪些接口开放了对外的权限

## 1.3 SSH连接：

win7，8 用PuTTY，已经操作过了把pem格式的放到pittygen里转一下，然后保存为私钥，就成为ppk格式文件了。然后Host Name写EC2-IP，之后去 **Connection** > **SSH** > **Auth > Credentials**，在private哪里打开文件夹双击ppk文件，再open就行了。可以通过上面保存session，以后不需要重复操作

windows10：terminal或者powershell里用`ssh -i {pem的路径} ec2-user@{public ipv4}`，其中ip里的instance一定要有前面的user，不过ec2-user是默认的

## 1.4 AMI:

就是虚拟机系统，主流linux，macos和windows，还有其他自定义的和第三方的。自己创建的AMI可以通过右键create image保存下来，并用来建新的instance

## 1.5 EC2 Image Builder：

自动创建instance然后安装软件，再弄成AMI再去测试instance

## 1.6 Elastic Container Service

为container准备的服务,ECS这里是负责deploy image的

## 1.7 Elastic Container Registry

为container准备的服务,ECR这里是负责储存docker images的

## 1.8 Elastic Container Service for kuberneters

为k8s准备的服务

## 1.9 AWS Elastic BeanStalk

一个自动帮你deploy环境的instance，但是底层os啥的环境啥的你还是可以更改的

属于PaaS，也有cloudWatch，可以检测health

## 1.10 AWS Lambda

也是一个自动帮你deploy环境的instance，但是这是serverless的，压根看不到底层也改不了

## 1.11 AWS Batch

是一系列批处理管理功能，能够让开发人员、科学家和工程师轻松高效地在AWS 上运行成千上万个批处理计算作业。

批处理是指计算机用来周期性地完成大量重复数据作业的方法。某些数据处理任务（如备份、筛选和排序）可能需要大量计算，而且在单个数据事务上运行效率很低。相反，数据系统通常可以在计算资源更普遍可用的非高峰时间批量处理这些任务，例如一天结束时或夜间。例如，考虑一个全天接收订单的电子商务系统。系统可能会在每天结束时收集所有订单，并用一个批处理与订单履行团队共享，而不是在发生时处理每个订单。

## 1.12 Amazon lightsail

轻量化的简单的EC2,包括快速启动项目所需的一切内容 - 实例（虚拟专用服务器）、容器服务、托管式数据库、内容分发网络 (CDN) 分配、负载均衡器、基于 SSD 的块存储、静态 IP 地址、注册域的 DNS 管理以及资源快照（备份），并且每月的费用少且可预测。

简单来说就是都给你打包配好了，适合个人开发或者小企业或者简单功能

## 1.13 Fargate 

AWS自己用来launch docker container的，serverless的。我们只需要把container丢进去就行了，自动化

# 2. EBS-Elestic Block Volume:

弹性储存，network drive的，instance删了数据也还在。同一时间只能mount到一个instance上（一般来说，不全是），但一个instance可以有多个EBS，并且会bound to a specific availabilty zone，被锁定到特定的AZ

## 2.1 snapshots:

就是backup备份，也可以放到snapshot Archive里用来整个EBS文件跨AZ传输。

设置recycle bin储存所有删除的snapshot达到备份作用

## 2.2 EC2 instance store:

提供高性能的hardware disk，如果关了 instance， 这个储存也会消失，主要用于临时储存

## 2.3 EFS-Elastic File System：

可以把EFS驱动器装载到很多instance上，比如说上百个instance共享一个EFS，可以跨AZs, 只用于EC2 

## 2.4 EFS-IA:

存不常用的efs的地方

## 2.5 Amazon FSx:

第三方高性能file systems

有windows和Lustre（linux + cluster）两个版本

lustre多用于高性能的比如机器学习

# 3. Elastic load Balancing

Elasticity是Horizontal的，也就是添加instance

high availability和Horizontal非常相关，可用的instance很多

负载均衡是平均的把每个请求发到不同的instance去处理

对于https的处理需要加证书，也就是SSL（ Secure Sockets Layer,）

## 3.1 application：

只处理HTTP和HTTPS的（layer 7），给了static DNS（URL）

需要建application LB，之后需要有target group

## 3.2 Network：

处理TCP和UDP（layer 4），给了static IP

## 3.3 classis：

同时包括layer4和layer7，可以加防火墙

# 4. Auto Scaling Groups

Scalabiliy是vertical的，就是单机性能扩展

Elasticity

需要创建launch template，选择复杂均衡器，之后配置configure

三种策略：根据cpu使用率设置上下阈值去自动增加减少instance，或者固定cpu使用率为40%去增加减少instance，或者知道哪个时段需求暴增，提前增加instance

# 5. AMAZON S3

## 5.1 作用：

backup & storage

disaster recovery

archive 存档

hybrid cloud storage

media hosting 

data lakes & bigdata analytics

software delivery

static website

## 5.2 buckets

存文件的，文件名必须全局唯一，必须在特定的region定义创建，bucket命名无大写无下划线，3-63字符，不能是ip，小写字母或数字开头

KEY: 是full path，就是从my-bucket路径下的全路径名字

Obj储存最大5TB,超过5G的需要分开上传

Metadata key/value键值对

## 5.3 S3 Storage Classes

### 5.3.1 Durability:

如果你在s3中有10m个object，平均1万年会丢失一个（11个9）

### 5.3.2 Availability:

每年平均有53分钟无法用服务（99.99%）

### 5.3.3 standard GP:

99.99% availability， use for frequently accessed data,低延迟高吞吐，sustain 2 concurrent facility failures.(2个并发设施故障)

一般用于大数据分析，移动&游戏应用及内容开发

### 5.3.4 standard IA:

less frequently accessed但模式需要rapid access，cost比standard低

99.9% availability，用于灾难恢复和备份

最短30天

### 5.3.5 One zone IA:

High durability(99.99999999999%) in single AZ, AZ被破坏数据就会丢失

99.5%  availability，储存的备份的辅助副本，可以是内部数据或者重新创建的

最短30天

### 5.3.6 Glacier Instant Retrieval:

及时检索 几ms内，最少storage 是90天

低成本归档/备份 的storage，费用是储存+检索费用

### 5.3.7 Glacier Flexible Retrieval:

Expedited是1-5 min内检索，Standard是3-5hrs，Bulk是5-12hrs(免费)

最少storage 是90天

### 5.3.8 Glacier Deep Archive:

long term storage， Standard (12hrs), Bulk (48hrs)

最少storage 是180天

### 5.3.9 Intelligent Tiering: 

可以基于usage把object自动的在access tiers移动

需支付小额月度监控费和auto tiering fee，没有检索费用

四个tier：

Freq Access tier (automatic): default 

Infrequent Access tier (automatic): Ojbect not accessed for 30 days

Archive Instant Access tier (automatic): Ojbect not accessed for 90 days

Archive Access tier (optional): configurable from 90 days to 700+ days

Deep Archive Access tier (optional): configurable from 180 days to 700+ days

## 5.4 S3 Encryption

server-side Encryption(default):上传以后由amazon s3来加密

client-side  Encryption: 用户上传之前自己加密

## 5.5 AMS snow Family

  离线(线下)数据迁移，aws寄一个物理的device过来，迁移完再寄回去

### 5.5.1 snowball Edge Storage Optimized:

80TB of HDD capacity（机械）

### 5.5.2 snowball Edge Compute Optimized:

42TB of HDD or 28TB NVMe capacity（机械或NVMe固态）

### 5.5.3 snowcone：

比较小

8TB of HDD(机械)，可以用DataSync从datacenter把数据传回去

### 5.5.4 snowcone SSD：

比较小

14TB of SSD(固态)，可以用DataSync从datacenter把数据传回去

### 5.5.5 snowmobile:

100PB，非常安全，非常大的数据传输

### 5.5.6 Edge Computing:

可以用上述设备进行计算，因为上面的这些设备也是有cpu和ram的。都可以运行EC2 和Lambda的

OpsHub用来连接device

## 5.6 GateWay

用于hybrid储存的，联合本地storage和s3

# 6. Databases

## 6.1 Read Replicas:

创建多个Amazon RDS的read replicas同时从数据库读取数据，提高性能

## 6.2 Multi-AZ:

当主RDS出问题的时候会触发故障转移哦，failover DB会接手主RDS的读写功能，这个是跨AZ的，主RDS不出问题是看不到failover DB的

只能有一个不同AZ的failover DB

## 6.3 Multi-Region:

可以再不同region部署read replicas，其他地区的application就可以从本地的db中读取数据，但是写入还是要写入到main RDS的

## 6.4 ElastiCache:

主要针对于redis和memcached，所有基于memory的db都必须选ElastiCache的

提高性能降低延迟

## 6.5 DynamoDB:

no-sql db, 可以复制扩展到三个AZ，key-value的

distributed serveless database, 不需要EC2或者什么

特别快的速度，特别低的延迟，直接创建table就行，数据库一直都是建好的

### 6.5.1 DynamoDB Accelerator-DAX: 

缓存最频繁读取的对象的工具，由DynamoDB完全集成

### 6.5.2 Global Tables:

全局表是一个完全托管、无服务器、多区域和多活动的数据库

## 6.6 Redshit:

base on PostgreSQL, 但不是OLTP

是OLAP, 用于分析和数据的仓库

load data once per hour， 10倍于其他db的性能，基于Column储存而不是行

适合大规模并行查询，由SQL接口和BI tools

适合计算分析和可视化

## 6.7 Amazon EMR：

帮助建立Hadoop Cluster(大数据)的，负责供应所有EC2并进行配置

## 6.8 Amazon Athena：

serverless，分析S3用的，用sql去query files

适合Business intelligence / analytics / reporting, analyze&query VPC logs, ELB logs, CloudTrail trails

## 6.9 Amazon QuickSight:

serverless machine learning-powered busniess intelligence service 

数据可视化的 AWS中进行DI的首选

## 6.10 Aurora：

postgresql和mysql的aws实现

完全托管的

## 6.11 DocumentDB：

MongoDB的aws实现，No-SQL db

可以复制扩展到三个AZ

## 6.12 Amazon Neptune：

全托管graph database（social network）,可以复制扩展到三个AZ, 15个read replicas

## 6.13 Amazon QLDB:

查看应用程序随时间推移所做的所有更改的历史记录，是一个中央机构，用于financial 和 ledger

写进的数据没法被删除更改

## 6.14 Amazon Managed Blockchain:

可以加入公共区块链网络，或者创建自己的可拓展私有区块链网络

## 6.15 AWS Glue:

ETL 服务，serverless

glue data catalog to store structural and operational metadata

## 6.16 DMS Database Migration Service

迁移数据库，数据库被迁移的时候仍可用，可以从同源或者不同源的迁移，如oracle to oracle, sql server to aurora

# 7. Deployments & Managing Infrastructure at Scale

## 7.1 CloudFormation

使用code去完成infrastructure的操作，本质是yml文件

用template去配置ec2，安全组，负载均衡等等

## 7.2 AWS CDK

用java，python等语言写，然后会被编译成yml文件

## 7.3 AWS CodeDeploy

自动deploy application的

可以用于EC2 也可以用于本地的

## 7.4 AWS CodeCommit

跟github差不多

## 7.5 AWS CodeBuild

给CodeCommit里的代码编译用的

## 7.6 AWS CodePipeline

连接CodeCommit和CodeBuild，是干CI/CD活儿的

## 7.7 AWS CodeArtifact

检索代码依赖的，store software package/dependencies on AWS， 或者artifact management

## 7.8 AWS CodeStar

整合整个CI/CD流程内需要的东西，配置这玩意去部署全部内容，相当于快速开始

## 7.9 AWS Cloud9

是一个cloud的IDE

## 7.10 AWS System Manager (SSM)

大规模管理EC2 instance 和本地部署系统， hybrid AWS service

patching automation for enhanced compliance，对所有服务器和instance进行自动修补以增强合规性

跨整个servers去run commands，store parameter configuration with the SSM parameter Store

## 7.11 SSM Session Manager

允许启动secure shell on EC2和On-premises的服务器

不需要SSH access，bastion host和SSH keys

可以关闭port22 on ec2

## 7.12 AWS OpsWorks

用于Manage Chef和Puppet这俩的，SSM的替代方案，但只有standatd AWS resources

# 8. AWS Global infrastructure

application deployed到多个地域了，好处就是低延迟，而且还有灾难恢复功能，并且能防止黑客攻击

## 8.1 Route 53

是个Managed DNS，用户向DNS发起访问请求，DNS返回IP地址

### 8.1.1 Simpe Routing Policy

没有health checks，简单的请求访问然后返回地址

### 8.1.2 Weighte Routing Policy

加权路由策略允许在多个institute实例分配权重，按权重分流，可以用health check

### 8.1.3 Latency Routing Policy

根据用户位置重新定位到离他们近的institute

### 8.1.4 Failover Routing Policy

有一个主要的institute和一个failover的institute，主要执行health check，要是坏了就启用failover的

## 8.2 CloudFront

是个CDN (Content Delivery Network)，通过在不同edge缓存的网站内容来提升性能

有 DDoS保护

s3 bucket用于分发file并且缓存在edge，OAC保证至于cloud front访问能得到他，cloud front还可以用来上传文件到s3，也可以在任何Custon Origin(HTTP)使用 

可以和s3连接控制访问内容

## 8.3 S3 Transfer Acceleration

加速从其他地区传入指定s3的速度

用户传入的文件会先传到最近的edge，然后用内部网路传到s3

## 8.4 AWS Global Accelerator

加速全球化的application的可用性和性能，和上边的原理一样通过使用内网传输

## 8.5 AWS Outposts

允许customer使用本地部署的架构使用方法去使用cloud，同时可以在本地部署属于cloud的东西，扩展本地架构内部部署系统

## 8.6 AWS WaveLength

是infrastructure deployments，5G相关，通过5G提供超低延迟

## 8.7 AWS LocalZones

扩展别的地区AZ为本地zone

##  8.8 Global Applications Architecture

主动/被动模式，passive AZ可以改善读取延迟

主动/主动模式，每个AZ都可以读取写入数据，明显改善了读取写入延迟

# 9. cloud integrations

decouple解耦

SQS主要用来解耦，SNS是同时对多个接收者发信息，Kinesis用于大数据，MQ用于产品迁移到云同时还得保留开放协议的

## 9.1 SQS simple queue service

SQS是分布式排队系统，不会主动发送给客户端，如果想要执行这些任务和获取这些信息需要客户端主动轮询消息队列服务。

![4e724b0ec3aa8033a59062f148b51f0](C:\Users\10602\Desktop\java学习\amazon_certificate\AWS Cloud Practitioner\imgs\4e724b0ec3aa8033a59062f148b51f0.png)

最古老的产品，serverless，用于解耦decouple， 保留时间4-14days，FIFO

![94b8da6c82bc5cb84bc3b40b94642da](C:\Users\10602\Desktop\java学习\amazon_certificate\AWS Cloud Practitioner\imgs\94b8da6c82bc5cb84bc3b40b94642da.png)

## 9.2 Kinesis

real-time big data streaming

## 9.3 SNS 

将一条消息发送给多个接收者， SNS是分布式发布 – 订阅系统，会主动发给客户端

## 9.4 Amazon MQ

其他软件会使用open protocols，Amazon MQ适配这些用的，仅用于将公司迁移到云并需要使用开放协议的情况

# 10. Cloud Monitoring

## 10.1 CloudWatch Metrics

监控的，能监控各种东西，cpu使用率，花费，status check，网络，EBS的信息，S3的信息啥的，还可以设置Alarms

## 10.2  CloudWatch Logs

收集日志的

## 10.3 EventBridge

对发生的事件做出反应，其中1个是调度cron jobs，比如说create rule：每隔1小时创建一个event，该even将出发在lambda函数上运行的script

## 10.4 CloudTrail

provides governance, complianc and audit for aws account(为 aws 账户提供治理、合规性和审计)

获得账户内发生的所有API调用或事件的历史记录，用于审计和安全目的

## 10.5 AWS X-Ray

对应用程序进行跟踪和可视化分析，看性能，失败之处和error，troubleshooting用的

## 10.6 Amazon CodeGuru

ML-powered service，做两件事，automated code reviews and application performance recommendations

## 10.7 AWS Health Dashboard

service history and my account, 展示health情况的

**Service Health Dashboard 显示AWS 服务的一般状态，**

**而Personal Health Dashboard 提供关于特定AWS 环境的主动、透明通知**

# 11. VPC& Networking

## 11.1 IP Address

IPv4,有个elestic IP，帮你固定ec2的public IPv4地址的

## 11.2 VPC & Subnets Pirmer

vpc是虚拟私有云，专用网络，subnet是vpc一部分，与AZ相关

VPC可以包含多个AZ，每个AZ中可以有public subnet和private subnet，public subnet直接和外网链接，private subnet不行，但可以放database什么的不需要连外网的

public subnet的需要通过Internet Gateway去联网，然后需要有个route去连Internet Gateway

NAT Gateway允许private subnet联网的同时保证还是private的， NAT是在public subnet中的，private subnet连NAT, NAT连Internet Gateway

subnet给定了一个IPv4地址范围，在特定subnet下创建的EC2，地址都在那个范围内

## 11.2 Network ACL & Security Groups

 Network ACL是控制子网流量的防火墙，可以定义Allow和Deny rules，rules只能包含ip address， 是stateless的, Return traffic must be explicitly allowed by rules(返回流量必须得到规则明确允许)

Security Groups是控制进出EC2流量的，只允许allow rules， 是Stateful的，Return traffic is auto allowed , regardless of any rules. 

## 11.3 VPC Flow Logs & Peering

获取flow的日志，VPC, Subnet和Elastic Netwark Interface都行

VPC Peering是连接两个VPC的，用AWS的network，通信的话需要把需要通信的两个VPC连一起

## 11.4 VPC Endpoints

允许使用private network连接AWS service

VPC Endpoint Gateway是连S3 和Dynamo DB的

VPC Endpoint Interface是连接所有服务的除了S3 和Dynamo DB

## 11.5  VPC PrivateLink

来自于VPC Endpoints。允许将自己VPC中运行的服务直接且私密的连接到其他VPC，而不需要VPC对等

## 11.6 Direct Connect & Site-to-Site VPN

Site-to-Site VPN：连接on-primise DataCenter和cloud VPC的，是加密通话/连接，但会有带宽限制和安全问题

对此，我们需要在on-primise DataCenter的内部部署Customer Gateway(CGW)，AWS上需要部署Virtual Private Gateway(VGW)

Direct Connect：只在on-primise  DataCenter和cloud VPC之间建里物理连接，更快，更安全，更可靠

## 11.7 AWS Client VPN

如果想用自己的电脑用OpenVPN连接自己的private network in AWS and on-primise的话，WS Client VPN允许你用private IP连接你的EC2 instance（如果你在一个private VPC network里）

## 11.8 Transit Gatway

将数千个VPC和on-primise系统之间建立peering，hub-and-spoke(star) connection，所有东西都通过这玩意中转一遍

# 12. Security & Compliance 

## AWS shared Responsibility Model

AWS Responsibility 是确保云安全的，他们提供的所有infrastructure的东西都要确保安全

但是自己云内的安全是自己负责的，自己的user，系统，防火墙，网络配置等

然后有些是共享的，比如说你和aws都得培训员工正确使用云

## 12.1 DDoS Protection: WAF & Shield

DDoS就是大流量攻击，AWS Shiled可以全天候防止DDoS，standard是免费的

AWS WAF可以过滤特定的请求，是web的防火墙

## 12.2 AWS Network Firewall

保护VPC的

## 12.3 Penetration Testing

渗透测试，是自己尝试攻击自己的基础架构以测试安全性，AWS可以帮助进行安全评估和渗透测试

## 12.4 Encryption with KMS & CloudHMS

aws有两种加密类型，data at rest 静态加密 和 data in transit 传输加密

KMS是key management service，用key进行加密，AMS管理密钥

HSM是AWS提供加密硬件，我们自己管理密钥

AWS的CMK是给aws服务用的，自己的CMK是我们自己用的，Customer Master Key

## 12.5 AWS Certificate Manager ACM

配置管理部署SSL / TLS 证书的，连负载均衡，CloudFront和API Gateway的

## 12.6 Secrets Manager

储存sercet的，能rotation， lambda可以自动生成，RDS中管理和轮换secrets

## 12.7 AWS Artifact

可以让客户按需访问合规AWS documentation和AWS agreement

## 12.8 GuardDuty

进行智能威胁发现来保护AWS account，有机器学习算法，用异常检测

## 12.9 Amazon Inspector

Auto Security Assessments，可以进行漏洞分析

只适用于EC2 instance，Container Images & Lambda Functions

## 12.10 AWS Config

通过记录配置及起随时间的变化来帮助审核和记录资源的合规性，可以查看config修改的所有列表，然后可以分析一些可能会存在的配置问题

## 12.11 Macie

全面鼓励数据安全和隐私的服务，使用ML 发现和保护 我在AWS中的敏感数据

## 12.12 AWS Security Hub

安全中心，管理多个账户的安全性并执行自动安全检查，把这些安全检查工具集合成一个页面了

## 12.13 Amazon Detective

用来检测潜在的安全问题时，需要找出问题如何发生的，找到根本原因。detective会用ml去快速调查并定位可疑活动的根本原因

## 12.14 AWS Abuse

如果怀疑AWS某些资源是被滥用或者用于非法目的或者非法行为，可以向AWS报告，AWS Abuse是干这个用的，向他们举报

## 12.15 Root User Privileges

root用户权限。root user能不用就不用。但是如下行为只能通过root user去做：

1. Change account setting--重要
2. View Certain tax invoices
3. Close your AWS account--重要
4. Restore IAM user permissions
5. Change or cancel AWS Support plan--重要
6. Register as a seller in the reserved instance marketplace(RIM)--重要
7. Configure Amazon S3 bucket to enable MFA
8. Edit or delete S3 policy that includes an invalid VPC ID or VPC endpoint ID
9. Sign up for GovCloud

## 12.16 IAM Access Analyzer

找出哪些资源需要与外部共享

# 13. Machine Learning

## 13.1 Amazon Rekongition

做人脸识别的和基于人脸的检测的

## 13.2 Amazon Transcribe

自动将语音转换成文本，ASR深度学习被使用在这里，可以自动删除关于个人信息的部分，并且是可以访问使用多语言音频的自动语言识别

## 13.3 Amazon Polly

将文本转换成语音

## 13.4 Amazon Translate

就是i翻译，但可以为国际用户本地化内容

## 13.5 Amazon Lex & Connect

amazon lex和Alexa设备采用相同的技术，可以帮助构建聊天机器人或者呼叫中心机器人的技术

Amazon Connect是建立call center的，是一个可视化的联络中心，允许接电话，创建联系人流程

## 13.6 Amazon Comprehend

是自然语言处理-NLP，Fully managed的serverless service，用ML去寻找洞察力和关系 in text，理解text用的语言，提取关键信息，可以坐情感分析，可以分析文本时消极还是积极的

## 13.7 SageMaker

Fully managed Service for developers / data scientists to build ML models

## 13.8 Forecast

Fully managed Services使用ML去提供准确的预测

## 13.9 Kendra

Fully managed Services 使用ML去做文档搜索，extract answers from within a document

## 13.10 Personalize

Fully managed Services 使用ML 去构建带有实时个性化推荐的应用（个性化的产品推荐，重新排名）

## 13.11 Textract

用来提取文本，通过ML自动提取文本，handwriting或者data从任何被扫描的文档里

# 14 Account Management, Billing & Support

## 14.1 AWS Organizations

global service, 可以管理多个aws账户，可以用一个账户支付所有账户的钱

从上往下账户管理SCP，外部OU的权限内部要遵守

## 14.2 Consolidated Billing

可以combine the usage across all AWS account，可以分享volume prcing，保留instance共享, 还有折扣什么的

把所有账户的账单合成一个

## 14.3 AWS Control Tower

easy way to set up and govern a secure and compliant multi-account AWS environment 

创建多账户环境，多个organizational unit和多个账户的

## 14.4 AWS RAM

与其他用户共享你所拥有的资源

## 14.5 AWS Service Catalog

制作快速列表，可以使用的所有产品，预先配置application，让客户使用管理员预先设置好的程序

## 14.6 Pricing Models in AWS

1. pay as u go: 为使用的东西付费 
2. save when u reserve: 预约会省钱
3. pay less by using more
4. pay less as AWS grows

free的服务：

1. IAM
2. VPC
3. Consolidated bill 合计计费
4. Elastic Beanstalk 本体免费，创建的东西需要按需付费
5. CloudFormation 本体免费，创建的东西需要按需付费
6. Auto scaling Groups 本体免费，创建的东西需要按需付费

### 14.6.1 计费模式

EC2：使用的时候才需付费

1. on-demand：最短60秒
2. reserved instance： 1-3年，省75%
3. Spot instance
4. dedicated host： 1-3年
5. savings plan

Lambda：

1. per call
2. per duration

ECS:

1. stored and created in appliation

Fargate:

1. pay for vCPU and memory allocated

S3:

1. Storage class
2. number and size of objects
3. number and type of requests 
4. data transfer out of s3 
5. s3 transfer accelerations
6. lifecycle

EBS:

1. volume type
2. storeage volume in GB
3. IOPS
4. snapshots
5. data transfer(outbound data transfer)

RDS:

1. Per hour
2. database characteristics
3. purchase type
4. backup storage 
5. additional storage 
6. number of input and output request per month
7. Deployment type(单AZ还是多AZ)
8. Data transfer(outbound data transfer)

cloud front

1. 价格根据地域区别不同
2. edge location使用费
3. Data transfer out
4. numbers of HTTP/HTTPS requests

Networking

1. pay for 不同AZ内的instance 通信，public IP和private 
2. 不同region内的通信，用private IP更便宜，提高性能

## 14.7 Saving plan

承诺再未来1-3年内每小时花费一定的金额就会获得折扣，而不是保留instance

EC2 Saving plan:

Compute Saving plan: 更灵活

Machine learning saving plan：for sageMaker

## 14.8 Compute Optimizer

通过工作负载推荐最佳的AWS资源来降低成本提高性能

## 14.9 Billing & Costing Tools

1. Estimating cost in the cloud:由定价计算器
2. Tracking cost in the cloud：cost explorer可以根据以前使用情况预测12个月的使用情况
3. Monitoring against cost plan

## 14.10 AWS Cost Anomaly Detection

就是成本异常监测

## 14.11 AWS Service Quotas

超出限额时得到通知

## 14.12 Trusted Advisor

分析你的AWS账户并提出5个建议：cost optimization, performance, security, fault tolerance, service limits

找到适合需求的正确支持计划

support plans, 7 core checks: s3 bucket premission, security group, IAM use, MFA on root account, EBS publis snapshots, RDS publis snapshots, service limits

Full checks: business support plan, programmatic access using aws support API

## 14.13 Support Plan

basic support is free:24*7的客服，文件，白板等，7 core trusted，personalized view的health dashboard

developer support plan: basic plan + business hours email access to cloud support associates, 无线案例，1个主联系人，根据严重性可以有不同的相应时间

business support plan: 有production workload的情况下用，拥有Trusted Advisor的Full check，24*7的客服邮件和chat to cloud support engineers, 无限案例和联系，可以支付额外费用访问基础架构事件管理，根据严重性可以有不同的相应时间

On-Ramp support plan: all from business plan + access to a pool of technical account managers, concierge support team, 基础架构事件管理和运营review，根据严重性可以有不同的相应时间

Enterprise support plan:  于上面弄不同的就是 access to a designed technical account manager

# 15. Advanced Identity

## 15.1 AWS STS(security token service)

创建临时的有限的凭据来访问aws资源

## 15.2 Amazon Cognito

为web和应用程序用户提供身份的方式，管理aws上用户

## 15.3 AD

可以用一个用户名和密码登录公司所有的计算机，就跟学校图书馆电脑是的

## 15.4 AWS IAM Identify Centers

单点登录，只需一次登录就可以登录组织中的所有aws账户

# 16. Other AWS Service

## 16.1 Amazon WorkSpaces

DaaS Desktop as a Service to provision windows or linux desktops

## 16.2 Amaszon AppStream2.0

桌面应用程序流，将应用程序流式传输到任何计算机，可以从web browser中delivered，不用特意开程序

## 16.3 AWS IoT Core

允许将IoT设备连到AWS cloud中

## 16.4 Elastic Transcoder

将储存在s3中的媒体文件转换成消费类播放设备(手机)所需格式的媒体文件

## 16.5 AppSync

为移动和web应用程序构建后端，要实时储存和同步数据，用了facebook的GraphQL技术，实时获取web和移动应用程序的数据更新

## 16.6 AWS Amplify

一组工具和服务，帮助开发和部署可拓展的全栈web和移动应用程序，可以全面管理程序，管理全栈开发的几乎一切的东西

## 16.7 Device Farm

full-managed service，用实际的移动设备和平板电脑来测试web和移动应用程序

## 16.8 AWS Backup

full-managed service，可跨AWS services集中管理和自动化备份，可以按需和按计划备份

## 16.9 Disaster Recovery Strategies

最便宜的是backup and restore

## 16.10 AWS Elastic Disaster Recovery (DRS)

将物理的，虚拟的和cloud based的servers恢复到AWS中，从而更好的执行灾难恢复

## 16.11 DataSync

允许将大量数据从内部部署移动到AWS，第一次加载之后，所有其他任务都是Incremental的

## 16.12 AWS Application Discovery Service

计划把现有的从本地迁移到云。获得有关本地服务器的信息以便于更好的迁移

用MGN可以将其他云上的物理，虚拟或者其他服务转换为再AWS上运行

## 16.13 AWS Migration Evaluator

帮助建立data-driven business case for 迁移到AWS的，商业案例的

## 16.14 AWS Fault Injection Simulator(FIS)

故障注入模拟器，创造非常具有破坏性的时间来给程序施压的

## 16.15 Step Fuctions

构建一种无服务的可视化工作流来执行lambda functions，流程图制定说这一步如果成功了或者失败了需要做什么

## 16.16 Ground Station

可以控制卫星通信(satellite communications)，处理数据并扩展卫星运营(satellite operations)，湖泊去卫星内数据，比如天气预报，表面成相，通信或视频广播

## 16.17 AWS Pinpoint

scalable 2-way(outbound/inbound) marketing communications service

支持email，SMS, push, voice and in app messaging，客户可以收到你从pinpoint发的消息

# 17. AWS Architecting & Ecosystem

## 17.1 AWS WhitePapers Well Architected Framework

general principles

1. stop guessing your capacity needs,应该用自动scaling
2. test systems at production scale
3. auto to make architectural experimentation easier
4. allow for evolutionary architectures(Design based on changing requirements)
5. Drive architectures using data
6. improve through game days(simulate applications for flash sale days)

Design princples

1. Scalability
2. Disposable resources : server should be disposable and easily configured.
3. automation
4. loosing coupling 松耦合
5. services, not servers

well farchitected framework

1. operational excellence
2. security
3. reliability
4. performance efficiency
5. cost optimization
6. sustainability

### 17.1.1 Operational excellence 

卓越运营包括运行和监控系统，交付业务价值以及持续改进支持流程和程序的能力

Design principle:

1. Perform operatons as code
2. annotate documentation
3. make frequent, small, reversible changes
4. refine operations procedure frequently经常改进操作程序
5. anticipate failure 预见失败
6. learn from all operational failures

Operational excellece AWS services

1. prepare
2. operate
3. evolve

### 17.1.2 Security

保护信息和资产，同时通过风险评估和降低风险来提供业务价值

Design principle:

1. Strong identity foundation
2. Enable traceability
3. Apply security at all layers
4. auto security best practice
5. protect data in transit and at rest
6. keep people away from data
7. prepare for security events

security  AWS services :

1. Identify and access management
2. detective controls
3. infrastructure protection
4. Data protection
5. Incident Response

### 17.1.3 Reliability

系统从基础架构或服务中断中恢复，动态获取计算资源以满足需求并缓解中断的能力

Design principle:

1. test recovery procedures
2. auto recover from failure
3. scale horizontally to increase aggregate system availability
4. stop guessing capacity
5. manage change in automation

Reliability AWS services :

1. Foundations
2. Change Management
3. Failure Management

### 17.1.4 Performance Efficiency

高效使用计算资源去满足系统要求并随着需求变化和技术发展儿保持该效率的能能力

Design principle:

1. Democratize advanced technologies
2. Go global in minutes
3. Use serverless architectures
4. Experiment more often
5. Mechanical sympathy: be aware of all AWS services

Performance Efficiency AWS services :

1. Selection
2. Review
3. Monitoring
4. Tradeoffs 权衡

### 17.1.5  Cost Optimization

以尽可能低的价格提供业务

Design principle:

1. Adopt a consumption mode : pay only for what u use
2. Measure overall efficiency
3. Stop spending money on data center operations
4. Analyze and attribute expenditure
5. Use managed and application level services to reduce cost of ownership

 Cost Optimization AWS services :

1. Expenditure Awareness
2. Cost-effective Resources
3. Matching supply and demand
4. Optimizing Over Time

### 17.1.6 Subtainability

 最大限度减少运行云工作负载对环境的影响

Design principle:

1. Uderstand your impact
2. Establsh sustainability goals 制定可持续发展目标
3. Max utilization
4. anticipate and adopt mew, more efficient hardware and software offering
5. Use namaged Services
6. Reduce the downstream impact of your cloud workloads,比如减少对能量和资源的需求并且减少客户去升级设备的需求

Subtainability AWS services :

1. EC2 Auto scaling, serverless offering
2. Cost Explorer, AWS Gravition 2, EC2 T instances, Spot Instances
3. EFS-IA, Amazon S3, Glacier, EBS Cold HDD Volumes
4. S3 Lifecycle Configurations, S3 Intelligent Tiering
5. Amazon Data Lifecycle Manager
6. Read Local, Write Global

## 17.2 AWS Well-Architected  Tool

free tool to review architectures against the 6 pillars Well-Architected Framework and adopt architectural best practices

## 17.3 AWS Cloud Adoption Framework (CAF)

是一个白皮书，不是服务，帮助实现云转型的

CAF groups its capabilities in six perspectives: Business, people, governance, platform, security, operations

## 17.4 AWS Right Sizing

正确调整规模是指以尽可能低的成本将instance类型和大小与您的工作负载性能和容量要求相匹配的过程

## 17.5 AWS Ecosystem

有很多免费资源

## 17.6 AWS IQ, re:Post

IQ是快速找专家帮你的aws project的

post是社区论坛

## 17.7 AWS Managed Service

是一个由aws专家组成的团队，去托管你的服务
