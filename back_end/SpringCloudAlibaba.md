# SpringCloudAlibaba

## 1. 系统架构演变

随着互联网的发展，网站应用的规模也在不断的扩大，进而导致系统架构也在不断的进行变化。

从互联网早起到现在，系统架构大体经历了下面几个过程：单体应用架构--->垂直应用架构--->分布式架构--->SOA架构--->微服务架构，当然还有悄然兴起的Service Mesh(服务网格化)。

接下来我们就来了解一下每种系统架构是什么样子的，以及各有什么优缺点。

### 1.1 单体应用架构

互联网早期，一般的网站应用流量较小，只需一个应用，将所有功能代码都部署在一起就可以，这样可以减少开发、部署和维护的成本。

比如说一个电商系统，里面会包含很多用户管理，商品管理，订单管理，物流管理等等很多模块，我们会把它们做成一个web项目，然后部署到一台tomcat服务器上。

![image-20220408102927363](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20220408102927363.png)

**优点**

- 项目架构简单，小型项目的话，开发成本低
- 项目部署在一个节点上，维护方便

**缺点**

- 全部功能集成在一个工程中，对于大型项目来讲不易开发和维护
- 项目模块之间紧密耦合，单点容错率低
- 无法针对不同模块进行针对性优化和水平扩展

### 1.2 垂直应用架构

随着访问量的逐渐增大，单一应用只能依靠增加节点来应对，但是这时候会发现并不是所有的模块都会有比较大的访问量。

还是以上面的电商为例子，用户访问量的增加可能影响的只是用户和订单模块，但是对消息模块的影响就比较小.那么此时我们希望只多增加几个订单模块，而不增加消息模块.此时单体应用就做不到了，垂直应用就应运而生了。

所谓的垂直应用架构，就是将原来的一个应用拆成互不相干的几个应用，以提升效率。比如我们可以将上面电商的单体应用拆分成：

- 电商系统(用户管理 商品管理 订单管理)
- 后台系统(用户管理 订单管理 客户管理)
- CMS系统(广告管理 营销管理)

这样拆分完毕之后，一旦用户访问量变大，只需要增加电商系统的节点就可以了，而无需增加后台和CMS的节点。

![image-20220408103318368](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20220408103318368.png)

**优点**

- 系统拆分实现了流量分担，解决了并发问题，而且可以针对不同模块进行优化和水平扩展
- —个系统的问题不会影响到其他系统，提高容错率

**缺点**

- 系统之间相互独立，无法进行相互调用
- 系统之间相互独立，会有重复的开发任务

### 1.3 分布式架构

当垂直应用越来越多，重复的业务代码就会越来越多。这时候，我们就思考可不可以将重复的代码抽取出来，做成统一的业务层作为独立的服务，然后由前端控制层调用不同的业务层服务呢？

这就产生了新的分布式系统架构。它将把工程拆分成表现层和服务层两个部分，服务层中包含业务逻辑。表现层只需要处理和页面的交互，业务逻辑都是调用服务层的服务来实现。

![image-20220408103454169](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20220408103454169.png)

**优点**

- 抽取公共的功能为服务层，提高代码复用性

**缺点**

- 系统间耦合度变高，调用关系错综复杂，难以维护

### 1.4 SOA架构（面向服务架构）

在分布式架构下，当服务越来越多，容量的评估，小服务资源的浪费等问题逐渐显现，此时需增加一个调度中心对集群进行实时管理。此时，用于资源调度和治理中心（SOA Service Oriented Architecture，面向服务的架构）是关键。

![image-20220408103703214](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20220408103703214.png)

**优点**

- 使用注册中心解决了服务间调用关系的自动调节

**缺点**

- 服务间会有依赖关系，一旦某个环节出错会影响较大（服务雪崩）
- 服务关心复杂，运维、测试部署困难

### 1.5 微服务架构

微服务架构在某种程度上是面向服务的架构SOA继续发展的下一步，它更加强调服务的"彻底拆分"。

![image-20220408103856809](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20220408103856809.png)

**优点**

- 服务原子化拆分，独立打包、部署和升级，保证每个微服务清晰的任务划分，利于扩展
- 微服务之间采用Restful等轻量级http协议相互调用

**缺点**

- 分布式系统开发的技术成本高（容错、分布式事务等）

## 2. 微服务架构介绍

微服务架构，简单的说就是将单体应用进一步拆分，拆分成更小的服务，每个服务都是一个可以独立运行的项目。

### 2.1 微服务架构常见问题

一旦采用微服务系统架构，就势必会遇到这样几个问题：

- 这么多小服务，如何管理他们？（服务治理 注册中心[服务注册 发现 剔除]）
- 这么多小服务，他们之间如何通讯？（**restful** rpc）
- 这么多小服务，客户端怎么访问他们？（网关）
- 这么多小服务，一旦出现问题了，应该如何自处理？（容错）
- 这么多小服务，一旦出现问题了，应该如何排错？（链路追踪）

对于上面的问题，是任何一个微服务设计者都不能绕过去的，因此大部分的微服务产品都针对每一个问题提供了相应的组件来解决它们。

![image-20220408102341245](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20220408102341245.png)

### 2.2 微服务架构常见概念

#### 2.2.1 服务治理
















































































































