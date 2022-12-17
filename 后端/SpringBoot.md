# SpringBoot

## 1. 创建idea项目

[SpringBoot官方文档](https://spring.io/projects/spring-boot)

![image-20211217135958104](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211217135958104.png)

选择依赖，例如Spring Web、Mybatis、MySQL

![image-20211217140015043](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211217140015043.png)

修复spring-boot-maven-plugin 爆红问题，加入版本

```xml
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<version>${project.parent.version}</version>
			</plugin>
		</plugins>
```





Spring Boot DevTools可以热部署

## 2. 快速上手

运行启动类xxxApplication的main方法

在启动类的同级目录下编写

在resources下创建banner.txt，可以替换启动图像

## 3. application.yaml

将application.properties修改为application.yaml

key:空格value

```yaml
# 普通的key-value
name: aaa

# 对象
student:
  name: 啊啊啊
  age: 11
# 行内写法
students: {name: 啊啊啊, age: 11}

# 数组
pet:
  - dog
  - cat
  - pig
# 行内写法
pets: [dog, cat, pig]
```

yaml可以将对象注册到配置类中

可以在里面加占位符例如 `${random.uuid}`、`${random.int}`

## 4. 对象注入

1. yaml方式

```yaml
person:
  name: 啊啊啊
  age: 11
  dog:
    name: 嘿嘿嘿
    age: 22
```

```java
@Component
@ConfigurationProperties(prefix = "person")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    private String name;
    private int age;
    private Dog dog;
}
```

测试

```java
@Autowired
Person person;

@Test
void contextLoads() {
    System.out.println(person);
}
```

2. 传统注解方式

```java
@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dog {
    @Value("嘿嘿嘿")
    private String name;
    @Value("22")
    private int age;
}
```

测试

```java
@Autowired
Dog dog;

@Test
void contextLoads() {
    System.out.println(dog);
}
```

## 5. JSR303校验

对yaml注入进行校验

导入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

@Validated添加到类上

```java
@Component
@ConfigurationProperties(prefix = "person")
@Validated
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    @Email
    private String name;
    private int age;
    private Dog dog;
}
```

对属性添加需要的验证

![img](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/webp-16397269849022.webp)

![img](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/webp-16397269937244.webp)

## 6. 配置位置与多环境配置

优先级从1到4

1. file: ./config/

2. file: ./

3. classpath: /config/
3. classpath: /

通过在application.yaml配置切换环境，或者将不同环境拆成不同的yaml，命名为application-test.yaml、application-dev.yaml

```yaml
server:
  port: 8080
spring:
  profiles:
    active: dev
---
server:
  port: 8081
spring:
  config:
    activate:
      on-profile: test
---
server:
  port: 8082
spring:
  config:
    activate:
      on-profile: dev
```

可以配置查看什么类生效未生效

```yaml
debug: true
```

## 7. Web

### 7.1 引入静态资源包

1. 在resources下的META-INF/resources、resources、static、public文件夹中，有优先级r>s(默认)>p
2. [webjars依赖](https://www.webjars.org/)

3. ```yaml
   自定义路径
   spring:
     mvc:
       static-path-pattern: 
   ```

### 7.2 首页

在上面resources下的那些文件夹里创建index.html，可以直接访问到

### 7.3 Thymeleaf

导入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

在resources下的templates里的静态资源可以使用controller跳转

在html中加入约束

```html
<html xmlns:th="http://www.thymeleaf.org">
```

用th:元素接管html元素

${}: 变量

@{}: 链接

关闭缓存

```properties
spring.thymeleaf.cache=false
```

### 7.4 MVC

扩展SpringMVC

```java
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {
```

添加controller可以像原来那样创建controller类也可以在WebMvcConfigurer类中重写方法

```java
@Override
public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/").setViewName("index");
    registry.addViewController("/index.html").setViewName("index");
}
```

可以在yaml中配置formatter

```properties
spring:
  mvc:
    format:
      date: yyyy-MM-dd
```

### 7.5 国际化

在resources中创建i18n文件夹，idea下载Resource Bundle Editor插件

创建如下

![image-20211222105337643](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211222105337643.png)

在application.properties中配置

```properties
spring.messages.basename=i18n.login
```

thymeleaf接管

#{}: 消息

checkbox那块应该是在后面加`th:text="#{login.remember}"`

乱码就把setting里的file encoding改成utf-8

![image-20211222105556667](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211222105556667.png)

实现语言切换

前端传递参数

是/index.html

![image-20211222110435537](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211222110435537.png)

在config中自定义LocaleResolver的实现类

```java
public class MyLocaleResolver implements LocaleResolver {
    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String lang = request.getParameter("lang");
        Locale locale = Locale.getDefault();

        if (StringUtils.hasText(lang)) {
            String[] s = lang.split("_");
            locale = new Locale(s[0], s[1]);
        }

        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {

    }
}
```

然后将这个类在WebMvcConfigurer类中用@Bean注册

```java
@Bean
public LocaleResolver localeResolver() {
    return new MyLocaleResolver();
}
```

### 7.6 登录功能

![image-20211222140933361](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211222140933361.png)

```java
@Controller
public class LoginController {
    @RequestMapping("/user/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password, Model model) {
        if (!ObjectUtils.isEmpty(username) && "123456".equals(password)) {
            return "redirect:/main.html";
        } else {
            model.addAttribute("msg", "error");
            return "index";
        }
    }
}
```

在WebMvcConfigurer类中配置controller

```java
registry.addViewController("/main.html").setViewName("dashboard");
```

### 7.7 拦截器

在LoginController的方法中添加session参数，添加loginUser属性

```java
@RequestMapping("/user/login")
public String login(@RequestParam("username") String username, @RequestParam("password") String password, Model model, HttpSession session) {
    if (!ObjectUtils.isEmpty(username) && "123456".equals(password)) {
        session.setAttribute("loginUser", username);
        return "redirect:/main.html";
    } else {
        model.addAttribute("msg", "error");
        return "index";
    }
}
```

在interceptor中自定义HandlerInterceptor的实现类

```java
public class LoginHandlerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object loginUser = request.getSession().getAttribute("loginUser");

        if (loginUser == null) {
            request.setAttribute("msg", "login first");
            request.getRequestDispatcher("/index.html").forward(request, response);
            return false;
        } else {
            return true;
        }
    }
}
```

然后将这个类在WebMvcConfigurer类中注册

```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new LoginHandlerInterceptor())
        .addPathPatterns("/**")
        .excludePathPatterns("/index.html","/","/user/login","/css/*","/js/*","/img/*");
}
```

### 7.8 抽取fragment并插入

可以把公用的元素定义在一个commons.html中

![image-20211222150038971](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211222150038971.png)

把原来的删除

![image-20211222151250124](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211222151250124.png)

或

![image-20211222150052347](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211222150052347.png)

最好用replace

切换高亮

![image-20211222152429110](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211222152429110.png)

![image-20211222152449479](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211222152449479.png)

### 7.9 404

在templates下创建error文件夹，放入资源

### 7.10 注销

请求中携带HttpSession session参数

```java
session.invalidate();
return "redirect:/index.html";
```

## 8. 数据库

创建项目时选择，MySql依赖或者手动导入

### 8.1 MyBatis

导入依赖

```xml
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>2.2.0</version>
</dependency>
```

application.properties中配置别名和mapper位置

```properties
mybatis.type-aliases-package=com.example.boot04.pojo
mybatis.mapper-locations=classpath:mapper/*.xml
```

创建实体类

创建Mapper添加注解

```java
@Mapper
@Repository
public interface UserMapper {
    List<User> queryUsers();

    User queryUserById(@Param("id") int id);
}
```

在resources下创建mybatis/mapper/的mapper.xml

在service层的方法上添加@Transactional开启事务

手动回滚(try/catch)

```java
@Transactional(rollbackFor=Exception.class)
public Integer test(){
    try {
        throw new Exception();
    } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
    }
}
```

自动回滚

```java
@Transactional(rollbackFor=Exception.class) // 可以不加(rollbackFor=Exception.class)
public Integer test(){
    int i=1/0;//抛出异常，回滚
    return 0;
}
```

部分回滚

```java
@Override
@Transactional(rollbackFor = Exception.class)
public Object submitOrder (){  
    success();  
    //只回滚以下异常，
    Object savePoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
    try {  
        exception(); 
     } catch (Exception e) {  
        e.printStackTrace();     
        //手工回滚异常
        TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savePoint);
        return ApiReturnUtil.error();
     }  
    return ApiReturnUtil.success();
}
```

### 8.2 MyBatisPlus

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.4.3.4</version>
</dependency>
```

### 8.3 Druid

导入依赖

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-starter</artifactId>
    <version>1.2.8</version>
</dependency>
```

在application.yaml中配置，用starter不需要写配置类了

```yaml
spring:
  datasource:
    username: root
    password: 88888888
    url: jdbc:mysql://47.100.36.90:3306/mybatis?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.alibaba.druid.pool.DruidDataSource
    # Druid的其他属性配置
    druid:
      # 初始化时建立物理连接的个数
      initial-size: 5
      # 连接池的最小空闲数量
      min-idle: 5
      # 连接池最大连接数量
      max-active: 20
      # 获取连接时最大等待时间，单位毫秒
      max-wait: 60000
      # 申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
      test-while-idle: true
      # 既作为检测的间隔时间又作为testWhileIdel执行的依据
      time-between-eviction-runs-millis: 60000
      # 销毁线程时检测当前连接的最后活动时间和当前时间差大于该值时，关闭当前连接(配置连接在池中的最小生存时间)
      min-evictable-idle-time-millis: 30000
      # 用来检测数据库连接是否有效的sql 必须是一个查询语句(oracle中为 select 1 from dual)
      validation-query: select 'x'
      # 申请连接时会执行validationQuery检测连接是否有效,开启会降低性能,默认为true
      test-on-borrow: false
      # 归还连接时会执行validationQuery检测连接是否有效,开启会降低性能,默认为true
      test-on-return: false
      # 是否缓存preparedStatement, 也就是PSCache,PSCache对支持游标的数据库性能提升巨大，比如说oracle,在mysql下建议关闭。
      pool-prepared-statements: false
      # 置监控统计拦截的filters，去掉后监控界面sql无法统计，stat: 监控统计、slf4j/log4j2:日志记录、waLL: 防御sqL注入
      filters: stat,wall,slf4j
      # 要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true。在Druid中，不会存在Oracle下PSCache占用内存过多的问题，可以把这个数值配置大一些，比如说100
      max-pool-prepared-statement-per-connection-size: -1
      # 合并多个DruidDataSource的监控数据
      use-global-data-source-stat: true
      # 通过connectionProperties属性来打开mergeSql功能；慢SQL记录
      connection-properties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000
      web-stat-filter:
        # 是否启用StatFilter默认值true
        enabled: true
        # 添加过滤规则
        url-pattern: /*
        # 忽略过滤的格式
        exclusions: /druid/*,*.js,*.gif,*.jpg,*.png,*.css,*.ico
      stat-view-servlet:
        # 是否启用StatViewServlet默认值true
        enabled: true
        # 访问路径为/druid时，跳转到StatViewServlet
        url-pattern: /druid/*
        # 是否能够重置数据
        reset-enable: false
        # 需要账号密码才能访问控制台，默认为root
        login-username: druid
        login-password: druid
        # IP白名单
        allow: 127.0.0.1
        # IP黑名单（共同存在时，deny优先于allow）
        deny:
```

日志使用log4j2需要导入并修改web依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <!-- 排除掉logging，不使用logback，改用log4j2 -->
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-log4j2</artifactId>
</dependency>
```

后台监控 http://localhost:8080/druid/login.html

## 9. 安全

### 9.1 SpringSecurity

创建项目时选择或者手动导入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

编写一个Controller用来测试

启动时控制台会生成一个随机密码，默认用户名为user

![image-20220124100150787](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20220124100150787.png)

访问http://localhost:8080会重定向到http://localhost:8080/login

![image-20220124100231958](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20220124100231958.png)

使用application.yaml定义用户名和密码

```yaml
spring:
  security:
    user:
      name: root
      password: root
```

配置指定用户名密码

在config中自定义类继承WebSecurityConfigurerAdapter，并添加@Configuration注解

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication() // .passwordEncoder(new BCryptPasswordEncoder()) 添加密码加密
                .withUser("admin") // 添加用户admin
                .password("{noop}admin")  // 不设置密码加密，设置的话格式为 .password(new BCryptPasswordEncoder().encode("密码"))
                .roles("vip1", "vip2", "vip3")// 添加角色为vip1,vip2,vip3
                .and()
                .withUser("user1") // 添加用户user1
                .password("{noop}user1")
                .roles("vip1")
                .and()
                .withUser("user2") // 添加用户user2
                .password("{noop}user2")
                .roles("vip2")
                .and()
                .withUser("tmp")
                .password("{noop}tmp")
                .roles(); // tmp没有角色
    }

    @Override
    //请求授权的规则
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
            	.antMatchers("/").permitAll() //所有人都可以访问主页
                .antMatchers("/level1/**").hasRole("vip1") //添加/level1/** 下的所有请求只能由vip1角色才能访问
                .antMatchers("/level2/**").hasRole("vip2") //添加/level2/** 下的所有请求只能由vip2角色才能访问
                .antMatchers("/level3/**").hasRole("vip3") //添加/level3/** 下的所有请求只能由vip3角色才能访问
                // .hasAuthority("vip1") // 不会自动补ROLE_前缀
            	// .hasAnyRole("vip1","vip2") // vip1 or vip2 都可以访问
            	// .hasAnyAuthority("vip1","vip2") // 不会自动补ROLE_前缀
            	.anyRequest().authenticated() // 没有定义的请求，所有的角色都可以访问（tmp也可以）。
                .and()
                .formLogin(); //登录
        http.logout(); //注销
    }
}
```

通过数据库定义用户名和密码

建user表，Spring Security中配置hasRole，表中必须以ROLE_开头，而配置hasAuthority则不需要

![image-20220124113850838](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20220124113850838.png)

实际开发中需要用户表、角色表、权限表、用户角色关系表（用户角色一对多）、角色权限关系表（角色权限一对多），授权时拿到用户id之后联查用户角色关系表获取角色列表，再联查角色权限表获取权限列表，再联查权限表获取实际权限字段的list（distinct）最后给addStringPermission或者不用distinct用setStringPermission使用set集合去重。
如果是小项目可以省略角色表，使用用户表、权限表、用户权限关系表（角色权限一对多），同理。
详细搜索RBAC模型

导入MyBatisPlus，创建User实体类，创建UserMapper继承BaseMapper

在service包下创建CustomUserDetailService实现UserDetailsService接口，返回的User是Spring Security的User

> ```java
> @Service
> public class CustomUserDetailsService implements UserDetailsService {
>     @Autowired
>     UserMapper userMapper;
> 
>     @Override
>     public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
>         QueryWrapper<com.example.boot05.pojo.User> wrapper = new QueryWrapper<>();
>         wrapper.eq("username", username);
>         com.example.boot05.pojo.User user = userMapper.selectOne(wrapper);
> 
>         if (user == null) {
>             throw new UsernameNotFoundException("User " + username + " was not found in db");
>         }
> 
> 
>         List<GrantedAuthority> auths = AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRole());
> 
>         return new User(username, NoOpPasswordEncoder.getInstance().encode(user.getPassword()), auths);
> //        return new User(username, new BCryptPasswordEncoder().encode(user.getPassword()), auths);
>     }
> }
> ```
>

修改SecurityConfig，如果要使用BCryptPasswordEncoder加密，要把数据库内的内容也加密后存入

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)// 设置自定义的userDetailsService
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/level1/**").hasRole("vip1") //添加/level1/** 下的所有请求只能由vip1角色才能访问
                .antMatchers("/level2/**").hasRole("vip2") //添加/level2/** 下的所有请求只能由vip2角色才能访问
                .antMatchers("/level3/**").hasRole("vip3") //添加/level3/** 下的所有请求只能由vip3角色才能访问
                .anyRequest().authenticated() // 没有定义的请求，所有的角色都可以访问（tmp也可以）。
                .and()
                .formLogin();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();// 不使用加密算法保持密码
//        return new BCryptPasswordEncoder();
    }
}
```

自定义登录注销

```java
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
//                .antMatchers("/").permitAll()
                .antMatchers("/level1/**").hasRole("vip1") //添加/level1/** 下的所有请求只能由vip1角色才能访问
                .antMatchers("/level2/**").hasRole("vip2") //添加/level2/** 下的所有请求只能由vip2角色才能访问
                .antMatchers("/level3/**").hasRole("vip3") //添加/level3/** 下的所有请求只能由vip3角色才能访问
            	// .hasAuthority("vip1") // 不会自动补ROLE_前缀
            	// .hasAnyRole("vip1","vip2") // vip1 or vip2 都可以访问
            	// .hasAnyAuthority("vip1","vip2") // 不会自动补ROLE_前缀    
           		.anyRequest().authenticated() // 没有定义的请求，所有的角色都可以访问（tmp也可以）。
                .and()
                .formLogin() // 没有权限跳转登录页面 默认/login可以继续向后点自定义
                .permitAll() // 允许所有人访问login的页面
                .loginPage("/toLogin") // 登录页面
//                .usernameParameter() // 自定义前端登录表单字段名 用户名
//                .passwordParameter() // 自定义前端登录表单字段名 密码
                .loginProcessingUrl("/user/login") // 登录处理路径 与提交表单的路径相同
                .defaultSuccessUrl("/", true) // 成功后重定向
//                .successForwardUrl("/") // 成功后转发地址
//                .failureUrl("/") // 失败后重定向
//                .failureForwardUrl() // 失败后转发地址
                .and()
                .logout().logoutSuccessUrl("/") // 注销成功返回首页 默认/logout可以继续向后点自定义
                .and()
                .csrf().disable(); // 为了测试关闭csrf
    }
```

保持登录

```java
http.rememberMe();
// .rememberMeParameter() // 自定义前端登录表单字段名 保持登录
```

无权限403跳转页面

```java
http.exceptionHandling().accessDeniedPage("/error/403");
```

获取上下文

```java 
SecurityContext context = SecurityContextHolder.getContext();
// getAuthentication表示当前的认证情况，可以获取的对象有：
// UserDetails：获取用户信息，是否锁定等额外信息。
// Credentials：获取密码。
// isAuthenticated：获取是否已经认证过。
// Principal：获取用户，如果没有认证，那么就是用户名，如果认证了，返回UserDetails。
```

注解认证

在启动类上添加注解

```java
// securedEnabled对应@Secured，prePostEnabled对应prepost
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
```

在controller的方法上添加注解

```java
@Secured({"ROLE_vip1","ROLE_vip2"}) // 与数据库中的权限名相等
// 进入方法之前
@PreAuthorize("hasRole('vip1')") // 区别参考上面
@PreAuthorize("hasAnyRole('vip1','vip2')")
@PreAuthorize("hasAuthority('ROLE_vip1')")
@PreAuthorize("hasAnyAuthority('ROLE_vip1','ROLE_vip2')")
// 进入方法之后
@PostAuthorize()

// 过滤 没太大用 filterObject是内置对象
@PreFilter("filterObject%2==0")
@PostFilter("filterObject%2==0")
```

csrf防护开启

在登录表单Post请求中加入以下，前后端不分离有效

```html
<input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}">
```

**整合thymeleaf控制显示内容**

导入依赖

```xml
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity5</artifactId>
    <version>3.0.4.RELEASE</version>
</dependency>
```

在html中加入约束

```html
xmlns:sec="http://www.thymeleaf.org/extras/spring-security"
```

![image-20211225214943080](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211225214943080.png)

![image-20211225215806047](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211225215806047.png)

**前后端分离**

无状态支持权限控制

```java
http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

// ALWAYS – a session will always be created if one doesn’t already exist，没有session就创建。
// IF_REQUIRED – a session will be created only if required (default)，如果需要就创建（默认）。
// NEVER – the framework will never create a session itself but it will use one if it already exists
// STATELESS – no session will be created or used by Spring Security 不创建不使用session
```

**整合jwt**

导入依赖

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.2</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.2</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId> <!-- or jjwt-gson if Gson is preferred -->
    <version>0.11.2</version>
    <scope>runtime</scope>
</dependency>
```

编写jwt工具类

```java
@Component
public class JwtTokenUtil {
    // Token请求头
    public static final String TOKEN_HEADER = "Authorization";
    // Token前缀
    public static final String TOKEN_PREFIX = "Bearer ";

    private Key key; // 私钥
//    @Value("${jwt.secret}")
    private String secret = "kxf9sc94jv93nv76la9hxk3jg8x2450s"; // 密钥
//    @Value("${jwt.expiration}")
    private long expiration = 2*60*60*1000L; // 有效时间
//    @Value("${jwt.refreshExpiration}")
    private long refreshExpiration = 30*24*60*60*1000L; // 有效时间
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UserMapper userMapper;

    @PostConstruct
    public void init() {
        byte[] keyBytes;
        if (StringUtils.hasText(secret)) {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            this.key = Keys.hmacShaKeyFor(keyBytes); // 使用mac-sha算法的密钥
        }
    }

    public String createToken(Authentication authentication) {
        long now = (new Date()).getTime();
        Date validity;
        validity = new Date(now + this.expiration);
        return generateToken(authentication, validity);
    }

    public String createRefreshToken(Authentication authentication) {
        long now = (new Date()).getTime();
        Date validity;
        validity = new Date(now + this.refreshExpiration);
        return generateToken(authentication, validity);
    }

    private String generateToken(Authentication authentication, Date validity) {
        User user = (User) userDetailsService.loadUserByUsername(authentication.getName());
        Map<String ,Object> map = new HashMap<>();
        map.put("sub",authentication.getName());
        map.put("user",user);
        return Jwts.builder()
                .setClaims(map) // 添加body
                .signWith(key, SignatureAlgorithm.HS256) // 指定摘要算法
                .setExpiration(validity) // 设置有效时间
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token).getBody(); // 根据token获取body
        Map map = (Map) claims.get("user");
        String username = map.get("username").toString();
        String password = map.get("password").toString();

        QueryWrapper<com.example.boot05.pojo.User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        com.example.boot05.pojo.User user = userMapper.selectOne(wrapper);
        if (!user.getPassword().equals(password)) {
            return null;
        }
        Collection<? extends GrantedAuthority> authorities;
        User principal = (User) userDetailsService.loadUserByUsername(claims.getSubject());
        authorities = principal.getAuthorities();
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
}
```

编写jwt过滤器

```java
public class JwtFilter extends BasicAuthenticationFilter {
    private JwtTokenUtil jwtTokenUtil;

    public JwtFilter(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil) {
        super(authenticationManager);
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String tokenHeader = request.getHeader(jwtTokenUtil.TOKEN_HEADER);
        String refreshToken = request.getHeader("Refresh_Token");
        // 若请求头中没有Authorization信息 或是Authorization不以Bearer开头 则直接放行
        if (tokenHeader != null && refreshToken != null && tokenHeader.startsWith(jwtTokenUtil.TOKEN_PREFIX)) {
            // 去掉前缀 获取Token字符串
            String token = tokenHeader.replace(JwtTokenUtil.TOKEN_PREFIX, "");
            // 验证
            try {
                UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) jwtTokenUtil.getAuthentication(token);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (ExpiredJwtException e) {
                if ((e.getClaims().getSubject()).equals(jwtTokenUtil.getAuthentication(refreshToken).getName())) {
                    UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) jwtTokenUtil.getAuthentication(refreshToken);
                    String newToken = jwtTokenUtil.createToken(authentication);
                    response.setContentType("application/json;charset=utf-8");
                    response.setHeader("Access_Token", JwtTokenUtil.TOKEN_PREFIX + newToken);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        super.doFilterInternal(request, response, chain);
    }
}
```

配置Spring Security

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)// 设置自定义的userDetailsService
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); //设置无状态
        http
                .authorizeRequests()
                .anyRequest().authenticated() // 没有定义的请求，所有的角色都可以访问
                .and()
                .formLogin()
                .successHandler((request, response, authentication) -> {
                    String token = jwtTokenUtil.createToken(authentication);
                    String refreshToken = jwtTokenUtil.createRefreshToken(authentication);
                    response.setContentType("application/json;charset=utf-8");
                    response.setHeader("Access_Token", JwtTokenUtil.TOKEN_PREFIX + token);
                    response.setHeader("Refresh_Token", refreshToken);
                    response.getWriter().write(JSONUtils.toJSONString("登录成功"));
                })
                .failureHandler(((request, response, exception) -> {
                    String returnData="";
                    response.setContentType("application/json;charset=utf-8");
                    if (exception instanceof AccountExpiredException) {
                        returnData="账号过期";
                    } else if (exception instanceof BadCredentialsException) {
                        returnData="密码错误";
                    } else if (exception instanceof CredentialsExpiredException) {
                        returnData="密码过期";
                    } else if (exception instanceof DisabledException) {
                        returnData="账号不可用";
                    } else if (exception instanceof LockedException) {
                        returnData="账号锁定";
                    } else if (exception instanceof InternalAuthenticationServiceException) {
                        returnData="用户不存在";
                    } else {
                        returnData="未知异常";
                    }
                    response.getWriter().write(JSONUtils.toJSONString(returnData));
                }))
                .and()
                .logout()
                .logoutSuccessHandler((HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
                    response.setContentType("application/json;charset=utf-8");
                    response.getWriter().write(JSONUtils.toJSONString("登出成功"));
                })
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(((request, response, authException) -> {
                    response.setContentType("application/json;charset=utf-8");
                    response.getWriter().write(JSONUtils.toJSONString("未授权"));
                }))
                .and().addFilter(new JwtFilter(authenticationManager(), jwtTokenUtil));
        http.csrf().disable();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();// 不使用加密算法保持密码
//        return new BCryptPasswordEncoder();
    }
}
```

响应可以统一用ResponseData类

### 9.2 Shiro

```
Subject 用户
SecurityManager 管理所有用户
Realm 连接数据
```

导入依赖

```xml
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-spring-boot-web-starter</artifactId>
    <version>1.8.0</version>
</dependency>
```

在config中自定义类UserRealm继承AuthorizingRealm

```java
public class UserRealm extends AuthorizingRealm {
    //授权    
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }
	//认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        return null;
    }
}
```

在config中自定义类ShiroConfig添加@Configuration注解

```java
@Configuration
public class ShiroConfig {
    // 3. ShiroFilterFactoryBean
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager defaultWebSecurityManager) {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(defaultWebSecurityManager);
        return bean;
    }

    // 2. DefaultWebSecurityManager
    @Bean
    public DefaultWebSecurityManager defaultWebSecurityManager(UserRealm userRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm);
        return securityManager;
    }

    // 1. 创建Realm对象
    @Bean
    public UserRealm userRealm() {
        return new UserRealm();
    }
}
```

拦截和设置登录页面

```java
@Bean
public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager defaultWebSecurityManager) {
    ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
    bean.setSecurityManager(defaultWebSecurityManager);
    /*
    *   anon    无需认证
    *   authc   必须认证
    *   user    必须拥有记住我功能
    *   perms   拥有对某个资源的权限
    *   role    拥有某个角色的权限
    * */
    Map<String, String> filterMap = new HashMap<>();
    filterMap.put("/user/*","authc");
    bean.setFilterChainDefinitionMap(filterMap);

    // 设置登录请求
    bean.setLoginUrl("/login");

    return bean;
}
```

**认证**

controller接收封装账号密码

```java
@RequestMapping("/doLogin")
public String doLogin(String username, String password, Model model) {
    Subject subject = SecurityUtils.getSubject();
    UsernamePasswordToken token = new UsernamePasswordToken(username, password);
    try {
        subject.login(token);
        return "redirect:/index";
    } catch (UnknownAccountException e) {
        model.addAttribute("msg", "用户名错误");
        return "login";
    } catch (IncorrectCredentialsException e) {
        model.addAttribute("msg", "密码错误");
        return "login";
    } catch (AuthenticationException e) {
        e.printStackTrace();
        return "login";
    }
}
```

配置数据库连接

编写UserRealm中的doGetAuthenticationInfo方法

```java
@Override
protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
    UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;

    User user = userService.getById(token.getUsername());

    if (user == null) {
        return null;
    }

    return new SimpleAuthenticationInfo(user, user.getPassword(), this.getName());
}
```

**加密**

```java
// 内部加密一般用不到
// 输入的密码加密
HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
credentialsMatcher.setHashAlgorithmName("MD5"); // 加密算法
credentialsMatcher.setHashIterations(2); // 加密次数
this.setCredentialsMatcher(credentialsMatcher);
// 数据库密码加密
Object hash = new SimpleHash("MD5", user.getPassword(), "", 2);

