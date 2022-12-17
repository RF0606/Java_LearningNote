# SpringCloudNetflix

## 1. 快速上手

父工程导入依赖

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>2021.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
</dependencyManagement>
<!--还有其他依赖-->
```

**服务提供者**

编写接口

**服务调用者**

编写Rest

```java
@Configuration
public class RestConfig {
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
```

调用

```java
@RestController
public class DeptConsumerController {
    @Autowired
    RestTemplate restTemplate;

    private static final String REST_URL_PREFIX = "http://localhost:8001";

    @RequestMapping("/consumer/dept/add")
    public boolean addDept(Dept dept) {
        return restTemplate.postForObject(REST_URL_PREFIX+"/dept/add", dept, Boolean.class);
    }

    @RequestMapping("/consumer/dept/get/{id}")
    public Dept getDept(@PathVariable("id") Long id) {
        return restTemplate.getForObject(REST_URL_PREFIX+"/dept/get/"+id, Dept.class);
    }

    @RequestMapping("/consumer/dept/list")
    public List<Dept> getDept() {
        return restTemplate.getForObject(REST_URL_PREFIX+"/dept/list", List.class);
    }
}
```

 ## 2. Eureka

服务注册中心组件

直接访问ip端口

### 2.1 EurekaServer单例

导入依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

配置Eureka

```yaml
server:
  port: 7001
eureka:
  instance:
    hostname: localhost # Eureka服务端实列名
  client:
    register-with-eureka: false # 是否注册自己
    fetch-registry: false # 是否从注册中心注册信息 false则表示自己为注册中心
    service-url: # 监控页面地址
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
```

在启动类上添加注解

```java
@EnableEurekaServer
```

**服务提供者**

导入依赖，对于EurekaServer来说服务提供这也是客户端

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

配置Eureka

```yaml
eureka:
  client:
    service-url: # 监控页面地址
      defaultZone: http://127.0.0.1:7001/eureka/ # EurekaServer地址
  instance:
    instance-id: springcloud-provider-dept-8001
    # instance-id: ${spring.cloud.client.ip-address}:${spring.application.name}:${server.port}:@project.version@
```

在启动类上添加注解

```java
@EnableEurekaClient
```

服务提供者监控信息

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

在父工程中添加build

```xml
<build>
    <finalName>microservicecloud</finalName>
    <resources>
        <resource>
            <directory>src/main/resources</directory>
            <filtering>true</filtering>
        </resource>
    </resources>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-resources-plugin</artifactId>
            <configuration>
                <delimiters>
                    <delimiter>$</delimiter>
                </delimiters>
            </configuration>
        </plugin>
    </plugins>
</build>
```

配置信息

```yaml
management:
  info:
    env:
      enabled: true
  endpoints:
    web:
      exposure:
        include: "*"   # * 在yaml 文件属于关键字，所以需要加引号
info:
  app.name: springcloud-ytc
  company.name: www.example.com
  build.artifactId: $project.artifactId$
  build.version: $project.version$
```

服务发现

```java
@GetMapping("/discovery")
public Object discovery(){
    //获取微服务列表清单
    List<String> services = client.getServices();
    System.out.println("注册到springcloud中的列表清单:"+services);
    //获取某一个微服务具体的信息 参数: spring.application.name的名称
    List<ServiceInstance> instances = client.getInstances("springcloud-provider-dept");
    instances.forEach(ins -> {
        System.out.println(ins.getInstanceId());
        System.out.println(ins.getHost());
        System.out.println(ins.getPort());//端口
        System.out.println(ins.getUri());//uri
    });
    return instances;
}
```

**服务调用者**

导入依赖，对于EurekaServer来说服务提供这也是客户端

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

配置Eureka

```yaml
eureka:
  client:
  register-with-eureka: false
    service-url: # 监控页面地址
      defaultZone: http://127.0.0.1:7001/eureka/ # EurekaServer地址
  instance:
    instance-id: springcloud-provider-dept-8001
```

在启动类上添加注解

```java
@EnableEurekaClient
```

服务调用

```java
@Autowired
RestTemplate restTemplate;

@Autowired
private DiscoveryClient discoveryClient;

@RequestMapping("/consumer/dept/list")
public List<Dept> getDept() {
    // 服务提供者的spring.application.name的名称
    List<ServiceInstance> instances = discoveryClient.getInstances("SPRINGCLOUD-PROVIDER-DEPT");

    // 获取第一个服务信息
    ServiceInstance instanceInfo = instances.get(0);
    //获取ip
    String ip = instanceInfo.getHost();
    //获取port
    int port = instanceInfo.getPort();

    return restTemplate.getForObject("http://" + ip + ":" + port + "/dept/list", List.class);
}
```

### 2.2 EurekaServer集群

在`C:\Windows\System32\drivers\etc`中`hosts`配置域名映射模拟多台服务器

```bash
127.0.0.1 eureka7001.com
127.0.0.1 eureka7002.com
127.0.0.1 eureka7003.com
```

实际情况要添加ip注册

```yaml
eureka:
  instance:
    prefer-ip-address: true
```

互相注册实现集群

```yaml
server:
  port: 7001
eureka:
  instance:
    hostname: eureka7001.com # Eureka服务端实列名
  client:
    register-with-eureka: false # 是否注册自己
    fetch-registry: false # 是否从注册中心注册信息 false则表示自己为注册中心
    service-url: # 监控页面地址
      defaultZone: http://eureka7002.com:7002/eureka/,http://eureka7003.com:7003/eureka/
```

```yaml
server:
  port: 7002
eureka:
  instance:
    hostname: eureka7002.com # Eureka服务端实列名
  client:
    register-with-eureka: false # 是否注册自己
    fetch-registry: false # 是否从注册中心注册信息 false则表示自己为注册中心
    service-url: # 监控页面地址
      defaultZone: http://eureka7001.com:7001/eureka/,http://eureka7003.com:7003/eureka/
```

```yaml
server:
  port: 7003
eureka:
  instance:
    hostname: eureka7003.com # Eureka服务端实列名
  client:
    register-with-eureka: false # 是否注册自己
    fetch-registry: false # 是否从注册中心注册信息 false则表示自己为注册中心
    service-url: # 监控页面地址
      defaultZone: http://eureka7001.com:7001/eureka/,http://eureka7002.com:7002/eureka/
```

**服务提供者与服务调用者**

注册所有地址

```yaml
eureka:
  client:
    service-url: # 监控页面地址
      defaultZone: http://eureka7001.com:7001/eureka/,http://eureka7002.com:7002/eureka/,http://eureka7003.com:7003/eureka/
  instance:
    instance-id: springcloud-provider-dept-8001
```

```yaml
eureka:
  client:
  	register-with-eureka: false
    service-url: # 监控页面地址
      defaultZone: http://eureka7001.com:7001/eureka/,http://eureka7002.com:7002/eureka/,http://eureka7003.com:7003/eureka/
  instance:
    instance-id: springcloud-consumer-dept-8080
```

## 3. LoadBalancer

负载均衡组件，微服务名字调用

3.0.0+版本的Eureka自带LoadBalancer

给Rest的配置添加注解`@LoadBalanced`

```java
@Configuration
public class ConfigBean {
    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
```

调用

```java
@RestController
public class DeptConsumerController {
    @Autowired
    RestTemplate restTemplate;

