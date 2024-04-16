# MyBatis

## 1. 添加Maven依赖

[MyBatis官方文档](https://mybatis.org/mybatis-3/zh/index.html)

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.28</version>
</dependency>
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.5.7</version>
</dependency>
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
    <scope>test</scope>
</dependency>
```

```xml
    <!--在build中配置依赖，防止资源导出失败问题-->
    <build>
        <resources>
            <resource>
                <directory>src/main/resources</directory>
                <includes>
                    <include>**/*.properties</include>
                    <include>**/*.xml</include>
                </includes>
                <filtering>true</filtering>
            </resource>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.properties</include>
                    <include>**/*.xml</include>
                </includes>
                <filtering>true</filtering>
            </resource>
        </resources>
    </build>
```



## 2. 创建mybatis-config.xml

driver用 com.mysql.cj.jdbc.Driver，这是最新的

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
  <environments default="development">
    <environment id="development">
      <transactionManager type="JDBC"/>
      <dataSource type="POOLED">
        <property name="driver" value="${driver}"/>
        <property name="url" value="${url}"/>
        <property name="username" value="${username}"/>
        <property name="password" value="${password}"/>
      </dataSource>
    </environment>
  </environments>
  <mappers>
    <mapper resource="org/mybatis/example/BlogMapper.xml"/>
  </mappers>
</configuration>
```

**参考[8.属性配置](#8. 属性配置)中的配置，引用db.properties**

或者

修改其中的数据库配置，例如，注意xml中`&`需要替换为`&amp;`

```xml
<property name="driver" value="com.mysql.cj.jdbc.Driver"/>
<property name="url" value="jdbc:mysql://47.100.36.90:3306/smbms?useUnicode=true&amp;characterEncoding=utf-8&amp;serverTimezone=Asia/Shanghai"/>
<property name="username" value="root"/>
<property name="password" value="88888888"/>
```

mappers参考[映射配置](#11. 映射配置)

## 3. 在utils包下定义工具类

MyBatisUtils.java

```java
package org.example.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class MybatisUtils {
    private static SqlSessionFactory sqlSessionFactory;

    static{
        try {
            //使用mybatis的第一步，获取SqlSessionFactory对象
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static SqlSession getSqlSession() {
        return sqlSessionFactory.openSession();
    }
}
```

**默认自动提交事务为关闭，酌情选择23行方法内容**

```java
public static SqlSession getSqlSession() {
    return sqlSessionFactory.openSession();			// 默认关闭
    return sqlSessionFactory.openSession(true);		// 开启
    return sqlSessionFactory.openSession(false);	// 关闭
}
```

## 4. 定义数据库实体类

![image-20211119231810651](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211119231810651.png)

可以使用[lombok](#16. lombok)快速创建实体类

## 5. 定义sql接口

![image-20211119235313339](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211119235313339-16373371980791.png)

## 6. 定义sql实现映射配置文件

可以定义在dao包下

也可以定义在resource文件夹下，**参考[11.映射配置](#11. 映射配置)** **[推荐使用]**

```xml
#{} 和 ${} 相当于替换符 和 字符串拼接的区别，后者可能会导致sql注入
```

![image-20211119235438508](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211119235438508.png)

`namespace`: 接口

`id`: 方法

`resultType`: 参数类型

`parameterType`: 返回类型

去setting--> language&framework --> sql dialets把里面的都选成mysql

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.example.dao.UserMapper">
    <select id="getUserList" resultType="org.example.pojo.User">
        select * from smbms_user
    </select>
</mapper>
```

配置mybatis-config.xml下的mappers标签

```xml
<mappers>
    <mapper resource="org/example/dao/UserMapper.xml"/>
</mappers>
```

不仅有`select`标签，还有`insert`、`update`、`delete`

**`#{}`和`${}`**

- 不论是单个参数，还是多个参数，一律都建议使用注解 `@Param("")`

- 能用 `#{}` 的地方就用 `#{}`，不用或少用 `${}`

- 表名作参数时，必须用 ` ${}`。如：`select * from ${tableName}`

- order by 时，必须用 `${}`。如：`select * from t_user order by ${columnName}`

- 使用 `${}` 时，要注意何时加或不加单引号，即 `${}` 和 `'${}'`

**多个参数解决方案**

1. 使用map作为参数

```java
int updateUser2(Map<String,Object> map);
```

```xml
<update id="updateUser2" parameterType="map">
    update smbms_user set userName = #{name}, userPassword = #{pwd} where id = #{id}
</update>
```

2. 使用参数默认值 arg0、arg1、arg2 … 或 param1、param2、param3 …

```java
int updateUser3(String name, String pwd, int id);
```

```xml
<update id="updateUser3">
    update smbms_user set userName = #{arg0}, userPassword = #{arg1} where id = #{arg2}
</update>
```

3. 使用注解 **[推荐使用]**

```java
int updateUser4(@Param("name") String name, @Param("pwd") String pwd, @Param("id") int id);
```

```xml
<update id="updateUser4" parameterType="map">
    update smbms_user set userName = #{name}, userPassword = #{pwd} where id = #{id}
</update>
```

**如果报错，可能是[resources资源导出失败](#N.1 resources资源导出失败(.properties,.xml))**

## 7. 映射实例

![image-20211120003832798](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211120003832798.png)

```java
try (SqlSession sqlSession = MybatisUtils.getSqlSession()) {
    UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
    // 你的应用逻辑代码
}
// try-with-resource代码块会自动关闭sqlsession
```

增删改需要手动提交事务，若开启自动提交事务，则不需要手动提交

```
sqlSession.commit();
```

## 8. 属性配置

mybatis-config.xml**标签需要按照顺序**

`properties`: 通过引用在resources中创建的db.properties来配置数据库连接信息

```xml
<!--可以直接自闭合了因为已经在db.properties中写好了-->
<properties resource="db.properties"/>

<properties resource="db.properties">
    <!--可以在中间加property但是不推荐-->
    <property name="username" value="root"/>
    <property name="password" value="88888888"/>
</properties>

```

```xml
<environments default="development">
    <environment id="development">
        <transactionManager type="JDBC"/>
        <dataSource type="POOLED">
            <property name="driver" value="${driver}"/>
            <property name="url" value="${url}"/>
            <property name="username" value="${username}"/>
            <property name="password" value="${password}"/>
        </dataSource>
    </environment>
</environments>
```



db.properties内的内容

```properties
driver=com.mysql.cj.jdbc.Driver
url=jdbc:mysql://localhost:3306/mybatis?useSSL=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
username=root
password=fangrunbo0606
```

`environment`: 可以配置多套，通过修改default值来改变环境

> `transactionManager`: JDBC | MANAGED 默认使用JDBC，Spring会直接覆盖这个配置

> `dataSource`: POOLED | UNPOOLED | JNDI 默认使用POOLED

## 9. 别名配置

mybatis-config.xml**标签需要按照顺序**

`typeAliases`:

可以给java实体类设置一个别名在xml中使用

```xml
<typeAliases>
    <typeAlias type="org.example.pojo.User" alias="user"/>
</typeAliases>
```

也可以直接扫描一个包，包下的实体类默认使用其首字母小写作为别名（比如说User这个class的别名就是user），如果有别名注解则使用注解值 **[推荐使用]**

```java
@Alias("user")
public class User {
```

```xml
    <typeAliases>
        <package name="com.peter.pojo"/>
    </typeAliases>
```

![image-20211120214141856](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211120214141856.png)

## 10. 其他配置

mybatis-config.xml**标签需要按照顺序**

`settings`:

>`cacheEnabled`: true | false 缓存
>
>`lazyLoadingEnabled`: true | false 懒加载
>
>`mapUnderscoreToCamelCase`: true | false  数据库中字段名A_COLUMN转java中属性名aColumn
>
>`logImpl`:  SLF4J | **LOG4J** | LOG4J2 | JDK_LOGGING | COMMONS_LOGGING | **STDOUT_LOGGING** | NO_LOGGING 日志

`typeHandlers`: 类型处理器

`objectFactory`: 对象工厂

`plugins`: mybatis-generator-core、mybatis-plus、通用mapper

`databaseIdProvider`: 数据库厂商标识

## 11. 映射配置

mybatis-config.xml**标签需要按照顺序**

`mappers`：

### 11.1 使用相对于类路径的资源引用

```xml
<!-- 使用相对于类路径的资源引用 -->
<mappers>
  <mapper resource="org/mybatis/builder/AuthorMapper.xml"/>
  <mapper resource="org/mybatis/builder/BlogMapper.xml"/>
  <mapper resource="org/mybatis/builder/PostMapper.xml"/>
</mappers>
```

### 11.2 使用映射器接口实现类的完全限定类名

如果把sql实现类放在resources目录下，需要创建对应的包，且需要接口与配置文件同名，否则这个方法将无法使用


```xml
<!-- 使用映射器接口实现类的完全限定类名 -->
<mappers>
  <mapper class="org.mybatis.builder.AuthorMapper"/>
  <mapper class="org.mybatis.builder.BlogMapper"/>
  <mapper class="org.mybatis.builder.PostMapper"/>
</mappers>
```

### 11.3 将包内的映射器接口实现全部注册为映射器

如果把sql实现类放在resources目录下，需要创建对应的包，且需要接口与配置文件同名，否则这个方法将无法使用 **[推荐使用]**

```xml
<!-- 将包内的映射器接口实现全部注册为映射器 -->
<mappers>
  <package name="org.mybatis.builder"/>
</mappers>
```

## 12. ResultMap结果集映射

**实体类属性名与数据库字段名不一致无法取出数据解决方案**

![image-20211120185351079](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211120185351079.png)

本质上是get/set方法名里的属性名与数据库字段名不一致

### 12.1 SQL结果起别名

```sql
select userPassword as Password from smbms_user where id = 1;
```

### 12.2 ResultMap

```xml
<resultMap id="UserMap" type="user">
    <result column="userPassword" property="Password"/>
</resultMap>

<select id="getUserById" resultMap="UserMap">
    select * from smbms_user where id = #{id}
</select>
```

- 定义resultMap标签id为命名、type为实体类名/别名
- 添加映射，column为数据库字段名，property为实体类属性名，一致的可以不添加
- sql标签上不选择resultType而改为resultMap

## 13. 日志

`logImpl`: SLF4J | **LOG4J** | LOG4J2 | JDK_LOGGING | COMMONS_LOGGING | **STDOUT_LOGGING** | NO_LOGGING

### 13.1 STDOUT_LOGGING

![image-20211120200200136](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211120200200136.png)

### 13.2 LOG4J

需要导包

```xml
<dependency>
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>1.2.17</version>
</dependency>
```

在resources下创建log4j.properties

需要修改就去百度

```properties
#将等级为DEBUG的日志信息输出到console和file这两个目的地，console和file的定义在下面的代码
log4j.rootLogger=DEBUG,console,file

#控制台输出的相关设置
log4j.appender.console = org.apache.log4j.ConsoleAppender
log4j.appender.console.Target = System.out
log4j.appender.console.Threshold=DEBUG
log4j.appender.console.layout = org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern=[%c]-%m%n

#文件输出的相关设置
log4j.appender.file = org.apache.log4j.RollingFileAppender
log4j.appender.file.File=./log/log.log
log4j.appender.file.MaxFileSize=10mb
log4j.appender.file.Threshold=DEBUG
log4j.appender.file.layout=org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionPattern=[%p][%d{yy-MM-dd}][%c]%m%n

#日志输出级别
log4j.logger.org.mybatis=DEBUG
log4j.logger.java.sql=DEBUG
log4j.logger.java.sql.Statement=DEBUG
log4j.logger.java.sql.ResultSet=DEBUG
log4j.logger.java.sql.PreparedStatement=DEBUG
```

在需要使用log4j的类中，导入包import org.apache.log4j.Logger;

在类中添加Logger属性，参数为当前类的class

```java
static Logger logger = Logger.getLogger(UserMapperTest.class);
```

自定义logger级别与信息

```java
logger.info("info:进入UserMapperTest");
logger.debug("debug:进入UserMapperTest");
logger.error("error:进入UserMapperTest");
```

## 14. 分页

### 14.1 limit

**[推荐使用]**

```sql
select * from smbms_user limit 0,5
-- 从0开始之后5个，不包括第0个
```

然后传入limit后面的2个参数进行分页

### 14.2 RowBounds

在java中实现

```java
RowBounds rowBounds = new RowBounds(0,5);
List<User> userList = sqlSession.selectList("org.example.dao.UserMapper.getUserByLimit",null,rowBounds);
```

### 14.3 分页插件

不常用，百度mybatis pagehelper

## 15. 注解

使用注解来映射简单语句会使代码显得更加简洁，但对于稍微复杂一点的语句，Java 注解不仅力不从心，还会让你本就复杂的 SQL 语句更加混乱不堪。 因此，如果你需要做一些很复杂的操作，最好用 XML 来映射语句。

在接口中的方法上面添加注解

```java
@Select("select * from smbms_user")
List<User> getUsers();

// @Param("id") 相当于给userId起了个别名，叫做id，然后上面sql语句执行就可以拿到userId了
// 有多个基本类型参数的话一定要加@Param()
@Select("select * from smbms_user where id = #{id}")
User getUserById(@Param("id") int userId);
```

然后在配置文件夹中mappers添加类或者整个包

insert、update、delete同理

## 16. lombok

添加Maven依赖

```xml
<dependencies>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.22</version>
    </dependency>
</dependencies>
```

安装idea的lombok插件

在实体类中定义私有属性，然后添加注解，主要使用以下几个：

`@Data`: getter setter equals hashCode toString

`@AllArgsConstructor`: 有参构造

`@NoArgsConstructor`: 无参构造

`@@Accessors(chain = true)`: 开启链式编程

![image-20211121153437167](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211121153437167.png)

## 17. 复杂查询

可以使用`id`标签给唯一标识提升性能

![image-20211121182742908](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211121182742908.png)

### 17.1 多对一

![image-20211121174058750](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211121174058750.png)

![image-20211121174125571](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211121174125571.png)

使用association标签

方式一：按照结果嵌套处理 **[推荐使用]**

result 和 id推荐使用result

```xml
<resultMap id="studentWithTeacher" type="Student">
    <id property="id" column="sid"/>
    <result property="name" column="sname"/>
    <association property="teacher" javaType="Teacher">
        <id property="id" column="tid"/>
        <result property="name" column="tname"/>
    </association>
</resultMap>

<select id="getStudent" resultMap="StudentWithTeacher">
    select s.id sid, s.name sname, t.id tid, t.name tname from student s
    left join teacher t on s.tid = t.id
</select>
```

`association`:

> `javaType`: java类型

方式二：按照查询嵌套处理

```xml
<resultMap id="studentWithTeacher" type="Student">
    <id property="id" column="id"/>
    <result property="name" column="name"/>
    <association property="teacher" column="tid" javaType="Teacher" select="getTeacher"/>
</resultMap>

<select id="getStudent" resultMap="studentWithTeacher">
    select * from student
</select>

<select id="getTeacher" resultType="Teacher">
    select * from teacher where id = #{tid}
</select>
```

`association`:

> `column`: 联查字段
>
> `select`: 联查id

### 17.2 一对多

![image-20211121174240097](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211121174240097.png)

![image-20211121174253848](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211121174253848.png)

使用collection标签

方式一：按照结果嵌套处理 **[推荐使用]**

```xml
<resultMap id="TeacherWithStudent" type="Teacher">
    <id property="id" column="tid"/>
    <result property="name" column="tname"/>
    <collection property="students" ofType="Student">
        <id property="id" column="sid"/>
        <result property="name" column="sname"/>
        <result property="tid" column="tid"/>
    </collection>
</resultMap>

<select id="getTeacher" resultMap="TeacherWithStudent">
    select t.id tid, t.name tname, s.id sid, s.name sname from teacher t
    left join student s on t.id = s.tid
</select>
```

`collection`:

> `ofType`: 集合中的java类型

方式二：按照查询嵌套处理

```xml
<resultMap id="TeacherWithStudent" type="Teacher">
    <id property="id" column="id"/>
    <result property="name" column="name"/>
    <collection property="students" column="id" javaType="ArrayList" ofType="Student" select="getStudent"/>
</resultMap>

<select id="getTeacher" resultMap="TeacherWithStudent">
    select * from teacher
</select>

<select id="getStudent" resultType="Student">
    select * from student where tid = #{tid}
</select>
```

`collection`:

> `column`: 联查字段
>
> `select`: 联查id
>
> `javaType`: 集合类型

## 18. 动态SQL

### 18.1 if

相当于java中的if

接口

```java
List<Blog> queryBlogIf(Map map);
```

映射配置文件

```xml
<select id="queryBlog" parameterType="map" resultType="Blog">
    select * from blog where 1=1
    <if test="title != null">
        and title = #{title}
    </if>
    <if test="author != null">
        and author = #{author}
    </if>
</select>
```

测试

```java
public void queryBlogIfTest() {
    try (SqlSession sqlSession = MybatisUtils.getSqlSession()) {
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);
        Map map = new HashMap<>();
        map.put("author", "啊啊啊");
        map.put("title", "Java");
        List<Blog> blogs = mapper.queryBlogIf(map);
        for (Blog blog : blogs) {
            System.out.println(blog);
        }
    }
}
```

### 18.2 choose、when、otherwise

相当于java中的if-elseif-else

接口

```java
List<Blog> queryBlogChoose(Map map);
```

映射配置文件

```xml
<select id="queryBlogChoose" parameterType="map" resultType="Blog">
    select * from blog where 1=1
    <choose>
        <when test="author != null">
            and author = #{author}
        </when>
        <when test="title != null">
            and title = #{title}
        </when>
        <otherwise>
            and views = #{views}
        </otherwise>
    </choose>
</select>
```

测试

```java
public void queryBlogChooseTest() {
    try (SqlSession sqlSession = MybatisUtils.getSqlSession()) {
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);
        Map map = new HashMap<>();
        map.put("author", "啊啊啊");
        map.put("title", "Java");
        map.put("views", 0);
        List<Blog> blogs = mapper.queryBlogChoose(map);
        for (Blog blog : blogs) {
            System.out.println(blog);
        }
    }
}
```

### 18.3 trim、where、set

使用where标签会在至少有一个条件的时候自动拼接where，如果只有一个条件会自动去掉and/or

```xml
<select id="queryBlogIf" parameterType="map" resultType="Blog">
    select * from blog
    <where>
        <if test="title != null">
            title = #{title}
        </if>
        <if test="author != null">
            and author = #{author}
        </if>
    </where>
</select>
```

where标签相当于trim如下

```xml
<trim prefix="WHERE" prefixOverrides="AND |OR ">
  ...
</trim>
```

使用set标签会自动去掉最后一个的逗号

```xml
<update id="updateBlog" parameterType="map">
    update blog
    <set>
        <if test="title != null">
            title = #{title},
        </if>
        <if test="author != null">
            author = #{author},
        </if>
    </set>
    where id = #{id}
</update>
```

set标签相当于trim如下

```xml
<trim prefix="SET" suffixOverrides=",">
  ...
</trim>
```

trim标签可以自定义标签，比如在insert into中，取出后缀的逗号

### 18.4 foreach

通常在in里

接口

```java
List<Blog> queryBlogForeach(Map map);
```

映射配置文件

```xml
<select id="queryBlogForeach" parameterType="map" resultType="Blog">
    select * from blog
    <where>
        <foreach collection="ids" item="id" open="id in (" separator="," close=")">
            #{id}
        </foreach>
    </where>
</select>


<select id="queryBlogForeach" parameterType="map" resultType="Blog">
    select * from blog
    <where>
        <foreach collection="ids" item="id" open="and (" separator="or" close=")">
           id = #{id}
        </foreach>
    </where>
</select>
```

测试

```java
public void queryBlogForeachTest() {
    try (SqlSession sqlSession = MybatisUtils.getSqlSession()) {
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);
        Map map = new HashMap<>();
        ArrayList<String> ids = new ArrayList<>();
        ids.add("75b2aa9dfa864139bad6c1f76673909f");
        ids.add("e541555de4fc42ebbc5ac6b99f2070c0");
        map.put("ids", ids);
        List<Blog> blogs = mapper.queryBlogForeach(map);
        for (Blog blog : blogs) {
            System.out.println(blog);
        }
    }
}
```

`collection`: map中的key或者list

`item`: 遍历名

`open`: 开头

`separator`: 分隔

`close`: 结尾

### 18.5 sql片段

提取片段，拿来复用

```xml
<sql id="aaa">
  ...
</sql>

<select id="bbb">
	<include refid="aaa"/>
</select>
```

- 最好基于单表来定义SQL片段
- 不要存在where标签

### 18.6 多数据库支持

如果配置了 databaseIdProvider，你就可以在动态代码中使用名为 “_databaseId” 的变量来为不同的数据库构建特定的语句。比如下面的例子

```xml
<insert id="insert">
  <selectKey keyProperty="id" resultType="int" order="BEFORE">
    <if test="_databaseId == 'oracle'">
      select seq_users.nextval from dual
    </if>
    <if test="_databaseId == 'db2'">
      select nextval for seq_users from sysibm.sysdummy1"
    </if>
  </selectKey>
  insert into users values (#{id}, #{name})
</insert>
```

## 19. 缓存

> 查询 ： 连接数据库，耗资源
>
> 一次查询的结果，给他暂存一个可以直接取到的地方 --> 内存：缓存
>
> 我们再次查询的相同数据的时候，直接走缓存，不走数据库了

1. 什么是缓存[Cache]？
   - 存在内存中的临时数据
   - 将用户经常查询的数据放在缓存（内存）中，用户去查询数据就不用从磁盘上（关系型数据库文件）查询，从缓存中查询，从而提高查询效率，解决了高并发系统的性能问题
2. 为什么使用缓存？
   - 减少和数据库的交互次数，减少系统开销，提高系统效率
3. 什么样的数据可以使用缓存？
   - 经常查询并且不经常改变的数据 【可以使用缓存】

- MyBatis包含一个非常强大的查询缓存特性，它可以非常方便的定制和配置缓存，缓存可以极大的提高查询效率。
- MyBatis系统中默认定义了两级缓存：一级缓存和二级缓存
  - 默认情况下，只有一级缓存开启（SqlSession级别的缓存，也称为本地缓存）
  - 二级缓存需要手动开启和配置，他是基于namespace级别的缓存。
  - 为了提高可扩展性，MyBatis定义了缓存接口Cache。我们可以通过实现Cache接口来定义二级缓存。

### 19.1 一级缓存

- 一级缓存也叫本地缓存：SqlSession
  - 与数据库同一次会话期间查询到的数据会放在本地缓存中
  - 以后如果需要获取相同的数据，直接从缓存中拿，没必要再去查询数据库
  - 默认开启

查询同一个只会执行一次sql

![image-20211121231830688](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211121231830688.png)

![image-20211121231848895](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211121231848895.png)

查询不同会执行多次sql

![image-20211121232127133](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211121232127133.png)

![image-20211121232137332](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211121232137332.png)

**缓存失效的情况：**

1. 查询不同的东西
2. 增删改操作，可能会改变原来的数据，所以必定会刷新缓存
3. 查询不同的Mapper.xml
4. 手动清理缓存

```java
sqlSession.clearCache();
```

### 19.2 二级缓存

- 二级缓存也叫全局缓存，一级缓存作用域太低了，所以诞生了二级缓存
- 基于namespace级别的缓存，一个名称空间，对应一个二级缓存，也就是同一个Mapper.xml级别
- 优先暂存在一级缓存，SqlSession提交或者关闭时，提交到二级换成

1. 开启全局缓存（默认为开启，在mybatis-config.xml中显式开启，可以不加）

```xml
<setting name="cacheEnabled" value="true"/>
```

2. 在Mapper.xml中使用缓存

实体类需要序列化，readOnly="true"可以不需要序列化

```xml
<cache/>

<cache
  eviction="FIFO"
  flushInterval="60000"
  size="512"
  readOnly="true"/>
```

- `LRU` – 最近最少使用：移除最长时间不被使用的对象。
- `FIFO` – 先进先出：按对象进入缓存的顺序来移除它们。

![image-20211121234914558](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211121234914558.png)

![image-20211121234925244](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211121234925244.png)

```xml
<select ... useCache="true"> <!--可以对单条开启或关闭-->
...
</select>
```

### 19.3 自定义缓存

1. 添加Maven依赖

```xml
<dependency>
    <groupId>org.mybatis.caches</groupId>
    <artifactId>mybatis-ehcache</artifactId>
    <version>1.2.1</version>
</dependency>
```

2. 在Mapper.xml中指定使用我们的ehcache缓存实现

```xml
<cache type="org.mybatis.caches.ehcache.EhcacheCache"/>
```

3. 在resources中创建ehcache.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:noNamespaceSchemaLocation="http://ehcache.org/ehcache.xsd"
        updateCheck="false">

    <diskStore path="./tmpdir/Tmp_EhCache"/>

    <defaultCache
            eternal="false"
            maxElementsInMemory="10000"
            overflowToDisk="false"
            diskPersistent="false"
            timeToIdleSeconds="1800"
            timeToLiveSeconds="259200"
            memoryStoreEvictionPolicy="LRU"/>

    <cache
            name="cloud_user"
            eternal="false"
            maxElementsInMemory="5000"
            overflowToDisk="false"
            diskPersistent="false"
            timeToIdleSeconds="1800"
            timeToLiveSeconds="1800"
            memoryStoreEvictionPolicy="LRU"/>
</ehcache>
```

## N. 问题解决

### N.1 resources资源导出失败(.properties,.xml)

在pom.xml中添加

```xml
<build>
    <resources>
        <resource>
            <directory>src/main/java</directory>
            <includes>
                <include>**/*.properties</include>
                <include>**/*.xml</include>
            </includes>
            <filtering>false</filtering>
        </resource>
        <resource>
            <directory>src/main/resources</directory>
            <includes>
                <include>**/*.properties</include>
                <include>**/*.xml</include>
            </includes>
            <filtering>false</filtering>
        </resource>
    </resources>
</build>
```