return new SimpleAuthenticationInfo(user, hash, this.getName());
```

**授权**

在ShiroConfig的shiroFilterFactoryBean方法种设置权限

```java
Map<String, String> filterMap = new HashMap<>();
//授权页面要写在拦截前面
filterMap.put("/user/add","perms[user:add]");
filterMap.put("/user/*","authc");
bean.setFilterChainDefinitionMap(filterMap);

// 设置登录请求
bean.setLoginUrl("/login");
// 未授权页面
bean.setUnauthorizedUrl("/unauth");
```

在UserRealm中的doGetAuthorizationInfo方法

```java
@Override
protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
    SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
    
    Subject subject = SecurityUtils.getSubject();
    User user = (User) subject.getPrincipal();
    info.addStringPermission(user.getPerms());

    return info;
}
```

权限不能为null不然会空指针

**注销**

controller用默认的logout方法

```java
@RequestMapping("/logout")
public String logout() {
    Subject subject = SecurityUtils.getSubject();
    subject.logout();
    return "redirect:/index";
}
```

**整合thymeleaf控制显示内容**

导入依赖

```xml
<dependency>
    <groupId>com.github.theborakompanioni</groupId>
    <artifactId>thymeleaf-extras-shiro</artifactId>
    <version>2.1.0</version>