    private static final String REST_URL_PREFIX = "http://SPRINGCLOUD-PROVIDER-DEPT";

    @RequestMapping("/consumer/dept/add")
    public boolean addDept(Dept dept) {
        return restTemplate.postForObject(REST_URL_PREFIX+"/dept/add", dept, Boolean.class);
    }

    @RequestMapping("/consumer/dept/get/{id}")
    public Dept getDept(@PathVariable("id") Long id) {
        return restTemplate.getForObject(REST_URL_PREFIX+"/dept/get/"+id, Dept.class);
    }

    @RequestMapping("/consumer/dept/list")
    public List<Dept> getDept() {
        return restTemplate.getForObject(REST_URL_PREFIX + "/dept/list", List.class);
    }
}
```

服务提供者集群模拟

```yaml
server:
  port: 8001
mybatis-plus:
  type-aliases-package: com.example.api.pojo
  mapper-locations: classpath:mapper/*.xml
spring:
  application:
    name: springcloud-provider-dept
  datasource:
    username: root
    password: Ytc19980211..
    url: jdbc:mysql://47.100.36.90:3306/db01?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.alibaba.druid.pool.DruidDataSource
  config:
    activate:
      on-profile: v1
eureka:
  client:
    service-url: # 监控页面地址
      defaultZone: http://eureka7001.com:7001/eureka/,http://eureka7002.com:7002/eureka/,http://eureka7003.com:7003/eureka/
  instance:
    instance-id: springcloud-provider-dept-8001
---
server:
  port: 8002
mybatis-plus:
  type-aliases-package: com.example.api.pojo
  mapper-locations: classpath:mapper/*.xml
spring:
  application:
    name: springcloud-provider-dept
  datasource:
    username: root
    password: Ytc19980211..
    url: jdbc:mysql://47.100.36.90:3306/db01?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.alibaba.druid.pool.DruidDataSource
  config:
    activate:
      on-profile: v2
eureka:
  client:
    service-url: # 监控页面地址
      defaultZone: http://eureka7001.com:7001/eureka/,http://eureka7002.com:7002/eureka/,http://eureka7003.com:7003/eureka/
  instance:
    instance-id: springcloud-provider-dept-8002
---
server:
  port: 8003
mybatis-plus:
  type-aliases-package: com.example.api.pojo
  mapper-locations: classpath:mapper/*.xml
spring:
  application:
    name: springcloud-provider-dept
  datasource:
    username: root
    password: Ytc19980211..
    url: jdbc:mysql://47.100.36.90:3306/db01?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.alibaba.druid.pool.DruidDataSource
  config:
    activate:
      on-profile: v3
eureka:
  client:
    service-url: # 监控页面地址
      defaultZone: http://eureka7001.com:7001/eureka/,http://eureka7002.com:7002/eureka/,http://eureka7003.com:7003/eureka/
  instance:
    instance-id: springcloud-provider-dept-8003
```

启动时使用v1、v2、v3启动

使用服务调用者调用接口会发现默认使用轮询

自定义负载均衡策略

LoadBalancer默认轮询，还有一个随机策略

![image-20220121130921178](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20220121130921178.png)

编写LoadBalancerConfig配置类更换策略，这个类不应该添加`@Configuration`注解

```java
public class LoadBalancerConfig {
    @Bean
    public ReactorServiceInstanceLoadBalancer reactorServiceInstanceLoadBalancer(Environment environment, LoadBalancerClientFactory loadBalancerClientFactory) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        //返回随机轮询负载均衡方式
        return new RandomLoadBalancer(loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class), name);
    }
}
```

在RestConfig类或者启动类上添加注解，name为serviceId，configuration为LoadBalancerConfig.class

```java
@LoadBalancerClient(name = "SPRINGCLOUD-PROVIDER-DEPT", configuration = LoadBalancerConfig.class)
```

自定义策略

创建自定义类实现ReactorServiceInstanceLoadBalancer接口，以下是复制的轮询策略为例

```java
public class CustomLoadBalancer implements ReactorServiceInstanceLoadBalancer {
    private static final Log log = LogFactory.getLog(CustomLoadBalancer.class);

    final AtomicInteger position;

    final String serviceId;

    ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

    /**
     * @param serviceInstanceListSupplierProvider a provider of
     * {@link ServiceInstanceListSupplier} that will be used to get available instances
     * @param serviceId id of the service for which to choose an instance
     */
    public CustomLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                                  String serviceId) {
        this(serviceInstanceListSupplierProvider, serviceId, new Random().nextInt(1000));
    }

