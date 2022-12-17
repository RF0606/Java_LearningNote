# Spring

Spring是一个轻量级的控制反转(IOC)和面向切面编程(AOP)的框架

## 1. 添加Maven依赖

[Spring官方文档](https://spring.io/projects/spring-framework)

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>5.3.13</version>
</dependency>

<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>5.3.22</version>
</dependency>

<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
    <version>5.3.22</version>
</dependency>
```

## 2. 创建applicationContext.xml

idea中右键New->XML Configuration File->Spring Config

或

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

</beans>
```

applicationContext.xml为总beans，多个beans取名随意beans.xml、beans1.xml、beans2.xml

## 3. 创建类

![image-20211123154621890](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211123154621890.png)

![image-20211122232906573](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211122232906573.png)

mysql、oracle实现类都是打印作为演示

![image-20211123154646124](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211123154646124.png)

## 4. 配置applicationContext.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="hello" class="org.example.pojo.Hello">
        <property name="str" value="Spring"/>
    </bean>
    
    <bean id="mysqlImpl" class="org.example.dao.UserDaoMysqlImpl"/>
	<bean id="oracleImpl" class="org.example.dao.UserDaoOracleImpl"/>
	<bean id="userServiceImp" class="org.example.service.UserServiceImpl">
		<property name="userDao" ref="mysqlImpl"/>
    </bean>
</beans>
```

`id`: 对象名

`class`: java类的完全限定名

`property`: 属性

`value`: 值

`ref`: 应用spring容器中创建好的对象

通过需要修改ref实现解耦，提高程序的可维护性

被spring的beans.xml托管需要有无参构造、set方法，如果没有无参构造需要使用[有参构造](#6. 有参构造)

## 5. 获取对象上下文

```java
public class MyTest {
    public static void main(String[] args) {
        // 可以一次读取多个beans.xml
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        Hello hello = (Hello) context.getBean("hello");
        Hello hello = context.getBean("hello", Hello.class);
        System.out.println(hello);

        UserService userService = (UserService) context.getBean("userServiceImpl");
        userService.getUser();
    }
}
```

getBeans之后会初始化所有注册的beans

## 6. 有参构造

### 6.1 参数名赋值

```xml
<bean id="user4" class="org.example.pojo.User">
    <constructor-arg name="name" value="啊啊啊"/>
</bean>
```

### 6.2 类型赋值

```xml
<bean id="user3" class="org.example.pojo.User">
    <constructor-arg type="java.lang.String" value="啊啊啊"/>
</bean>
```

### 6.3 下标赋值

```xml
<bean id="user2" class="org.example.pojo.User">
    <constructor-arg index="0" value="啊啊啊"/>
</bean>
```

## 7. Spring配置

### 7.1 bean配置

```xml
<bean id="user5" class="org.example.pojo.User" name="userName userName2,userName3;userName4">
    <constructor-arg name="name" value="啊啊啊"/>
</bean>
```

`id`: 对象名

`class`: java类的完全限定名

`name`: 别名，可以多个，可以用空格、逗号、分号隔开

### 7.2 import导入

一般用于团队开发，可将多个配置文件导入合并

```xml
<import resource="beans.xml"/>
```

### 7.3 alias别名

没啥用

```xml
<alias name="user" alias="userNew"/>
```

## 8. DI依赖注入

### 8.1 构造器注入

[有参构造](#6. 有参构造)

### 8.2 Set方式注入

创建复杂类

![image-20211123154745655](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211123154745655.png)

![image-20211123154805363](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211123154805363.png)

注入

```xml
<!--对应bean注入的-->
<bean id="address" class="org.example.pojo.Address">
        <property name="address" value="qwe"/>
    </bean>

    <bean id="student" class="org.example.pojo.Student">
        <!--普通注入-->
        <property name="name" value="啊啊啊"/>
        <!--bean注入-->
        <property name="address" ref="address"/>
        <!--array-->
        <property name="book">
            <array>
                <value>aaa</value>
                <value>bbb</value>
                <value>ccc</value>
            </array>
        </property>
        <!--list-->
        <property name="hobby">
            <list>
                <value>aaa</value>
                <value>bbb</value>
                <value>ccc</value>
                <ref bean="address"/>
            </list>
        </property>
        <!--map-->
        <property name="card">
            <map>
                <entry key="a" value="aa"/>
                <entry key="b" value-ref="address"/>
            </map>
        </property>
        <!--set-->
        <property name="game">
            <set>
                <value>aaa</value>
                <ref bean="address"/>
            </set>
        </property>
        <!--null-->
        <property name="wifi">
            <null></null>
        </property>
        <!--Properties-->
        <property name="info">
            <props>
                <prop key="administrator">administrator@example.org</prop>
                <prop key="support">support@example.org</prop>
                <prop key="development">development@example.org</prop>
            </props>
        </property>
    </bean>
```

### 8.3 p-namespace注入

简化简单注入，对应Set方式注入

```java
public class User {
    private String name;
    private int age;
}
```

导入约束`xmlns:p="http://www.springframework.org/schema/p"`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:p="http://www.springframework.org/schema/p"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="user" class="org.example.pojo.User" p:name="aa" p:age="12"/>
</beans>
```

`p:name`: 属性name

`p:name`-ref: 属性name引用

### 8.4 c-namespace注入

需要有参构造，对应构造器注入

需要在配置中导入约束`xmlns:c="http://www.springframework.org/schema/c"`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:c="http://www.springframework.org/schema/c"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="user" class="org.example.pojo.User" c:name="aa" c:age="12"/>
</beans>
```

`c:name`: 构造name

`c:name`-ref: 构造name引用

### 8.5 Bean作用域

默认单例

![image-20211123153833604](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211123153833604.png)

1. Singleton

单例模式

```xml
<bean id="user" class="org.example.pojo.User" scope="singleton"/>
```

2. Prototype

原型模式

```xml
<bean id="user" class="org.example.pojo.User" scope="prototype"/>
```

3. request、session、application、websocket

web中使用

## 9. Bean自动装配

**`autowire`**

```xml
<bean id="cat" class="org.example.pojo.Cat"/>
<bean id="dog" class="org.example.pojo.Dog"/>

<bean id="person" class="org.example.pojo.Person" autowire="...">
    <property name="name" value="啊啊啊"/>
</bean>

```

1. byName

会自动在容器上下文查找与对象set方法后面的值相同的bean的id

其中person这个类里有cat和dog的set方法，所以自动装配了，把cat和dog类自动set到了person里的cat和dog属性

必须同名

2. byType

会自动在容器上下文查找与对象set方法后面的类型相同的bean的id

必须同类型，且容器中不能有多个需要被装配的类型

## 10. 注解开发

导入约束

添加注解配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context https://www.springframework.org/schema/context/spring-context.xsd">

    <context:annotation-config/>
</beans>
```

### 10.1 bean注入

扫描指定包的@Component注解

```xml
<context:component-scan base-package="org.example"/>
```

```java
@Component
public class User {
    @Value("啊啊啊")
    public String name;
}
```

@Component注入类， 把类变成组件

@Value注入属性

@Component的衍生注解，功能相同，但应用于不同层

dao: @Repository

service: @Service

controller: @Controller

### 10.2 自动装配

@Autowired自动装配

先byType，后byName

```xml
<bean id="cat" class="org.example.pojo.Cat"/>
<bean id="dog" class="org.example.pojo.Dog"/>
<bean id="person" class="org.example.pojo.Person"/>
```

属性上，这样可以省略set方法不写了

```java
@Autowired
private Cat cat;
@Autowired
private Dog dog;
```

Set方法上(如果再set方法里写逻辑的话)

```java
@Autowired
public void setCat(Cat cat) {
    this.cat = cat;
}
@Autowired
public void setDog(Dog dog) {
    this.dog = dog;
}
```

required为false可以为null

```java
@Autowired(required = false)
```

@Qualifier指定装配

当存在多个同类型的id的时候，用qualifier指定装配

```java
<bean id="cat" class="org.example.pojo.Cat"/>
<bean id="cat111" class="org.example.pojo.Cat"/>
<bean id="cat11221" class="org.example.pojo.Cat"/>  
<bean id="dog" class="org.example.pojo.Dog"/>
<bean id="person" class="org.example.pojo.Person"/>
```

```java
@Autowired
@Qualifier(value = "cat111")
private Cat cat;
```

@Resource与@Autowired相反

先byName查找，后byType，所以要么bean id对应，要么该类型只有一个

```java
@Resource
private Cat cat;

@Resource(name = "cat1")
private Cat cat;
```

@Nullable可以为null

```java
@Autowired
@Nullable
private Cat cat;

@Autowired
public void setCat(@Nullable Cat cat) {
    this.cat = cat;
}
```

### 10.3 作用域

@Scope 
单例就写 `"singleton"`

```java
@Component
@Scope("...")
public class User {
    @Value("啊啊啊")
    public String name;
}
```

### 10.4 使用java配置Spring

```xml
<bean id="user" class="org.example.pojo.User"/>
```

相当于

```java
@Component
public class User {
    @Value("zxcvzx")
    private String name;
}
```

```java
@Configuration
@ComponentScan("org.example")
@Import(MyConfig2.class)
public class MyConfig {

    @Bean
    public User user() {
        return new User();
    }
}
```

```java
public static void main(String[] args) {
    ApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class);
    User user = context.getBean("user", User.class);
    System.out.println(user);
}
```

@Bean: 方法名=id return=class

@Component: 可以不加

@Import(): xml的导入

获取对线上下文用AnnotationConfigApplicationContext

## 11. AOP

导入依赖

```xml
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
    <version>1.9.7</version>
</dependency>
```

导入约束

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context https://www.springframework.org/schema/context/spring-context.xsd
                           http://www.springframework.org/schema/aop https://www.springframework.org/schema/aop/spring-aop.xsd">

    <context:annotation-config/>

</beans>
```

切入点

```java
public class UserServiceImpl implements UserService{
    @Override
    public void insert() {
        System.out.println("insert");
    }

    @Override
    public void delete() {
        System.out.println("delete");
    }

    @Override
    public void update() {
        System.out.println("update");
    }

    @Override
    public void select() {
        System.out.println("select");
    }
}
```

### 11.1 原生API

```java
public class BeforeLog implements MethodBeforeAdvice {
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println(target.getClass().getName()+"执行了"+method.getName());
    }
}
```

```java
public class AfterLog implements AfterReturningAdvice {
    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        System.out.println(target.getClass().getName()+"执行了"+method.getName()+"的方法，返回的结果是"+returnValue);
    }
}
```

```xml
<!--注册bean-->
<bean id="userService" class="org.example.service.UserServiceImpl"/>
<bean id="beforeLog" class="org.example.log.BeforeLog"/>
<bean id="afterLog" class="org.example.log.AfterLog"/>