</dependency>
```

在ShiroConfig中配置

```java
@Bean
public ShiroDialect getShiroDialect() {
    return new ShiroDialect();
}
```

在html中加入约束

```html
xmlns:shiro="http://www.pollix.at/thymeleaf/shiro"
```

![image-20211226153003365](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211226153003365.png)

![image-20211226152205647](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211226152205647.png)

**保持登录**

```java
token.rememberMe(remember);
```

## 10. Swagger

导入依赖

```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-boot-starter</artifactId>
    <version>3.0.0</version>
</dependency>
```

用starter以来的话必须在启动类上添加注解

```java
@EnableWebMvc
@EnableOpenApi
```

或者在application.yaml上添加

```yaml
spring:
  mvc:
    pathmatch:
      matching-strategy: ant_path_matcher
```



在config中自定义类SwaggerConfig添加@Configuration注解

配置内不写东西也可以直接去swagger的页面

```java
@Configuration
public class SwaggerConfig {
    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                // .groupName("a") // 通过修改groupName和apis包路径分组 ui右上角选组 多个docket分组
                .enable(true) // 默认开启，是否启动swagger
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)) // 有@ApiOperation注解的方法接口
                // .apis(RequestHandlerSelectors.basePackage("com.example.boot06.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("标题")
                .description("描述")
                .contact(new Contact("name","localhost:8080","214800722@qq.com"))
                .version("1.0")
                .build();
    }
}
```

[Swagger UI 试一试这个](http://localhost:8080/swagger-ui/)

[Swagger UI 不行试试这个](http://localhost:8080/swagger-ui/index.html)

通过读取配置文件实现Swagger开关，在yaml中配合环境

```java
@Profile("dev")
@Bean
public Docket docket1() {
    return new Docket(DocumentationType.OAS_30)
            .apiInfo(apiInfo())
            // .groupName("a") // 通过修改groupName和apis包路径分组
            .enable(true) // 默认开启
            .select()
            .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)) // 有@ApiOperation注解的方法接口
            // .apis(RequestHandlerSelectors.basePackage("com.example.boot06.controller"))
            .paths(PathSelectors.any())
            .build();
}
@Profile("prod")
@Bean
public Docket docket2() {
    return new Docket(DocumentationType.OAS_30)
            .apiInfo(apiInfo())
            // .groupName("a") // 通过修改groupName和apis包路径分组
            .enable(true) // 默认开启
            .select()
            .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)) // 有@ApiOperation注解的方法接口
            // .apis(RequestHandlerSelectors.basePackage("com.example.boot06.controller"))
            .paths(PathSelectors.any())
            .build();
}
```

@Api：用在请求的类上，表示对类的说明
    tags="说明该类的作用，可以在UI界面上看到的注解"
    value="该参数没什么意义，在UI界面上也看不到，所以不需要配置"

@ApiOperation：用在请求的方法上，说明方法的用途、作用
    value="说明方法的用途、作用"
    notes="方法的备注说明"

@ApiImplicitParams：用在请求的方法上，表示一组参数说明
    @ApiImplicitParam：用在@ApiImplicitParams注解中，指定一个请求参数的各个方面
        name：参数名
        value：参数的汉字说明、解释
        required：参数是否必须传
        paramType：参数放在哪个地方
            · header --> 请求参数的获取：@RequestHeader
            · query --> 请求参数的获取：@RequestParam
            · path（用于restful接口）--> 请求参数的获取：@PathVariable
            · div（不常用）
            · form（不常用）    
        dataType：参数类型，默认String，其它值dataType="Integer"       
        defaultValue：参数的默认值

@ApiResponses：用在请求的方法上，表示一组响应
    @ApiResponse：用在@ApiResponses中，一般用于表达一个错误的响应信息
        code：数字，例如400
        message：信息，例如"请求参数没填好"
        response：抛出异常的类

@ApiModel：用于响应类上，表示一个返回响应数据的信息
            （这种一般用在post创建的时候，使用@RequestBody这样的场景，
            请求参数无法使用@ApiImplicitParam注解进行描述的时候）
    @ApiModelProperty：用在属性上，描述响应类的属性

## 11. 异步任务

service给需要异步的方法添加@Async注解

```java
@Service
public class AsyncService {
    @Async
    public void hello() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("处理中");
    }
}
```

在启动类上添加注解

```java
@EnableAsync
```

## 12. 邮件任务

导入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

在qq邮箱开启smtp

![image-20211227102216222](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211227102216222.png)

配置

```yaml
spring:
  mail:
    username: 214800722@qq.com
    password: sczzkleudlzgbgii
    host: smtp.qq.com
    # QQ邮箱需要开启加密验证
    properties: {mail.smtl.ssl.enable : true}
