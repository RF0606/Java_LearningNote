# 1. 前期准备：

## 1.1 Sagrant 安装

(全部用最新版，包括virtual box)

1. 安装：https://developer.hashicorp.com/vagrant/downloads 64位系统的 (镜像仓库：https://app.vagrantup.com/centos/boxes/7)

2. cmd输入`vagrant` 测试是否安装成功

3. cmd初始化一个centos7：cmd输入`vagrant init centos/7`

4. 之后`vagrant up`, 等待最后不动了，ctrl + c退出就行

5. 使用 `vagrant ssh` 连接虚拟机（或者右键start 选headless start）

6. 虚拟机关机，就是右键`exit`然后选择中间的那个 (之后每次可以用`vagrant up`启动虚拟机)

7. 去`user/10602`中把`vargantfile`那个文件的 `config.vm.network "private_network", ip: "192.168.56.10"` 打开并把ip地址改成自己的

   虚拟机地址是 以太网2，本机地址是 网线
   
   切换到root 用户：`su root` 密码 `vagrant`

## 1.2 Docker 安装

网站：https://docs.docker.com/engine/install/centos/

开启开机自启动：`sudo systemctl enable docker`

## 1.3 阿里云镜像加速

不需要因为我人在国外

## 1.4 Mysql

1. 下载: `sudo docker pull mysql`

2. 启动mysql：

   ```shell
   docker run -p 3306:3306 --name mysql \
   -v /mydata/mysql/log:/var/log/mysql \
   -v /mydata/mysql/data:/var/lib/mysql \
   -v /mydata/mysql/conf:/etc/mysql/conf.d \
   -e MYSQL_ROOT_PASSWORD=root \
   -d mysql
   
   -v是挂载用的，把 /var/log/mysql 的挂载到linux下/mydata/mysql/log里
   ```

3. 进入容器：`docker exec -it 容器ID /bin/bash`(没有sudo, dnf和yum命令，但不影响)

4. 退出容器，在最外层输入`vi /mydata/mysql/conf/my.cnf`

5. 按 i，把下面的粘贴进去

   ```shell
   [client]
   default-character-set=utf8
   
   [mysql]
   default-character-set=utf8
   
   [mysqld]
   init_connect='SET collation_connection = utf8_unicode_ci'
   init_connect='SET NAMES utf8'
   character-set-server=utf8
   collation-server=utf8_unicode_ci
   skip-character-set-client-handshake
   skip-name-resolve
   ```

6. 按esc之后 `:wq` + 回车退出

7. `docker restart mysql`重启mysql

## 1.5 Redis

1. 下载`docker pull redis`

2. 创建instance并启动

   ```shell
   # 修改需要自定义的配置(docker-redis默认没有配置文件，自己在宿主机建立后挂载映射)
   mkdir -p /mydata/redis/conf
   touch /mydata/redis/conf/redis.conf
   
   # 启动redis服务运行容器
   docker run -p 6379:6379 --name redis -v /mydata/redis/data:/data \
   -v /mydata/redis/conf/redis.conf:/usr/local/etc/redis/redis.conf \
   -d redis /usr/local/bin/redis-server /usr/local/etc/redis/redis.conf
   
   # 添加配置
   vi /mydata/redis/conf/redis.conf
   在里面添加appendonly yes，之后退出并重启redis 
   
   # redis客户端
   docker exec -it redis redis-cli
   ```

## 1.6 开发环境

Java 1.8

maven 3.8.6(他用的3.6.1我先试试3.8.6行不行)

1. 修改maven 3.8.6内的conf内的setting.xml文件，镜像仓库mirror那块和profile那块(我没改我人不在CHN)

   ```xml
   <mirror>
       <id>nexus-aliyun</id>
       <mirrorOf>central</mirrorOf>
       <name>Nexus aliyun</name>
       <url>http://maven.aliyun.com/nexus/content/groups/public</url>    
   </mirror>
   
   	<profile>
         <id>jdk-1.8</id>
         <activation>
           <activeByDefault>true</activeByDefault>
           <jdk>1.8</jdk>
         </activation>
         <properties>
           <maven.compiler.source>1.8</maven.compiler.source>
           <maven.compiler.target>1.8</maven.compiler.target>
           <maven.compiler.compilerVersion>1.8</maven.compiler.compilerVersion>
         </properties>
   	</profile>
   ```

2. 修改idea里setting里的maven的相关配置

idea下载插件，mybatisPlus

下载vscode，并安装插件(webstorm应该也可以)