<!--配置aop-->
<aop:config>
    <!--切入点 expression中execution(返回类型 完全限定方法名(参数))-->
    <aop:pointcut id="pointcut" expression="execution(* org.example.service.UserServiceImpl.*(..))"/>

    <aop:advisor advice-ref="beforeLog" pointcut-ref="pointcut"/>
    <aop:advisor advice-ref="afterLog" pointcut-ref="pointcut"/>
</aop:config>
```

### 11.2 自定义类

```java
public class PointCut {
    public void before() {
        System.out.println("前");
    }

    public void after() {
        System.out.println("后");
    }
}
```

```xml
<!--注册bean-->
    <bean id="userService" class="org.example.service.UserServiceImpl"/>

    <bean id="diy" class="org.example.diy.PointCut"/>
    <aop:config>
        <!--自定义切面 ref引用类id-->
        <aop:aspect ref="diy">
            <aop:pointcut id="point" expression="execution(* org.example.service.UserServiceImpl.*(..))"/>
            <aop:before method="before" pointcut-ref="point"/>
            <aop:after method="after" pointcut-ref="point"/>
        </aop:aspect>
    </aop:config>
```

### 11.3 注解

```xml
<context:component-scan base-package="org.example.diy"/>
<!--开启aop注解 默认(proxy-target-class="false") cglib(proxy-target-class="true")-->
<aop:aspectj-autoproxy/>

