# MyBatisPlus 

https://baomidou.com/pages/24112f/

## 1. 添加Maven依赖

[MyBatisPlus官方文档](https://baomidou.com/)

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.4.3.4</version>
</dependency>
```

## 2. 配置连接数据库

```yaml
spring:
  datasource:
    username: root
    password: 88888888
    url: jdbc:mysql://47.100.36.90:3306/smbms?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    driver-class-name: com.mysql.cj.jdbc.Driver
```

## 3. 快速上手

创建pojo

创建mapper继承BaseMapper

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
```

基本的CRUD就完成了

如果不使用@MAPPER的话需要在启动项上加入

```java
@MapperScan("com.peter.mapper")
```



复杂CRUD需要自定义

## 4. 配置日志

```yaml
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

## 5. 主键策略

![image-20211223131652110](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211223131652110.png)

全局配置

```yaml
mybatis-plus:
  global-config:
    db-config:
      id-type: assign_id
```

## 6. 自动填充

数据库创建create_time、update_time字段

给实体类添加注解

```java
@TableField(fill = FieldFill.INSERT)
private Date createTime;
@TableField(fill = FieldFill.INSERT_UPDATE)
private Date updateTime;
```

自定义实现类MetaObjectHandler

```java
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill ....");
        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill ....");
        this.setFieldValByName("updateTime", new Date(), metaObject);
    }
}
```

## 7. 乐观锁  

Optimistic Concurrency Control

https://baomidou.com/pages/0d93c0/#_1-%E9%85%8D%E7%BD%AE%E6%8F%92%E4%BB%B6

数据库创建version字段，默认为1

给实体类添加注解

```java
@Version
private Integer version;
```

注册组件

```java
@Configuration
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return mybatisPlusInterceptor;
    }
}
```

## 8. 查询删除方法

普通查询

```java
userMapper.selectById(1);
```

批量查询

```java
userMapper.selectBatchIds(Arrays.asList(1,2,3));
```

条件查询

```java
Map<String,Object> map = new HashMap<>();
map.put("name","bbb");
map.put("age",22);
userMapper.selectByMap(map);
```

删除同理

## 9. 分页查询

注册组件

```java
@Configuration
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
    	return configuration -> configuration.setUseDeprecatedExecutor(false);
    }
}
```

在service层进行

两个参数是：当前页，页大小，从前端传递

执行完selectPage会自动返回给传入的page，通过getRecords取出

```java
Page<User> page = new Page<>(1, 5);
List<User> users = userMapper.selectPage(page, null).getRecords();
```

## 10. 逻辑删除

数据库创建deleted字段，默认为0

```yaml
mybatis-plus:
  global-config:
    db-config:
      logic-delete-field: deleted # 全局逻辑删除的实体字段名(since 3.3.0,配置后可以忽略不配置下个步骤)
      logic-delete-value: 1 # 逻辑已删除值(默认为 1)
      logic-not-delete-value: 0 # 逻辑未删除值(默认为 0)
```

给实体类添加注解

```java
@TableLogic
private Integer deleted
```

## 11. 条件构造器

参考官网

查询Wrapper

```java
QueryWrapper<User> wrapper = new QueryWrapper<>();
wrapper.like("name","sdf")
        .ge("age",18);

List<User> users = userMapper.selectList(wrapper);
users.forEach(System.out::println);
```

## 12. 通用枚举

配置枚举包扫描

```yaml
mybatis-plus:
    typeEnumsPackage: com.example.mybatisplusstudy.pojo.enums
```

```java
public enum AgeEnum implements IEnum<Integer> {
    ONE(1, "一岁"),
    TWO(2, "二岁"),
    THREE(3, "三岁");

    private int value;
    private String desc;

    @Override
    public Integer getValue() {
        return this.value;
    }
}
```

```java
public class User {
    /**
     * 名字
     * 数据库字段: name varchar(20)
     */
    private String name;

    /**
     * 年龄，IEnum接口的枚举处理
     * 数据库字段：age INT(3)
     */
    private AgeEnum age;
}
```

## 13. 代码生成器

https://baomidou.com/pages/779a6e/#%E4%BD%BF%E7%94%A8

导入依赖

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-generator</artifactId>
    <version>3.5.1</version>
</dependency>
<dependency>
    <groupId>org.apache.velocity</groupId>
    <artifactId>velocity-engine-core</artifactId>
    <version>2.3</version>
</dependency>
```

代码生成器，建一个有main的类运行

```java
FastAutoGenerator.create("jdbc:mysql://47.100.36.90:3306/smbms?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai","root","88888888")
    .globalConfig(builder -> {
        builder.outputDir(System.getProperty("user.dir")+"/src/main/java")
            .author("Tiancheng Yang")
            .disableOpenDir() // 禁止打开输出目录
            // .fileOverride() // 覆盖已生成文件
            .dateType(DateType.ONLY_DATE)
            .enableSwagger() // 开启swagger
            .commentDate("yyyy-MM-dd");
    })
    .packageConfig(builder -> {
        builder.parent("com.example")
            .moduleName("test")
            .entity("entity")
            .mapper("mapper")
            .xml("mapper")
            .service("service")
            .controller("controller")
            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, System.getProperty("user.dir")+"/src/main/resources/mapper"));
    })
    .strategyConfig(builder -> {
        builder.addInclude("user") // 要生成的表
            // .addExclude() // 排除生成的表 与include只能配置一项
            // .addTablePrefix("t_","ct_") // 过滤表前缀
            // .addTableSuffix() // 过滤表后缀
            // .addFieldPrefix("t_","ct_") // 过滤字段前缀
            // .addFieldSuffix() // 过滤字段后缀
            .entityBuilder()
            .enableLombok() // 开启lombok
            .idType(IdType.ASSIGN_ID) // 主键策略
            .naming(NamingStrategy.underline_to_camel) // 表命名转换
            .columnNaming(NamingStrategy.underline_to_camel) // 字段命名转换
            .versionColumnName("version") // 乐观锁
            .versionPropertyName("version")
            .logicDeleteColumnName("deleted") // 逻辑删除
            .logicDeletePropertyName("deleted")
            .addTableFills(new Property("createTime", FieldFill.INSERT))
            .addTableFills(new Property("updateTime", FieldFill.INSERT_UPDATE))
            .formatFileName("%s") // 格式化文件名称
            .mapperBuilder()
            .enableMapperAnnotation() // @Mapper注解
            .formatMapperFileName("%sMapper") // 格式化 mapper 文件名称
            .formatXmlFileName("%sMapper") // 格式化 xml 实现类文件名称
            .serviceBuilder()
            .formatServiceFileName("%sService") // 格式化 service 接口文件名称
            .formatServiceImplFileName("%sServiceImpl") // 格式化 service 实现类文件名称
            .controllerBuilder()
            .enableRestStyle() // 开启生成@RestController 控制器
            .enableHyphenStyle() // 开启驼峰转连字符
            .formatFileName("%sController"); // 格式化文件名称
    })
    // .templateEngine() // 模版引擎
    .execute();
```