```

邮件发送

```java
@SpringBootTest
class Boot07ApplicationTests {
    @Autowired
    JavaMailSenderImpl mailSender;

    @Test
    void contextLoads() {
        // 简单邮件
        SimpleMailMessage message = new SimpleMailMessage();

        message.setSubject("主题主题主题主题主题主题");
        message.setText("文本文本文本文本文本文本");
        message.setTo("214800722@qq.com");
        message.setFrom("214800722@qq.com");

        mailSender.send(message);
    }

    @Test
    void contextLoads2() throws MessagingException {
        // 复杂邮件
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setSubject("主题主题主题主题主题主题");
        helper.setText("<p style='color: red'>文本文本文本文本文本文本</p>",true);

        // 附件，new file内写地址
        // helper.addAttachment("1.jpg", new File(""));

        helper.setTo("214800722@qq.com");
        helper.setFrom("214800722@qq.com");

        mailSender.send(message);
    }

}
```

可以封装方法

## 13. 定时任务

在启动类上添加注解

```java
@EnableScheduling
```

在需要执行定时任务的方法上添加注解和Cron表达式

```java
@Scheduled(cron = "0 * * * * 0-7")
public void hello() {
    System.out.println("hello");
}
```

![image-20211227153501715](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211227153501715.png)

![image-20211227153517454](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211227153517454.png)

![image-20211227153547441](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211227153547441.png)

## 14. AOP

导入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

在aspect包下创建切面

```java
@Component
@Aspect
public class TestAspect {
    @Pointcut("execution(* com.example.boot10.service.TestService.*(..))")
    public void pointCut() {}

