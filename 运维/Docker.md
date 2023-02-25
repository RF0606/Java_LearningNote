# Docker

## 1. 安装及卸载

[Docker官网](https://www.docker.com/)

```bash
# 卸载docker旧版本
yum remove docker \
                  docker-client \
                  docker-client-latest \
                  docker-common \
                  docker-latest \
                  docker-latest-logrotate \
                  docker-logrotate \
                  docker-engine
                  
# 安装docker环境
yum install -y yum-utils
# 配置阿里云镜像
yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
# 安装Docker CE
yum -y install docker-ce docker-ce-cli containerd.io
# 启动docker
systemctl start docker
# 测试
docker version
docker run hello-world
docker images

# 查询所有docker相关
yum list installed | grep docker
# 卸载docker相关
yum -y remove docker-ce.x86_64 docker-ce-cli.x86_64 docker-ce-rootless-extras.x86_64 docker-scan-plugin.x86_64 containerd.io.x86_64
# 删除镜像容器
rm -rf /var/lib/docker
```

## 2. 阿里云镜像加速

https://cr.console.aliyun.com/cn-hangzhou/instances/mirrors

```bash
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["https://vf58ggq6.mirror.aliyuncs.com"]
}
EOF
sudo systemctl daemon-reload
sudo systemctl restart docker
```

## 3. 基本命令

### 3.1 帮助命令

[官方参考文档](https://docs.docker.com/engine/reference/run/)

```bash
docker -version # docker版本信息
docker info # docker的系统信息
docker --help # 帮助命令
docker 命令 --help
```

### 3.2 镜像命令

```bash
docker images # 查看镜像

[root@iz2vcbxtcjxxdepmmofk3hz home]# docker images
REPOSITORY    TAG       IMAGE ID       CREATED       SIZE
hello-world   latest    feb5d9fea6a5   4 weeks ago   13.3kB

# REPOSITORY  镜像仓库源
# TAG       镜像的标签
# IMAGE ID  镜像的ID
# CREATED    镜像创建时间 
# SIZE  镜像的大小

# 选项
[root@iz2vcbxtcjxxdepmmofk3hz home]# docker images --help

Usage:  docker images [OPTIONS] [REPOSITORY[:TAG]]

List images

Options:
  -a, --all             Show all images (default hides intermediate images) # 查看全部镜像
      --digests         Show digests
  -f, --filter filter   Filter output based on conditions provided
      --format string   Pretty-print images using a Go template
      --no-trunc        Don't truncate output
  -q, --quiet           Only show image IDs # 只显示镜像的ID
```

```bash
docker search # 搜索镜像

[root@iz2vcbxtcjxxdepmmofk3hz home]# docker search mysql
NAME                              DESCRIPTION                                     STARS     OFFICIAL   AUTOMATED
mysql                             MySQL is a widely used, open-source relation…   11587     [OK]       
mariadb                           MariaDB Server is a high performing open sou…   4407      [OK] 

# 选项
[root@iz2vcbxtcjxxdepmmofk3hz home]# docker search --help 

Usage:  docker search [OPTIONS] TERM

Search the Docker Hub for images

Options:
  -f, --filter filter   Filter output based on conditions provided
      --format string   Pretty-print search using a Go template
      --limit int       Max number of search results (default 25)
      --no-trunc        Don't truncate output
```

```bash
docker pull # 下载镜像

[root@iz2vcbxtcjxxdepmmofk3hz home]# docker pull mysql:5.7  # 指定版本下载 也可以不指定 docker pull mysql
5.7: Pulling from library/mysql
b380bbd43752: Extracting [===========================>                       ]  14.75MB/27.14MB
f23cbf2ecc5d: Download complete 
30cfc6c29c0a: Download complete 
b38609286cbe: Download complete 
8211d9e66cd6: Download complete 
2313f9eeca4a: Downloading [====>                                              ]  1.222MB/13.45MB
7eb487d00da0: Download complete 
a71aacf913e7: Download complete 
393153c555df: Waiting 
06628e2290d7: Waiting 
ff2ab8dac9ac: Waiting 
```

```bash
docker rmi -f 镜像ID # 根据ID删除进行

[root@iz2vcbxtcjxxdepmmofk3hz home]# docker images
REPOSITORY    TAG       IMAGE ID       CREATED       SIZE
mysql         5.7       938b57d64674   7 days ago    448MB
hello-world   latest    feb5d9fea6a5   4 weeks ago   13.3kB
[root@iz2vcbxtcjxxdepmmofk3hz home]# docker rmi -f 938b57d64674
Untagged: mysql:5.7
Untagged: mysql@sha256:2db8bfd2656b51ded5d938abcded8d32ec6181a9eae8dfc7ddf87a656ef97e97
Deleted: sha256:938b57d64674c4a123bf8bed384e5e057be77db934303b3023d9be331398b761

# 删除全部镜像
[root@iz2vcbxtcjxxdepmmofk3hz home]# docker rmi -f $(docker images -aq)
```

```bash
docker history 镜像ID # 查看镜像构建历史

# 查看mysql镜像是怎么创建的
[root@iz2vcbxtcjxxdepmmofk3hz ~]# docker images
REPOSITORY            TAG       IMAGE ID       CREATED        SIZE
mysql                 5.7       938b57d64674   2 weeks ago    448MB
centos                latest    5d0da3dc9764   7 weeks ago    231MB
portainer/portainer   latest    580c0e4e98b0   7 months ago   79.1MB
[root@iz2vcbxtcjxxdepmmofk3hz ~]# docker history 938b57d64674
IMAGE          CREATED       CREATED BY                                      SIZE      COMMENT
938b57d64674   2 weeks ago   /bin/sh -c #(nop)  CMD ["mysqld"]               0B        
<missing>      2 weeks ago   /bin/sh -c #(nop)  EXPOSE 3306 33060            0B        
<missing>      2 weeks ago   /bin/sh -c #(nop)  ENTRYPOINT ["docker-entry…   0B        
<missing>      2 weeks ago   /bin/sh -c ln -s usr/local/bin/docker-entryp…   0B        
<missing>      2 weeks ago   /bin/sh -c #(nop) COPY file:345a22fe55d3e678…   14.5kB    
<missing>      2 weeks ago   /bin/sh -c #(nop)  VOLUME [/var/lib/mysql]      0B        
<missing>      2 weeks ago   /bin/sh -c {   echo mysql-community-server m…   313MB     
<missing>      2 weeks ago   /bin/sh -c echo 'deb http://repo.mysql.com/a…   55B       
<missing>      2 weeks ago   /bin/sh -c #(nop)  ENV MYSQL_VERSION=5.7.36-…   0B        
<missing>      3 weeks ago   /bin/sh -c #(nop)  ENV MYSQL_MAJOR=5.7          0B        
<missing>      3 weeks ago   /bin/sh -c set -ex;  key='A4A9406876FCBD3C45…   1.84kB    
<missing>      3 weeks ago   /bin/sh -c apt-get update && apt-get install…   52.2MB    
<missing>      3 weeks ago   /bin/sh -c mkdir /docker-entrypoint-initdb.d    0B        
<missing>      3 weeks ago   /bin/sh -c set -eux;  savedAptMark="$(apt-ma…   4.17MB    
<missing>      3 weeks ago   /bin/sh -c #(nop)  ENV GOSU_VERSION=1.12        0B        
<missing>      3 weeks ago   /bin/sh -c apt-get update && apt-get install…   9.34MB    
<missing>      3 weeks ago   /bin/sh -c groupadd -r mysql && useradd -r -…   329kB     
<missing>      3 weeks ago   /bin/sh -c #(nop)  CMD ["bash"]                 0B        
<missing>      3 weeks ago   /bin/sh -c #(nop) ADD file:910392427fdf089bc…   69.2MB   
```

### 3.3 容器命令

```bash
docker run # 启动镜像

# 常用参数说明:
--name="name" # 容器名字 用于区分容器
-d # 后台方式运行
-it # 使用交互方式运行,进入容器查看内容
-p # 指定容器端口 -p 8080(主机端口):8080(容器端口)
-P # 随机指定端口

# 测试运行
[root@iz2vcbxtcjxxdepmmofk3hz home]# docker run -it centos /bin/bash # 启动并进入容器
[root@e06b1cf39b05 /]#                                                # 容器中
[root@e06b1cf39b05 /]# ls                                             # 查看容器内的centOS
bin  dev  etc  home  lib  lib64  lost+found  media  mnt  opt  proc  root  run  sbin  srv  sys  tmp  usr  var
[root@e06b1cf39b05 /]# exit                                           # 退出容器
exit

# 常见的坑
# docker run -d 镜像名 
# docker 容器使用后台运行,就必须要又一个前台进程, 如果docker发现没有提供服务,就会立刻停止
# ningx启动后,没有对外提供服务就会立刻停止
```

```bash
docker ps # 查看运行中的环境

# 常用参数说明:
-a # 运行中 和 曾经运行的容器
-n=? # 显示最近创建的容器
-q # 只显示容器编号

[root@iz2vcbxtcjxxdepmmofk3hz home]# docker ps # 查看运行中的环境
CONTAINER ID   IMAGE     COMMAND   CREATED   STATUS    PORTS     NAMES
[root@iz2vcbxtcjxxdepmmofk3hz home]# docker ps -a  # 查看曾经运行过的容器
CONTAINER ID   IMAGE          COMMAND       CREATED         STATUS                          PORTS     NAMES
e06b1cf39b05   centos         "/bin/bash"   3 minutes ago   Exited (0) About a minute ago             upbeat_moser
c6bae7b68f55   feb5d9fea6a5   "/hello"      4 days ago      Exited (0) 4 days ago                     eloquent_kilby
```

```bash
exit # 退出容器并停止容器
ctrl + P + Q # 退出容器不停止

docker rm 容器ID # 删除容器,但不难删除正在运行的容器 
docker rm -f # 强制删除
docker rm -f $(docker ps -aq) #删除所有

docker start 容器ID # 启动容器
docker restart 容器ID # 重启容器
docker stop 容器ID # 停止容器
docker kill 容器ID # 强制停止容器
```

### 3.4 其他命令

```bash
docker logs 容器ID # 查看日志

[root@iz2vcbxtcjxxdepmmofk3hz home]# docker logs --help

Usage:  docker logs [OPTIONS] CONTAINER

Fetch the logs of a container

Options:
      --details        Show extra details provided to logs
  -f, --follow         Follow log output # 查看全部
      --since string   Show logs since timestamp (e.g. 2013-01-02T13:23:37Z) or relative (e.g. 42m for 42 minutes)
  -n, --tail string    Number of lines to show from the end of the logs (default "all")
  -t, --timestamps     Show timestamps # 时间戳
      --until string   Show logs before a timestamp (e.g. 2013-01-02T13:23:37Z) or relative (e.g. 42m for 42 minutes)
```

```bash
docker top 容器ID # 查看容器中的进程信息

[root@iz2vcbxtcjxxdepmmofk3hz home]# docker run -it centos /bin/bash
[root@5519b9623114 /]# [root@iz2vcbxtcjxxdepmmofk3hz home]# 
[root@iz2vcbxtcjxxdepmmofk3hz home]# 
[root@iz2vcbxtcjxxdepmmofk3hz home]# docker ps
CONTAINER ID   IMAGE     COMMAND       CREATED          STATUS          PORTS     NAMES
5519b9623114   centos    "/bin/bash"   17 seconds ago   Up 15 seconds             compassionate_wiles
[root@iz2vcbxtcjxxdepmmofk3hz home]# docker top 5519b9623114
UID                 PID                 PPID                C                   STIME               TTY                 TIME                CMD
root                5085                5065                0                   15:28               pts/0               00:00:00            /bin/bash
```

```bash
docker inspect 容器ID # 查看容器的元数据

[root@iz2vcbxtcjxxdepmmofk3hz home]# docker inspect 5519b9623114
[
    {
        "Id": "5519b9623114fa82f4069c36978175f98ac4e1eecf45adbb84e86f4fbae5cdf4",
        "Created": "2021-10-28T07:28:19.781565297Z",
        "Path": "/bin/bash",
        "Args": [],
        "State": {
            "Status": "running",
            "Running": true,
            "Paused": false,
            "Restarting": false,
            "OOMKilled": false,
            "Dead": false,
            "Pid": 5085,
            "ExitCode": 0,
            "Error": "",
            "StartedAt": "2021-10-28T07:28:21.195048721Z",
            "FinishedAt": "0001-01-01T00:00:00Z"
        },
```

```bash
# 进入当前正在运行的容器
docker exec -it 容器ID /bin/bash
docker exec -it 容器ID 命令
docker attach 容器ID # 此命令进入当前容器正在运行的命令行 需要使用ctrl + P + Q退出
```

```bash
docker cp 容器ID:容器路径 目的主机路径 # 在容器中拷贝文件到主机

[root@iz2vcbxtcjxxdepmmofk3hz home]# docker cp a8a1efbeb3bb:/home/1.txt /home
[root@iz2vcbxtcjxxdepmmofk3hz home]# ls
1.txt
```

```bash
docker stats # 查看内存占用

[root@iz2vcbxtcjxxdepmmofk3hz home]# docker ps
CONTAINER ID   IMAGE     COMMAND       CREATED         STATUS         PORTS     NAMES
824c124be989   centos    "/bin/bash"   6 seconds ago   Up 6 seconds             pensive_ishizaka
[root@iz2vcbxtcjxxdepmmofk3hz home]# docker stats

CONTAINER ID   NAME               CPU %     MEM USAGE / LIMIT   MEM %     NET I/O   BLOCK I/O     PIDS
824c124be989   pensive_ishizaka   0.00%     856KiB / 1.696GiB   0.05%     0B / 0B   2.05MB / 0B   1
```

```bash
docker volume ls # 查看容器数据卷

# 查看卷
[root@iz2vcbxtcjxxdepmmofk3hz test]# docker volume ls
DRIVER    VOLUME NAME
local     8bcf170ad325fa5d0b8c83891dec1033246bd084d16883fe10b0efc3204049eb
local     cb3aa4e249e66160e94cdc63cfc866356b1c9e8a9d2caf74b2345f4f7fca41d1
```

## 4. 镜像

```bash
# 命令
docker commit 提交容器成为一个新的副本

# 命令和git原理类似
docker commit -m="提交描述信息" -a="作者" 容器ID 目标镜像名:[TAG]
```

```bash
# 下载tomcat镜像
docker pull tomcat
# 启动镜像
docker run -it -p 4329:8080 tomcat
# 重新打开一个窗口 进入容器
docker exec -it fdb06be50092 /bin/bash
# 进入webapps目录
cd webapps
# 拷贝webapps.dist目录下的所有文件 到webapps
cp -rf ../webapps.dist/*  ./
# 打包一个镜像
docker commit -a="tuyoooo" -m="cp webapps.dist" 63664c7d0482 tomcat02:1.0
# 查看镜像是否打包成功
[root@iz2vcbxtcjxxdepmmofk3hz ~]# docker images
REPOSITORY            TAG       IMAGE ID       CREATED         SIZE
tomcat02              1.0       84d658eadf85   6 seconds ago   684MB
tomcat                latest    b0e0b0a92cf9   11 days ago     680MB
centos                latest    5d0da3dc9764   6 weeks ago     231MB
portainer/portainer   latest    580c0e4e98b0   7 months ago    79.1MB
```

## 5. 容器数据卷  volume

挂载叫做 bind mount

```bash
# 方式一:直接使用命令来挂载 -v
docker run -it -v 主机目录:容器目录 镜像名 /bin/bash
# 挂载成功以后就类似Vue中的双向绑定,一边发生变化以后,另一边也跟着变化
# 可以通过卷的技术 将数据保存到本地

# 容器数据卷测试 mysql
docker pull mysql:5.7
# 运行mysql 并做数据挂载
# 注意mysql 运行要设置密码
docker run -d -p 4329:3306 -v /home/test/msyql/conf:/etc/mysql/conf.d \

> -v /home/test/mysql/data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=123456 --name some-mysql mysql:5.7

# 方式二:具名和匿名挂载 -v
# 匿名挂载 不指定主机路径
docker run -d -P --name nginx01 -v /etc/nginx nginx 

# 查看卷
[root@iz2vcbxtcjxxdepmmofk3hz test]# docker volume ls
DRIVER    VOLUME NAME
local     8bcf170ad325fa5d0b8c83891dec1033246bd084d16883fe10b0efc3204049eb
local     cb3aa4e249e66160e94cdc63cfc866356b1c9e8a9d2caf74b2345f4f7fca41d1
# 这里发现,这种匿名挂载,我们再-v 只写了容器内的路径 没有容器外的路径 所以名称自动生成的

# 具名挂载 通过 -v 卷名:容器内路径
docker run -d -P --name nginx01 -v nginx01:/etc/nginx nginx 
# 查看一下这个卷
[root@iz2vcbxtcjxxdepmmofk3hz test]# docker volume ls
DRIVER    VOLUME NAME
local     nginx01
# 在docker中所有没指定主机路径的卷都存放在/var/lib/docker/volumes/XXXX/_data

-v 容器内路径 # 匿名挂载
-v 卷名:容器内路径 # 具名挂载
-v /主机路径:容器内路径 # 指定路径挂载

# -v 容器内路径, ro rw 改变读写权限
# ro readonly 只读
# rw readwrite 可读可写 

# 一旦设置了容器权限 容器对挂载出来的内容 就有限定了
docker run -d -P --name nginx01 -v /etc/nginx:ro nginx 
docker run -d -P --name nginx01 -v /etc/nginx:rw nginx 
# 只要看到ro 就说明 这个路径 只能通过宿主机来操作

# 创建并同步卷，类似硬链接
docker run -it --name docker02 --volume-from docker01 ytc/centos:1.0
```

## 6. Dockerfile

```bash
FROM # 基础镜像 centos  一切从这里开始构建
MAINTAINER # 镜像是谁写的 作者 + 邮箱
RUN # docker 镜像构建的时候 需要运行的命令 
ADD # 加入文件 redis nginx tomcat 
WORKDIR # 镜像的工作目录
VOLUME # 挂载目录
EXPOSE # 指定暴露端口
CMD # 指定这个容器启动的时候 要运行的命令 只有最后一个会生效 可被替代
ENTRYPOINT #  指定这个容器启动的时候 要运行的命令 可以追加命令
ONBUILD # 当构建一个被继承dockerFile 这个时候就会运行ONBUILD的指令 触发指令
COPY # 类似 ADD 将文件拷贝到镜像中
ENV # 构建的适合设置环境变量
```

```bash
FROM centos
MAINTAINER peter<fangrunbo0606@gmail.com>

ENV MYPATH /usr/local
WORKDIR $MYPATH

RUN yum -y install vim
RUN yum -y install net-tools

EXPOSE 18080

CMD /bin/bash
----------------------
# 通过这个文件构建镜像 -f 通过什么文件构建 -t 名称:版本  注意后面有个 .
docker build -f mydockerile-centos -t mycentos:1.0 . 
```

发布

注册dockerhub

```bash
# 登录
docker login [OPTIONS] [SERVER]

Log in to a Docker registry.
If no server is specified, the default is defined by the daemon.

Options:
  -p, --password string   Password
      --password-stdin    Take the password from stdin
  -u, --username string   Username

# 提交
docker push [OPTIONS] NAME[:TAG]

Push an image or a repository to a registry

Options:
  -a, --all-tags                Push all tagged images in the repository
      --disable-content-trust   Skip image signing (default true)
  -q, --quiet                   Suppress verbose output

--------
# 推送镜像的规范是：
docker push 注册用户名/镜像名:版本 
# tag命令修改为规范的镜像：
docker tag boonya/tomcat-allow-remote boonyadocker/tomcat-allow-remote  
# 推送镜像到Docker Hub
docker push boonyadocker/tomcat-allow-remote:latest  
```

也可以发布到阿里云镜像仓库

登录阿里云

## 7. docker0

容器之间可以互相ping通

通过ip addr进入容器内查询容器内网ip

可以使用容器实现集群

使用--link可以使用容器名，但是新版已经不支持

## 8. 自定义网络

网络模式

bridge: 桥接，docker默认

none: 不配置

host: 宿主共享

container: 容器网络连通

```bash
# docker默认启动时会带上--net bridge

# 自定义网络
docker network create --subnet 192.168.0.0/16 --gateway 192.168.0.1 mynet

# 启动
docker run -d -P --name tomcat01 --net mynet tomcat
docker run -d -P --name tomcat02 --net mynet tomcat
# 直接ping域名tomcat01 tomcat02能ping通
```

## 9. 网络连通

```bash
# tomcat001为另一个网络中的容器，讲mynet与tomcat001连通
docker network connect mynet tomcat001
```

## 10. idea远程部署

开启docker远程访问

```bash
# 1.创建ca文件夹，存放CA私钥和公钥
mkdir -p /usr/local/ca
cd /usr/local/ca/
# 2.创建密码 需要连续输入两次相同的密码
openssl genrsa -aes256 -out ca-key.pem 4096
# 3.依次输入密码、国家、省、市、组织名称等
openssl req -new -x509 -days 365 -key ca-key.pem -sha256 -out ca.pem
# 4.生成server-key.pem
openssl genrsa -out server-key.pem 4096
# 5.把下面的IP换成你自己服务器外网的IP或者域名
openssl req -subj "/CN=47.100.36.90" -sha256 -new -key server-key.pem -out server.csr
# 6.配置白名单 0.0.0.0表示所有ip都可以连接(但只有拥有证书的才可以连接成功)
echo subjectAltName = IP:192.168.136.132,IP:0.0.0.0 >> extfile.cnf
# 7.执行命令，将Docker守护程序密钥的扩展使用属性设置为仅用于服务器身份验证
echo extendedKeyUsage = serverAuth >> extfile.cnf
# 8.执行命令，并输入之前设置的密码，生成签名证书
openssl x509 -req -days 365 -sha256 -in server.csr -CA ca.pem -CAkey ca-key.pem \-CAcreateserial -out server-cert.pem -extfile extfile.cnf
# 9.生成客户端的key.pem，到时候把生成好的几个公钥私钥拷出去即可
openssl genrsa -out key.pem 4096
# 10.执行命令
openssl req -subj '/CN=client' -new -key key.pem -out client.csr
# 11.执行命令，要使密钥适合客户端身份验证，请创建扩展配置文件
echo extendedKeyUsage = clientAuth >> extfile.cnf
# 12.生成cert.pem,需要输入前面设置的密码，生成签名证书
openssl x509 -req -days 365 -sha256 -in client.csr -CA ca.pem -CAkey ca-key.pem \-CAcreateserial -out cert.pem -extfile extfile.cnf
# 13.删除不需要的文件，两个证书签名请求
rm -v client.csr server.csr
# 14.修改权限，要保护您的密钥免受意外损坏，请删除其写入权限。要使它们只能被您读取，更改文件模式,证书可以是对外可读的，删除写入权限以防止意外损坏
chmod -v 0400 ca-key.pem key.pem server-key.pem
chmod -v 0444 ca.pem server-cert.pem cert.pem
# 15.归集服务器证书
cp server-*.pem  /etc/docker/
cp ca.pem /etc/docker/
# 16.修改Docker配置，使Docker守护程序仅接受来自提供CA信任的证书的客户端的连接
vim /lib/systemd/system/docker.service
# 17.将ExecStart那一行替换为一下的
ExecStart=/usr/bin/dockerd --tlsverify --tlscacert=/etc/docker/ca.pem --tlscert=/etc/docker/server-cert.pem --tlskey=/etc/docker/server-key.pem -H tcp://0.0.0.0:2375 -H unix:///var/run/docker.sock
# 18.重新加载daemon并重启docker
systemctl daemon-reload 
systemctl restart docker
# 19.开放2375端口
```

保存相关客户端的pem文件到本地

![image-20220112003636479](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20220112003636479.png)

idea插件连接docker

![image-20220112001600477](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20220112001600477.png)

![image-20220110161452538](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20220110161452538.png)

pom中配置自动部署，修改镜像名，如果项目名有大写，配置服务器ip以及本地ca证书路径

```xml
<build>
    <!-- 引用项目名字 -->
    <finalName>${project.artifactId}</finalName>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
        <!-- 跳过单元测试 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <configuration>
                <skipTests>true</skipTests>
            </configuration>
        </plugin>
        <!-- 使用docker-maven-plugin插件 -->
        <plugin>
            <groupId>com.spotify</groupId>
            <artifactId>docker-maven-plugin</artifactId>
            <version>1.0.0</version>

            <!-- 将插件绑定在某个phase执行 -->
            <executions>
                <execution>
                    <id>build-image</id>
                    <!-- 将插件绑定在package这个phase上，也就是说，
                    用户只需执行mvn package，就会自动执行mvn docker:build -->
                    <phase>package</phase>
                    <goals>
                        <goal>build</goal>
                    </goals>
                </execution>
            </executions>

            <configuration>
                <!-- 指定生成的镜像名，这里是我们的项目名，使用小写命名 -->
                <imageName>${project.artifactId}</imageName>
                <!-- 指定标签，这里指定的是镜像的版本，我们默认版本是latest -->
                <imageTags>
                    <imageTag>latest</imageTag>
                </imageTags>
                <!-- 指定我们项目中Dockerfile文件的路径 -->
                <dockerDirectory>${project.basedir}</dockerDirectory>
                <!-- 指定远程docker地址 -->
                <dockerHost>https://47.100.36.90:2375</dockerHost>
                <!-- 指定docker的ca证书路径 -->
                <dockerCertPath>D:\apache\docker-ca</dockerCertPath>
                <!-- 这里是复制jar包到docker容器指定目录配置 -->
                <resources>
                    <resource>
                        <targetPath>/</targetPath>
                        <!-- jar包所在的路径，此处配置的即对应项目中target目录 -->
                        <directory>${project.build.directory}</directory>
                        <!-- 需要包含的 jar包，这里对应的是Dockerfile中添加的文件名 -->
                        <include>${project.build.finalName}.jar</include>
                    </resource>
                </resources>
            </configuration>
        </plugin>
    </plugins>
</build>
```

maven打包

![image-20220110161831024](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20220110161831024.png)

创建容器

![image-20220110162142507](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20220110162142507.png)

![image-20220110162520412](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20220110162520412.png)

创建Dockerfile，修改端口

```dockerfile
FROM java:8
ADD *.jar app.jar
EXPOSE 18080
ENTRYPOINT [ "java", "-jar", "/app.jar" ]
```



