<!--注册bean-->
<bean id="userService" class="org.example.service.UserServiceImpl"/>
```

```java
@Component
@Aspect
public class AnnotationPointCut {

    @Pointcut("execution(* org.example.service.UserServiceImpl.*(..))")
    public void pointCut() {}

    @Before(value = "pointCut()")
    public void before() {
        System.out.println("前");
    }

    @After(value = "pointCut()")
    public void after() {
        System.out.println("后");
    }

    @Around("execution(* org.example.service.UserServiceImpl.*(..))")
    public void around(ProceedingJoinPoint jp) throws Throwable {
        System.out.println("a前");
        Signature signature = jp.getSignature();
        System.out.println(signature);
        Object proceed = jp.proceed();
        System.out.println("a后");
    }
}
```

a前-signature-前-方法-后-a后

可以定义一个方法实现切入点复用

## 12. 整合MyBatis

导入依赖

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
    <version>5.3.13</version>
</dependency>
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis-spring</artifactId>
    <version>2.0.6</version>
</dependency>
```

### 12.1 方式一

spring配置，可以单独放在spring-mapper.xml，导入applicationContext.xml

```xml
<!--dataSource-->
<bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
    <property name="driverClassName" value="com.mysql.cj.jdbc.Driver"/>
    <property name="url" value="jdbc:mysql://47.100.36.90:3306/smbms?useUnicode=true&amp;characterEncoding=utf-8&amp;serverTimezone=Asia/Shanghai"/>
    <property name="username" value="root"/>
    <property name="password" value="88888888"/>
</bean>
<!--sqlSessionFactory-->
<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
    <property name="dataSource" ref="dataSource"/>
    <!--注入mybatis配置文件，可以在spring配置用property配置，也可以去用mybatis配置-->
    <property name="configLocation" value="classpath:mybatis-config.xml"/>
    <!--
	写这行记得把线面的mappers删掉
	<property name="mapperLocations" value="classpath:com/peter/mapper/*.xml"/>
	-->
    
</bean>
<!--sqlSession配置代替工具类-->
<bean id="sqlSession" class="org.mybatis.spring.SqlSessionTemplate">
    <constructor-arg name="sqlSessionFactory" ref="sqlSessionFactory"/>
</bean>
```