    @Before(value = "pointCut()")
    public void before() {
        System.out.println("执行前权限检查");
    }

    @After(value = "pointCut()")
    public void after() {
        System.out.println("执行后资源释放");
    }

    @Around(value = "pointCut()")
    public void around(ProceedingJoinPoint jp) throws Throwable {
        System.out.println("around前模拟开启事务");
//        Signature signature = jp.getSignature();
//        System.out.println(signature);
        Object proceed = jp.proceed();
        System.out.println("around后模拟结束事务");
    }

    @AfterReturning(value = "pointCut()", returning = "returnVal")
    public void afterReturning(Object returnVal) {
        System.out.println(returnVal+"  ar模拟日志功能");
    }

    @AfterThrowing(value = "pointCut()", throwing = "er")
    public void afterThrowing(Throwable er) {
        System.out.println(er+"    修复");
    }
}
```

## 15. 热部署

导入依赖或创建项目时选择

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

配置

```yaml
spring:
  devtools:
    restart:
      enabled: true
      additional-paths: src/main/java
      exclude: WEB-INF/**
  thymeleaf:
    cache: false
```

idea中

![image-20211229094325726](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211229094325726.png)

![image-20211229094535587](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211229094535587.png)

## 16. RestFul风格

传统

```java
// http://localhost:8080/s02/h2?a=1&b=1
@RequestMapping("/h2")
public String hello2(int a, int b, Model model) {
    model.addAttribute("msg",a+b);
    return "hello";
}
```

RestFul

```java
// http://localhost:8080/s02/h3/1/2
@RequestMapping("/h3/{a}/{b}")
public String hello3(@PathVariable int a, @PathVariable int b, Model model) {
    model.addAttribute("msg",a+b);
    return "hello";
}
```

限制请求方法，GET、POST、DELETE、PUT

```java
// http://localhost:8080/s02/h4/1/2
@RequestMapping(value = "/h4/{a}/{b}", method = RequestMethod.GET)
// @GetMapping("/h4/{a}/{b}")
public String hello4(@PathVariable int a, @PathVariable int b, Model model) {
    model.addAttribute("msg",a+b);
    return "hello";
}
```

## 17. 转发和重定向

转发就是默认的return

```java
@RequestMapping("/h1")
public String hello(Model model) {
    model.addAttribute("msg","转发");
    return "hello";
}
```

重定向，不能访问WEB-INF下的静态资源，可以通过转发到其他controller

```java
@RequestMapping("/h1")
public String hello(Model model) {
    model.addAttribute("msg","转发");
    return "redirect:/index.jsp";
    // return "redirect:/h1";
}
```

## 18. 获取请求参数以及响应

1. 接收

同名，直接接收

```java
@RequestMapping("/h2")
public String hello2(int a, int b, Model model) {
    model.addAttribute("msg",a+b);
    return "hello";
}
```

不同名，通过注解改名

```java
@RequestMapping("/h2")
public String hello2(@RequestParam("a") int aa, int b, Model model) {
    model.addAttribute("msg",a+b);
    return "hello";
}
```

对象，直接接收对象，需要字段名一致

```java
@RequestMapping("/h2")
public String hello2(User user, Model model) {
    model.addAttribute("msg",user);
    return "hello";
}

// 如果传递是通过http调用接口请求，请求体一个对象、map、json
@RequestMapping("/h3")
public String hello3(@RequestBody TestObject testObject, Model model) {
    model.addAttribute("msg",a+b);
    return "hello";
}
```

2. 响应

ModelAndView

ModelMap

Model

## 19. 用JSON传递

前后端分离不走视图解析器，controller注解改为

```java
@RestController
```

或者在类上注解改为

```java
@ResponseBody
```

导入JSON转换包的依赖，Gson、jackson、fastjson

返回JSON字符串

统一响应格式

```java
@Data
public class ResponseData<T> implements Serializable {

    private String code;

    private String msg;

    private T data;


    public ResponseData(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResponseData(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResponseData(ResultEnums resultEnums) {
        this.code = resultEnums.getCode();
        this.msg = resultEnums.getMsg();
    }

    public ResponseData(ResultEnums resultEnums, T data) {
        this.code = resultEnums.getCode();
        this.msg = resultEnums.getMsg();
        this.data = data;
    }

    public ResponseData() {
    }
}
```

```java
public enum ResultEnums {
    SUCCESS("0000", "请求成功"),
    ERROR("1111", "请求失败"),
    SYSTEM_ERROR("1000", "系统异常"),
    BUSSINESS_ERROR("2001", "业务逻辑错误"),
    VERIFY_CODE_ERROR("2002", "业务参数错误"),
    PARAM_ERROR("2002", "业务参数错误");

    private String code;
    private String msg;

    ResultEnums(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
```

```java
public class ResponseDataUtil {

    /**
     * 带实体的统一返回
     *
     * @param data 实体
     * @param <T>  实体类型
     * @return
     */
    public static <T> ResponseData buildSuccess(T data) {
        return new ResponseData<T>(ResultEnums.SUCCESS, data);
    }

    public static ResponseData buildSuccess() {
        return new ResponseData(ResultEnums.SUCCESS);
    }

    public static ResponseData buildSuccess(String msg) {
        return new ResponseData(ResultEnums.SUCCESS.getCode(), msg);
    }

    public static ResponseData buildSuccess(String code, String msg) {
        return new ResponseData(code, msg);
    }

    public static <T> ResponseData buildSuccess(String code, String msg, T data) {
        return new ResponseData<T>(code, msg, data);
    }

    public static ResponseData buildSuccess(ResultEnums resultEnums) {
        return new ResponseData(resultEnums);
    }

    public static <T> ResponseData buildError(T data) {
        return new ResponseData<T>(ResultEnums.ERROR, data);
    }

    public static ResponseData buildError() {
        return new ResponseData(ResultEnums.ERROR);
    }

    public static ResponseData buildError(String msg) {
        return new ResponseData(ResultEnums.ERROR.getCode(), msg);
    }

    public static ResponseData buildError(String code, String msg) {
        return new ResponseData(code, msg);
    }

    public static <T> ResponseData buildError(String code, String msg, T data) {
        return new ResponseData<T>(code, msg, data);
    }

    public static ResponseData buildError(ResultEnums resultEnums) {
        return new ResponseData(resultEnums);
    }
}
```

## 20. 跨域

在controller上添加注解

```java
@CrossOrigin
```

## N. 问题解决

### N.1 yaml读取中文乱码

![image-20211217151830572](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211217151830572.png)