    /**
     * @param serviceInstanceListSupplierProvider a provider of
     * {@link ServiceInstanceListSupplier} that will be used to get available instances
     * @param serviceId id of the service for which to choose an instance
     * @param seedPosition Round Robin element position marker
     */
    public CustomLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                                  String serviceId, int seedPosition) {
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
        this.position = new AtomicInteger(seedPosition);
    }

    @SuppressWarnings("rawtypes")
    @Override
    // see original
    // https://github.com/Netflix/ocelli/blob/master/ocelli-core/
    // src/main/java/netflix/ocelli/loadbalancer/RoundRobinLoadBalancer.java
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
                .getIfAvailable(NoopServiceInstanceListSupplier::new);
        return supplier.get(request).next()
                .map(serviceInstances -> processInstanceResponse(supplier, serviceInstances));
    }

    private Response<ServiceInstance> processInstanceResponse(ServiceInstanceListSupplier supplier,
                                                              List<ServiceInstance> serviceInstances) {
        Response<ServiceInstance> serviceInstanceResponse = getInstanceResponse(serviceInstances);
        if (supplier instanceof SelectedInstanceCallback && serviceInstanceResponse.hasServer()) {
            ((SelectedInstanceCallback) supplier).selectedServiceInstance(serviceInstanceResponse.getServer());
        }
        return serviceInstanceResponse;
    }

    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances) {
        if (instances.isEmpty()) {
            if (log.isWarnEnabled()) {
                log.warn("No servers available for service: " + serviceId);
            }
            return new EmptyResponse();
        }
        // TODO: enforce order?
        int pos = Math.abs(this.position.incrementAndGet());

        ServiceInstance instance = instances.get(pos % instances.size());

        return new DefaultResponse(instance);
    }
}

