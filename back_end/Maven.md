# Maven

## 1. 下载及配置

[Maven官网](https://maven.apache.org/what-is-maven.html)

[MavenRepository](https://mvnrepository.com/)

解压到非系统文件夹中，在系统文件夹下需要将仓库配置在其他路径下，会很乱

### 1.1 环境变量

解压完成之后配置环境变量

![image-20211013141554707](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211013141554707.png)

![image-20211013141646729](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211013141646729.png)

### 1.2 Maven配置文件

`Maven_path`替换为Maven根目录路径

打开`Maven_path\conf\settings.xml`

配置本地仓库路径

```xml
<localRepository>Maven_path\repository</localRepository>
```

![image-20211013142309085](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211013142309085.png)

配置镜像，在国内建议配置阿里云镜像，不配也行

```xml
<mirror>
    <id>nexus-aliyun</id>
    <mirrorOf>*,!jeecg,!jeecg-snapshots</mirrorOf>
    <name>Nexus aliyun</name>
    <url>http://maven.aliyun.com/nexus/content/groups/public</url> 
</mirror>
```

![image-20211013142535055](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211013142535055-16372923938671.png)

配置默认jdk1.8

```xml
<profile>
	<id>jdk-1.8</id>
    <activation>
        <activeByDefault>true</activeByDefault>
        <jdk>1.8</jdk>
    </activation>
    <properties>
        <maven.compiler.target>1.8</maven.compiler.target>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.compilerVersion>1.8</maven.compiler.compilerVersion>
    </properties>
</profile>
```

### 1.3 Maven在idea中的配置

旧版本idea，选择`File->Close Project`，选择齿轮，选择`Default Settings`

新版本idea，直接选择`File->New Projects Setup->Settings for New Projects`

![image-20211013143006662](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211013143006662.png)

配置Maven、Maven配置文件、Maven仓库的路径

![image-20211013143155066](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211013143155066.png)

## 2. Maven在idea中的注意

### 2.1 新建项目的常用archetype

普通项目

web项目

![image-20211013143545973](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211013143545973.png)

### 2.2 pom.xml配置文件

**修改过xml之后一定要刷新一下maven**

创建完毕之后删除无用配置，最终为

![image-20211120201732486](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211120201732486.png)

添加需要的依赖如

![image-20211013143928071](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211013143928071.png)

可以去https://mvnrepository.com/寻找，选择版本后有配置代码

![image-20211013144024153](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211013144024153.png)

### 2.3 创建项目环境

![image-20211013144242201](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211013144242201.png)

需要测试的话，src下还需要test，test中还需要java和resources

1. web.xml可以使用webapp骨架默认的
2. 也可以去idea模板中，根据版本复制，推荐4.0配合Tomcat 9

![image-20211013144733260](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211013144733260.png)

### 2.4 创建Maven子模块

![image-20211013145035563](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211013145035563.png)

创建空项目会自动带出`parent`标签，但是需要手动添加打包代码

```xml
<packaging>war</packaging>
```

将配置文件删除无用配置至如图

![image-20211013145303842](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211013145303842.png)

创建web项目需要手动添加`parent`标签，原因未知，可能是idea版本或者maven版本问题

## 3. Maven父模块控制依赖版本

```xml
<properties>
    <maven.compiler.source>8</maven.compiler.source>
    <maven.compiler.target>8</maven.compiler.target>
    <springcloud.version>2021.0.0</spring.version>
    <springboot.version>2.6.2</spring.version>
</properties>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${springcloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>${springboot.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

子项目直接添加依赖不需要版本号

## N. 问题解决

### N.1 resources资源导出失败(.properties,.xml)

在pom.xml中添加

```xml
<!--  在build中配置resources，来防止我们资源导出失败的问题  -->
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

### N.2 替换webapp骨架默认web.xml

`Maven_path`替换为Maven根目录路径

打开`Maven_path\repository\org\apache\maven\archetypes\maven-archetype-webapp`

选择1.4，没有1.4就选有的

![image-20211015093806752](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211015093806752.png)

打开`maven-archetype-webapp-1.4.jar`

![image-20211015093834139](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211015093834139.png)

打开`archetype-resources\src\main\webapp\WEB-INF`

![image-20211015094023824](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211015094023824.png)

替换web.xml文件

4.0

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">
</web-app>
```

