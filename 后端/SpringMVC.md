# SpringMVC

## 1. 添加Maven依赖

[Spring官方文档](https://spring.io/projects/spring-framework)

根据需求导入依赖

推荐使用注解开发

## 2. 配置web.xml  

```xml
<!--配置DispatcherServlet-->
<servlet>
    <servlet-name>springmvc</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <!--绑定spring-mvc.xml-->
    <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>classpath:spring-mvc.xml</param-value>
    </init-param>
    <!--设置启动级别为1-->
    <load-on-startup>1</load-on-startup>
</servlet>
<servlet-mapping>
    <servlet-name>springmvc</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>
```

`/` 和`/*`的区别: `/`不会匹配静态资源，而`/*`会

## 3. 配置spring-mvc.xml

推荐[使用注解](#6. 注解)

```xml
<!--处理器映射器-->
<bean class="org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping"/>
<!--处理器适配器-->
<bean class="org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter"/>

<!--视图解析器：模版引擎Thymeleaf Freemaker等-->
<bean id="internalResourceViewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
    <!--前缀-->
    <property name="prefix" value="/WEB-INF/jsp/"/>
    <!--后缀-->
    <property name="suffix" value=".jsp"/>
</bean>

<!--映射请求给controller-->
<bean id="/hello" class="org.example.controller.HelloController"/>
```

## 4. 定义controller

```java
package org.example.controller;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloController implements Controller {
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mv = new ModelAndView();

        // 业务代码
        String result = "hellospringmvc";
        mv.addObject("msg",result);

        // 视图跳转
        mv.setViewName("test");

        return mv;
    }
}
```

## 5. 启动Tomcat

或者最开始就用maven的webapp骨架

出问题查看Artifacts中是否有lib是否输出，没有就手动添加

## 6. 注解

[配置web.xml](#2. 配置web.xml)

配置spring-mvc.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context" xmlns:mvc="http://www.springframework.org/schema/mvc"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd
                           http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc.xsd">

    <context:component-scan base-package="org.example.controller"/>
    <!--自动过滤静态资源-->
    <mvc:default-servlet-handler/>
    <!--自动映射适配和bean-->
    <mvc:annotation-driven/>

    <!--视图解析器-->
    <bean id="internalResourceViewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <!--前缀-->
        <property name="prefix" value="/WEB-INF/jsp/"/>
        <!--后缀-->
        <property name="suffix" value=".jsp"/>
    </bean>
</beans>
```

```java
package org.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloController {
    // @RequestMapping("/h1") 请求名 如果在类上就会优先拼接类上注解
    @RequestMapping("/h1")
    public String hello(Model model) {
        model.addAttribute("msg","hellospringmvc");
        // 跳转的视图名 会被视图解析器解析
        return "hello";
    }
}
```

## 7. RestFul风格

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

## 8. 转发和重定向

转发就是默认的return

```java
@RequestMapping("/h1")
public String hello(Model model) {
    model.addAttribute("msg","转发");
    return "hello";
}
```

重定向，不能访问WEB-INF下的静态资源，或者通过转发到其他controller

```java
@RequestMapping("/h1")
public String hello(Model model) {
    model.addAttribute("msg","转发");
    return "redirect:/index.jsp";
    // return "redirect:/h1";
}
```

## 9. 获取请求参数以及响应

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

## 10. 中文乱码

请求中文乱码

1. 添加servlet的filter
2. 在web.xml中添加SpringMVC的乱码过滤

```xml
<filter>
    <filter-name>encoding</filter-name>
    <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
    <init-param>
        <param-name>encoding</param-name>
        <param-value>utf-8</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>encoding</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

传递JSON中文乱码

在spring-mvc.xml中添加

```xml
<mvc:annotation-driven>
    <mvc:message-converters register-defaults="true">
        <bean class="org.springframework.http.converter.StringHttpMessageConverter">
           <constructor-arg value="UTF-8"/>
        </bean>
        <bean class="org.springframework.http.converter.json.MappingJackson2HttpMessageConverter">
            <property name="objectMapper">
                <bean class="org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean">
                    <property name="failOnEmptyBeans" value="false"/>
                </bean>
            </property>
        </bean>
    </mvc:message-converters>
</mvc:annotation-driven>
```
## 11. 用JSON传递

乱码问题：function上面加(一般不这么做，一般用上面这个)

```java
@RequestMapping(value="/j1",produces = "application/json;charset=utf-8")
```



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

## 12. 拦截器

实现HandlerInterceptor接口

```java
public class MyInterceptor implements HandlerInterceptor {

    /*return true; 执行下一个拦截器 放行*/
    /*return false; 不执行下一个拦截器 卡住 不通过 后续使用response跳转首页等逻辑*/
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("处理前");
        return true;
    }
    //在请求处理方法执行之后执行
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("处理后");
    }
    //在dispatcherServlet处理后执行,做清理工作.
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("清理");
    }
}
```

在spring-mvc.xml中配置

```xml
 <!--配置拦截器-->
    <mvc:interceptors>
        <mvc:interceptor>
            <!--/** 包括路径及其子路径-->
            <!--/admin/* 拦截的是/admin/add等等这种 , /admin/add/user不会被拦截-->
            <!--/admin/** 拦截的是/admin/下的所有-->
            <mvc:mapping path="/**"/>
            <!--bean配置的就是拦截器-->
            <bean class="com.tony.config.MyInterceptor"/>
        </mvc:interceptor>
    </mvc:interceptors>
```

## 13. 文件上传和下载

**上传**

前端表单要求：为了能上传文件，必须将表单的method设置为POST，并将enctype设置为multipart/form-data。只有在这样的情况下，浏览器才会把用户选择的文件以二进制数据发送给服务器

**对表单中的 enctype 属性做个详细的说明：**

- application/x-www=form-urlencoded：默认方式，只处理表单域中的 value 属性值，采用这种编码方式的表单会将表单域中的值处理成 URL 编码方式。
- multipart/form-data：这种编码方式会以二进制流的方式来处理表单数据，这种编码方式会把文件域指定文件的内容也封装到请求参数中，不会对字符编码。
- text/plain：除了把空格转换为 "+" 号外，其他字符都不做编码处理，这种方式适用直接通过表单发送邮件。

```html
<form action="/upload" enctype="multipart/form-data" method="post">
 <input type="file" name="file"/>
 <input type="submit" value="upload">
</form>
```

导入文件上传的jar包，commons-fileupload ， Maven会自动帮我们导入他的依赖包 commons-io包；

```xml
<!--文件上传-->
<dependency>
   <groupId>commons-fileupload</groupId>
   <artifactId>commons-fileupload</artifactId>
   <version>1.3.3</version>
</dependency>
<!--servlet-api导入高版本的-->
<dependency>
   <groupId>javax.servlet</groupId>
   <artifactId>javax.servlet-api</artifactId>
   <version>4.0.1</version>
</dependency>
```

2、配置bean：multipartResolver

【**注意！！！这个bena的id必须为：multipartResolver ， 否则上传文件会报400的错误！在这里栽过坑,教训！**】

```xml
<!--文件上传配置-->
<bean id="multipartResolver"  class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
   <!-- 请求的编码格式，必须和jSP的pageEncoding属性一致，以便正确读取表单的内容，默认为ISO-8859-1 -->
   <property name="defaultEncoding" value="utf-8"/>
   <!-- 上传文件大小上限，单位为字节（10485760=10M） -->
   <property name="maxUploadSize" value="10485760"/>
   <property name="maxInMemorySize" value="40960"/>
</bean>
```

3、controller

```java
package com.kuang.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

@Controller
public class FileController {
   //@RequestParam("file") 将name=file控件得到的文件封装成CommonsMultipartFile 对象
   //批量上传CommonsMultipartFile则为数组即可
   @RequestMapping("/upload")
   public String fileUpload(@RequestParam("file") CommonsMultipartFile file , HttpServletRequest request) throws IOException {

       //获取文件名 : file.getOriginalFilename();
       String uploadFileName = file.getOriginalFilename();

       //如果文件名为空，直接回到首页！
       if ("".equals(uploadFileName)){
           return "redirect:/index.jsp";
      }
       System.out.println("上传文件名 : "+uploadFileName);

       //上传路径保存设置 UUID
       String path = request.getServletContext().getRealPath("/upload");
       //如果路径不存在，创建一个
       File realPath = new File(path);
       if (!realPath.exists()){
           realPath.mkdir();
      }
       System.out.println("上传文件保存地址："+realPath);

       InputStream is = file.getInputStream(); //文件输入流
       OutputStream os = new FileOutputStream(new File(realPath,uploadFileName)); //文件输出流

       //读取写出
       int len=0;
       byte[] buffer = new byte[1024];
       while ((len=is.read(buffer))!=-1){
           os.write(buffer,0,len);
           os.flush();
      }
       os.close();
       is.close();
       return "redirect:/index.jsp";
  }
    
    // 采用file.Transto 来保存上传的文件
    @RequestMapping("/upload2")
    public String  fileUpload2(@RequestParam("file") CommonsMultipartFile file, HttpServletRequest request) throws IOException {

       //上传路径保存设置
       String path = request.getServletContext().getRealPath("/upload");
       File realPath = new File(path);
       if (!realPath.exists()){
           realPath.mkdir();
      }
       //上传文件地址
       System.out.println("上传文件保存地址："+realPath);

       //通过CommonsMultipartFile的方法直接写文件（注意这个时候）
       file.transferTo(new File(realPath +"/"+ file.getOriginalFilename()));

       return "redirect:/index.jsp";
    }
}
```

**下载**

前端

```html
<a href="/download">点击下载</a>
```

后端

```java
@RequestMapping(value="/download")
public String downloads(HttpServletResponse response ,HttpServletRequest request) throws Exception{
   //要下载的图片地址
   String  path = request.getServletContext().getRealPath("/upload");
   String  fileName = "基础语法.jpg";

   //1、设置response 响应头
   response.reset(); //设置页面不缓存,清空buffer
   response.setCharacterEncoding("UTF-8"); //字符编码
   response.setContentType("multipart/form-data"); //二进制传输数据
   //设置响应头
   response.setHeader("Content-Disposition",
           "attachment;fileName="+URLEncoder.encode(fileName, "UTF-8"));

   File file = new File(path,fileName);
   //2、 读取文件--输入流
   InputStream input=new FileInputStream(file);
   //3、 写出文件--输出流
   OutputStream out = response.getOutputStream();

   byte[] buff =new byte[1024];
   int index=0;
   //4、执行 写出操作
   while((index= input.read(buff))!= -1){
       out.write(buff, 0, index);
       out.flush();
  }
   out.close();
   input.close();
   return null;
}
```