```

然后修改LoadBalancerConfig返回的方法

```java
@Bean
public ReactorServiceInstanceLoadBalancer reactorServiceInstanceLoadBalancer(Environment environment, LoadBalancerClientFactory loadBalancerClientFactory) {
    String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
    //返回随机轮询负载均衡方式
    return new CustomLoadBalancer(loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class), name);
}
```

在RestConfig类或者启动类上添加注解，name为serviceId，configuration为LoadBalancerConfig.class

```java
@LoadBalancerClient(name = "SPRINGCLOUD-PROVIDER-DEPT", configuration = LoadBalancerConfig.class)
```

## 4. Feign

负载均衡组件，接口和注解调用

给服务调用者和公用实体类包，或如果下一步service接口写在服务调用者只给服务调用者导入依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

Feign接口编写

```java
@Service
@FeignClient(value = "SPRINGCLOUD-PROVIDER-DEPT")
public interface DeptClient {
    @PostMapping("/dept/add")
    boolean addDept(Dept dept);

    @GetMapping("/dept/get/{id}")
    Dept getDept(@PathVariable("id") Long id);

    @GetMapping("/dept/list")
    List<Dept> getDept();
}
```

在启动类上添加注解

```java
@EnableFeignClients
```

服务调用者

```java
@RestController
public class DeptConsumerController {
    @Autowired
    DeptClient deptClient;

    @RequestMapping("/consumer/dept/add")
    public boolean addDept(Dept dept) {
        return deptClient.addDept(dept);
    }

    @RequestMapping("/consumer/dept/get/{id}")
    public Dept getDept(@PathVariable("id") Long id) {
        return deptClient.getDept(id);
    }

    @RequestMapping("/consumer/dept/list")
    public List<Dept> getDept() {
        return deptClient.getDept();
    }
}
```

## 5. Hystrix

熔断组件

### 5.1 熔断

服务端操作,某个服务超时或者异常就会引起服务熔断,类似于保险丝

服务提供者导入依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
    <version>2.2.10.RELEASE</version>
</dependency>
```

在启动类上添加注解

```java
@EnableHystrix
```

熔断备选，当抛出异常时触发熔断

```java
@RestController
public class DeptController {
    @Autowired
    DeptService deptService;

    // 抛异常
    @GetMapping("/dept/get/{id}")
    @HystrixCommand(fallbackMethod = "hytrixGetDept")
    public Dept getDept(@PathVariable("id") Long id) {
        System.out.println(deptService.getById(id));

        if (deptService.getById(id) == null) {
            throw new RuntimeException("id:"+id+" not exists");
        }

        return deptService.getById(id);
    }

    // 备选方案
    public Dept hytrixGetDept(@PathVariable("id") Long id) {
        return new Dept()
                .setDeptno(id)
                .setDname("not exists @Hystrix")
                .setDbSource("no db");
    }
}
```

### 5.2 降级