## 1.7 创建git项目

如果弄了ssh和git这些东西的话就不需要再弄了。我github是弄过的

先去github创建一个repo，ignore那块把maven选上，证书选择apache 2.0，并且额外创建一个develop的分支

## 1.8 idea拉项目和初始化

idea中file -> new -> project from version control -> github ->选上要选的repo 之后点clone

创建5个module

```java
商品服务mall-product
存储服务mall-ware
订单服务mall-order
优惠服务mall-coupon
用户服务mall-member
    
    
name/artifact都是mall-xxx
group 是com.peter.mall
package名字都是 com.peter.mall.xxx
都需要spring web和openfeign服务
    
每个模块添加springweb支持和openfeign依赖
```



在项目最外层创建pom文件

pom内容修改成：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.peter.mall</groupId>
    <artifactId>mall</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>mall</name>
    <description>商城-聚合服务</description>

    <packaging>pom</packaging>

    <modules>
        <module>mall-coupon</module>
        <module>mall-member</module>
        <module>mall-order</module>
        <module>mall-product</module>
        <module>mall-ware</module>
    </modules>

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.5.1</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
        </dependency>

    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>2.6.6</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>2021.0.1</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

</project>
```



将每个module 的pom.xml修改为：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <artifactId>mall</artifactId>
        <groupId>com.peter.mall</groupId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>

    <artifactId>mall-coupon</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>mall-coupon</name> 
    <description>mall-coupon</description>

    <properties>
        <java.version>1.8</java.version> <!--根据使用版本名字修改-->
        <spring-cloud.version>2021.0.1</spring-cloud.version> <!--根据使用版本名字修改-->
    </properties>
```

修改.gitignore

```
target/
pom.xml.tag
pom.xml.releaseBackup
pom.xml.versionsBackup
pom.xml.next
release.properties
dependency-reduced-pom.xml
buildNumber.properties
.mvn/timing.properties
# https://github.com/takari/maven-wrapper#usage-without-binary-jar
.mvn/wrapper/maven-wrapper.jar

**/mvnw
**/mvnw.cmd

**/.mvn
**/target

.idea

**/.gitignore
```

## 1.9 创建数据库

```
gulimall_oms
gulimall_pms
gulimall_sms
gulimall_ums
gulimall_wms
```

打开文件，复制到navicat里然后建数据

# 2. 快速开发：

## 2.1 renren开源文件导入

1. 克隆renren-fast， 从gitee或者github都可以

2. 删除里面的 .git 文件之后把整个文件夹拉入project内，跟其他5个module同级

3. 此时pom文件肯定会爆红，如果plugin爆红，就找到对应的maven dependency依赖，放进dependencies里面，比如这两个

   ```xml
   		<dependency>
   			<groupId>org.codehaus.mojo</groupId>
   			<artifactId>wagon-maven-plugin</artifactId>
   			<version>1.0</version>
   		</dependency>
   		<dependency>
   			<groupId>com.spotify</groupId>
   			<artifactId>docker-maven-plugin</artifactId>
   			<version>0.4.14</version>
   		</dependency>
   ```

   

4. 关于parents问题爆红，添加`<relativePath/>`标签

   ```xml
   	<modelVersion>4.0.0</modelVersion>
   	<groupId>io.renren</groupId>
   	<artifactId>renren-fast</artifactId>
   	<version>3.0.0</version>
   	<packaging>jar</packaging>
   	<description>renren-fast</description>
   
   	<parent>
   		<groupId>org.springframework.boot</groupId>
   		<artifactId>spring-boot-starter-parent</artifactId>
   		<version>2.6.6</version>
   		<relativePath/>
   	</parent>
   ```

5. 用renrenfast内的mysql.sql去虚拟机里的mysql建数据库`gulimall_admin`和表

6. 修改application_dev.yml文件

   ```yml
   替换成  	url: jdbc:mysql://192.168.56.10:3306/gulimall_admin?
              username: root
              password: root
   ```

7. 字符问题请修改lombox版本

   ```xml
   <lombok.version>1.18.20</lombok.version>
   ```

8. 启动，启动成功

9. renren-fast-vue直接拖给vscode，然后关闭vscode再用管理员权限打开

10. 如果没有下载node.js请去官网下载https://nodejs.org/en/，最好下载renren所需版本，如果在国内，下载以后请配置镜像