mybatis配置

```xml
<typeAliases>
    <package name="org.example.pojo"/>
</typeAliases>
<mappers>
    <package name="org.example.mapper"/>
</mappers>
```

创建User实体类

创建UserMapper接口

创建UserMapper.xml

实现UserMapper接口

```java
public class UserMapperImpl implements UserMapper{
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public List<User> selectUser() {
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        return mapper.selectUser();
    }
}
```

注入到spring中

```xml
<!--mapper实现注入，可以用注解实现-->
<bean id="userMapper" class="org.example.mapper.UserMapperImpl">
    <property name="sqlSession" ref="sqlSession"/>
</bean>
```

测试直接调用对象上下文

```java
@Test
public void test1() {
    ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
    UserMapper userMapper = context.getBean("userMapper", UserMapper.class);
    for (User user : userMapper.selectUser()) {
        System.out.println(user);
    }
}
```

### 12.2 方式二

spring配置，可以单独放在spring-mapper.xml，导入applicationContext.xml

```xml
<!--dataSource-->
<bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
    <property name="driverClassName" value="com.mysql.cj.jdbc.Driver"/>
    <property name="url" value="jdbc:mysql://47.100.36.90:3306/smbms?useUnicode=true&amp;characterEncoding=utf-8&amp;serverTimezone=Asia/Shanghai"/>
    <property name="username" value="root"/>
    <property name="password" value="88888888"/>
</bean>
<!--sqlSessionFactory-->
<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
    <property name="dataSource" ref="dataSource"/>
    <!--注入mybatis配置文件，可以在spring配置用property配置，也可以去用mybatis配置-->
    <property name="configLocation" value="classpath:mybatis-config.xml"/>
</bean>
```

mybatis配置

```xml
<typeAliases>
    <package name="org.example.pojo"/>
</typeAliases>
<mappers>
    <package name="org.example.mapper"/>
</mappers>
```

创建User实体类

创建UserMapper接口

创建UserMapper.xml

实现UserMapper接口

```java
public class UserMapperImpl2 extends SqlSessionDaoSupport implements UserMapper{
    @Override
    //可以直接省去spring-dao里面的sqlSession配置
    public List<User> selectUser() {
        return getSqlSession().getMapper(UserMapper.class).selectUser();
    }
}
```

```xml
<!--mapper实现注入，可以用注解实现-->
<bean id="userMapper2" class="org.example.mapper.UserMapperImpl2">
    <property name="sqlSessionFactory" ref="sqlSessionFactory"/>
</bean>
```

测试直接调用对象上下文

```java
@Test
public void test2() {
    ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
    UserMapper userMapper = context.getBean("userMapper2", UserMapper.class);
    for (User user : userMapper.selectUser()) {
        System.out.println(user);
    }
}
```

## 13. 声明式事务

```xml
<!--配置声明式事务-->
<bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
    <constructor-arg name="dataSource" ref="dataSource"/>
</bean>
<!--配置事务通知，要导入tx约束-->
<tx:advice id="txAdice" transaction-manager="transactionManager">
    <tx:attributes>
        <!--propagation传播类型默认为REQUIRED-->
        <tx:method name="*" propagation="REQUIRED"/>
        <tx:method name="select" read-only="true"/>
    </tx:attributes>
</tx:advice>
<!--配置事务切入，要导入aop约束-->
<aop:config>
    <aop:pointcut id="txPointCut" expression="execution(* org.example.mapper.*.*(..))"/>
    <aop:advisor advice-ref="txAdice" pointcut-ref="txPointCut"/>
</aop:config>
```
