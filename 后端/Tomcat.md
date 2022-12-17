# Tomcat

## 1. 下载及配置

[Tomcat官网](https://tomcat.apache.org/index.html)

下载Tomcat 8或者9，不要下载10

解压到跟Maven一起

### 1.1 环境变量

解压完成之后配置环境变量

![image-20211013150131020](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211013150131020.png)

![image-20211013150148796](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211013150148796.png)

### 1.2 Tomcat在idea中的配置

![image-20211013150506745](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211013150506745.png)

![image-20211013150609364](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211013150609364.png)

![image-20211013150722635](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211013150722635.png)

![image-20211013150746649](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211013150746649.png)

![image-20211013150758821](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211013150758821.png)

编辑当前项目web路径后缀

![image-20211013150850140](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211013150850140.png)

URL会自动同步，如果未同步，手动修改

![image-20211013150910075](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211013150910075.png)

## N. 问题解决

### N.1 控制台乱码

`Tomcat_path`替换为Tomcat根目录路径

打开`Maven_path\conf\logging.properties`

将UTF-8修改为GBA

![image-20211013151842596](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/image-20211013151842596.png)