11. 之后ctrl + `启动控制台，控制台输入npm install，等待完成以后控制台输入npm run dev 切记这时候要保证renren那个项目再idea中是run起来的

12. 每次打开：用vscode打开renren-fast-vue后，在terminal输入`npm run dev`启动，

    ```bash
    启动使用：npm run dev
    默认登录账号：admin
    密码：admin
    ```

## 2.2 renren generator导入

1. git clone 人人generator到本地，并把它复制粘贴进 mall 项目下

2. pom文件爆红，这是3.8.1以上版本的maven的问题，问题是http被block了，此时去 `D:\apache-maven-3.8.6\conf` 下的 `settings.xml` 文件中把下面这段comment掉，然后保存重启idea就可以了

   ```xml
           <mirror>
               <id>maven-default-http-blocker</id>
               <mirrorOf>external:dummy:*</mirrorOf>
               <name>Pseudo repository to mirror external repositories initially using HTTP.</name>
               <url>http://0.0.0.0/</url>
               <blocked>true</blocked>
           </mirror>
   ```

3. 修改renren generator下的application.yml文件，把数据库换成想要generate的数据库，并且修改用户米密码：

   ```yml
       url: jdbc:mysql://192.168.56.10:3306/gulimall_pms?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai
       username: root
       password: root
       
   #springboot 2.6.x及以上版本需要添加：
   spring:
     main:
       allow-circular-references: true
     datasource:
     
   #爆红修改resources：
     jackson:
       time-zone: GMT+8
       date-format: yyyy-MM-dd HH:mm:ss
     web:
       resources:
         static-locations: classpath:/static/,classpath:/views/
   ```

4. 修改renren generator下的generator.properties文件，首先从product开始生成

   ```properties
   mainPath=com.peter
   package=com.peter.mall
   moduleName=product
   author=Runbo Fang
   email=fangrunbo0606@gmail.com
   #因为是生成product这个表，所以表前缀写的是pms里面的table的前缀，就是pms_
   tablePrefix=pms_
   ```

5. 修改renren generator的resources下的template里的controller.java.vm，删除最上面import的shiro的依赖，注释掉所有RequiresPermissions注解

6. run renren generator

7. 生成代码，下载并解压

8. 拿生成出来的main包替换掉 mall product的 main包

9. 在mall project下面创建mall-common的module， 导入下面的依赖, 并添加到mall.pom的项目结构内

   ```xml
   <dependencies>
       <dependency>
           <groupId>commons-lang</groupId>
           <artifactId>commons-lang</artifactId>
           <version>2.6</version>
       </dependency>
   </dependencies>
   
   <!--添加到mall.pom的项目结构内-->
       <modules>
           <module>mall-coupon</module>
           <module>mall-member</module>
           <module>mall-order</module>
           <module>mall-product</module>
           <module>mall-ware</module>
           <module>renren-fast</module>
           <module>renren-generator</module>
           <module>mall-common</module>
       </modules>
   ```

   

10. 根据mall-product里面爆红所显示需要的依赖包，然后慢慢改爆红问题，如果Constant报错，导入renren数据源就好了

11. 在mall-product的resources下创建application.yml文件，去配置数据源相关配置

    ```yml
    spring:
      datasource:
        username: root
        password: root
        url: jdbc:mysql://192.168.56.10:3306/gulimall_pms?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai
        driver-class-name: com.mysql.cj.jdbc.Driver
    
    # 指定resources下的mapper 路径，并且设置主键自增
    mybatis-plus:
      mapper-locations: classpath*:/mapper/**/*.xml
      global-config:
        db-config:
          id-type: auto
    
    ```

12. 在MallProductApplication启动项上添加mapperscan注解

    ```java
    @MapperScan("com.peter.mall.product.dao")
    ```

13. 可以去test里测试以下

14. 按照上述方法依次为每个微服务创造所需内容：

    ```yml
    1. 先修改renren generator里的application.yml
    	url里的数据库
    2. properties改：
    	moduleName = 
    	tablePrefix =
    3. 启动逆向生成工具
    	下载解压，把main直接粘贴过去
    4. 配置pom的依赖，依赖一下mall-common
    5. 在resources下创建application.yml,内容为：
    spring:
      datasource:
        username: root
        password: root
        url: jdbc:mysql://192.168.56.10:3306/'数据库名字'?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai
        driver-class-name: com.mysql.cj.jdbc.Driver
    
    # 指定resources下的mapper 路径，并且设置主键自增
    mybatis-plus:
      mapper-locations: classpath*:/mapper/**/*.xml
      global-config:
        db-config:
          id-type: auto
    ```