客户端操作,一般从整体的网站负载考虑,如果某些服务关闭后,服务将不在被调用,用户正常请求只是不走服务器,返回默认值

服务调用者导入依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
    <version>2.2.10.RELEASE</version>
</dependency>
```

在公用实体类包或者服务调用者下service添加回调实现类或回调工厂类，仅重写一个方法演示

```java
@Service
public class DeptClientServiceFallback implements DeptClientService {

    @Override
    public boolean addDept(Dept dept) {
        return false;
    }

    @Override
    public Dept getDept(Long id) {
        return new Dept().setDeptno(id).setDname("降级").setDbSource("no db");
    }

    @Override
    public List<Dept> getDept() {
        return null;
    }
}
// 或者
@Service
public class DeptClientServiceFallbackFactory implements FallbackFactory<DeptClientService> {

    @Override
    public DeptClientService create(Throwable cause) {
        return new DeptClientService() {
            @Override
            public boolean addDept(Dept dept) {
                return false;
            }

            @Override
            public Dept getDept(Long id) {
                return new Dept().setDeptno(id).setDname("降级").setDbSource("no db");
            }

            @Override
            public List<Dept> getDept() {
                return null;
            }
        };
    }
}
```

Feign接口修改注解

```java
@FeignClient(value = "SPRINGCLOUD-PROVIDER-DEPT", fallback = DeptClientServiceFallback.class)
// 或者
@FeignClient(value = "SPRINGCLOUD-PROVIDER-DEPT", fallbackFactory = DeptClientServiceFallbackFactory.class)
```

添加配置

```yaml
feign:
  circuitbreaker:
    enabled: true
```

### 5.3 Dashboard

导入依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
    <version>2.2.10.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
    <version>2.2.10.RELEASE</version>
</dependency>
```

配置

```yaml
server:
  port: 9001
spring:
  application:
    name: springcloud-consumer-hystrix-dashboard
eureka:
  client:
    service-url: # 监控页面地址
      defaultZone: http://eureka7001.com:7001/eureka/,http://eureka7002.com:7002/eureka/,http://eureka7003.com:7003/eureka/
  instance:
    instance-id: springcloud-consumer-hystrix-dashboard-9001
    prefer-ip-address: true
hystrix:
  dashboard:
    proxy-stream-allow-list: "*"
```

在启动类上添加注解

```java
@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
@EnableHystrixDashboard
```

在服务提供者的启动类或者配置类中添加Bean

```java
@Bean
public ServletRegistrationBean hystrixMetricsStreamServlet(){
    ServletRegistrationBean registrationBean = new ServletRegistrationBean(new HystrixMetricsStreamServlet());
    registrationBean.addUrlMappings("/actuator/hystrix.stream");
    return registrationBean;
}
```

http://localhost:9001/hystrix

输入http://localhost:8001/actuator/hystrix.stream监控

## 6. Gateway

导入依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```

配置

```yaml
# 直接转发uri
spring:
  cloud:
    gateway:
      routes:
        - id: springcloud-provider-dept
          uri: http://127.0.0.1:8001 # 直接向该地址转发
          predicates: # 断言 匹配条件 还有其他匹配类型 如时间 Before After
            - Path=/dept/** # 满足/dept/**会转发，例如****/dept/list会转发到http://127.0.0.1:8001/dept/list
            
# 在Eureka中拉取服务路径
spring:
  cloud:
    gateway:
      routes:
        - id: springcloud-provider-dept
          uri: lb://springcloud-provider-dept # 向Eureka拉去该服务路径
          predicates:
            - Path=/dept/** # 满足/dept/**会转发，例如****/dept/list会转发到http://127.0.0.1:8001/dept/list

# 重写重定向路径
spring:
  cloud:
    gateway:
      routes:
        - id: springcloud-provider-dept
          uri: lb://springcloud-provider-dept # 向Eureka拉去该服务路径
          predicates:
            - Path=/springcloud-provider-dept/**
          filters: # 过滤器 localhost:9527/springcloud-provider-dept/dept/list --> localhost:8001/dept/list
            - RewritePath=/springcloud-provider-dept/(?<segment>.*),/$\{segment} # 路径重写的过滤器
  
# Eureka微服务名转发
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true # 开启微服务名称转发 直接请求/微服务名/dept/list
          lower-case-service-id: true # 微服务名称已小写呈现
```

过滤器

```java
// 模拟Token认证
@Component
public class BlackListFilter  implements GlobalFilter, Ordered {
    /**
     * 进行过滤操作
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getQueryParams().getFirst("access-token");

		if (token ==  null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        // 放行到下一个过滤器
        return chain.filter(exchange);
    }

    /**
     * 主要是多个过滤器的时候，需要对过滤器排序，
     * 先经过哪个，后经过哪个,数值越小，这个优先级越高
     * @return order优先级
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
```

```java
// 模拟黑名单
@Component
public class BlackListFilter  implements GlobalFilter, Ordered {
    // 这里模拟下黑名单
    private static final List<String>  blackList=new ArrayList<>();
    static {

        blackList.add("127.0.0.1");// 模拟本机地址
    }
    /**
     * 进行过滤操作
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 获取请求
        ServerHttpRequest request = exchange.getRequest();

        // 获取响应
        ServerHttpResponse response = exchange.getResponse();
        // 获取客户端ip
        String host = request.getRemoteAddress().getHostString();
        System.out.println("remote host:"+host);
        if (blackList.contains(host)){  // 这个客户端ip在黑名单里面
            // 设置响应码
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            String data = "拒绝访问";
            DataBuffer wrap = response.bufferFactory().wrap(data.getBytes());
            HttpHeaders headers = response.getHeaders();
            // 设置中文乱码
            headers.add("content-type", "text/html;charset=utf-8");
            return response.writeWith(Mono.just(wrap));
        }
        // 放行到下一个过滤器
        return chain.filter(exchange);
    }

    /**
     * 主要是多个过滤器的时候，需要对过滤器排序，
     * 先经过哪个，后经过哪个,数值越小，这个优先级越高
     * @return order优先级
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
```

## 7. Config

配置中心

先创建一个git远程仓库创建xxx.yaml配置文件用来读取

可以集中修改配置文件，重启服务生效

### 7.1 直接读取uri

**ConfigServer**

导入依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

配置

```yaml
server:
  port: 3344
spring:
  application:
    name: springcloud-config-server
  cloud:
    config:
      server:
        git:
          uri: https://gitee.com/ytc214800722/springcloud-config.git
          username: 214800722@qq.com
          password: Ytc19980211..
```

在启动类上添加注解

```java
@EnableConfigServer
```

**ConfigClient**

导入依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bootstrap</artifactId>
    <version>3.0.2</version>
</dependency>
```

bootstrap.yaml配置

```yaml
spring:
  cloud:
    config:
      name: config-client # yaml文件名
      profile: dev # 环境
      label: master # 远程仓库分支
      uri: http://localhost:3344 # ConfigServer地址
```

### 7.2 从Eureka读取

可以搭建ConfigServer集群实现高可用

**ConfigServer**

导入依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

配置

```yaml
server:
  port: 3344
spring:
  application:
    name: springcloud-config-server
  cloud:
    config:
      server:
        git:
          uri: https://gitee.com/ytc214800722/springcloud-config.git
          username: 214800722@qq.com
          password: Ytc19980211..
eureka:
  client:
    service-url: # 监控页面地址
      defaultZone: http://eureka7001.com:7001/eureka/,http://eureka7002.com:7002/eureka/,http://eureka7003.com:7003/eureka/
  instance:
    instance-id: springcloud-config-server-3344
    prefer-ip-address: true
```

在启动类上添加注解

```java
@EnableConfigServer
```

**ConfigClient**

导入依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bootstrap</artifactId>
    <version>3.0.2</version>
</dependency>
```

bootstrap.yaml配置

```yaml
server:
  port: 3355
spring:
  cloud:
    config:
      name: config-client
      profile: dev
      label: master
      discovery:
        enabled: true
        service-id: springcloud-config-server
eureka:
  client:
    service-url: # 监控页面地址
      defaultZone: http://eureka7001.com:7001/eureka/,http://eureka7002.com:7002/eureka/,http://eureka7003.com:7003/eureka/
```

读取配置文件，重复属性会被读取的覆盖
