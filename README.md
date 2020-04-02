# 微信公众号项目后端介绍

### 一、 技术栈
1. 使用springboot整合ssm进行开发
2. 使用阿里Druid数据库连接池
3. 使用mybatis plus简化单表操作
4. 使用shiro进行用户认证、权限检查
5. 使用shiro的SessionManager管理session.同时使用redis实现
Shiro的session持久化
6. 使用spring websocket实现聊天功能
7. 使用lombok、mapstruct、hibernate validator简化代码，
分别作用是生成getset方法、实体bean之间的转换(dto、vo、entity)、
参数校验
8. 使用FastJSON做springmvc的JSON转化
9. 微信api使用wxtools框架简化开发
9. 使用gradle做项目的构建

### 二、 目录结构 
- api 接口层，一般写controller相关的代码
    - controller     放置controller，提供对外接口
    - exhandler      全局异常处理器
    - config         springboot一些框架Bean的配置
    - interceptor    springmvc拦截器
    - processor      spring后置处理器，为我自定义的微信拦截器提供bean注入
    - quartz         定时器，定时处理相关任务，如每日用户数据更新
    - runner         启动器，在springboot应用启动后执行
    - shiro          shiro框架相关内容
    - util           controller层需要使用的工具类
    - wxinterceptor  自定义的微信拦截器，用户拦截微信的事件或消息
- service 服务层， 一般写为api提供下层服务。同时也需要通过dao访问数据库
    - advice 切面，提供面向切面的功能，如在service层使用hibernate validator
    - config 该模块中的相关配置，如mybatis plus
    - constant 系统常量
    - dao 数据库访问对象
    - service 服务
    - exception 自定义异常
    - mq rabbitmq的消费者与生产者
    - util 工具类
- common 公共模块，放置项目的工具类、异常等
    - annotation 自定义的微信拦截器的注解
    - config 配置，写了redis的配置
    - exception 自定义异常
    - page 分页实体
    - util 通用工具类
    - validator 添加的通用校验相关代码
    - **resources**下的配置文件
        - application.yml 基本配置文件，配置了公共的配置，可以通过spring.profiles.active指定激活的环境
        - application-dev.yml 开发环境下的配置文件,需要active为dev
        - application-prod.yml 生产环境下的配置文件，需要active为prod
        - wx.properties wxtools的配置文件，配置了微信的相关接入参数
        
 ### 三、项目代码解释 
 ##### 1. WxInterceptor
- 简介

 由于我们需要接受微信的信息(用户发送消息或点击菜单),并提供正确的响应，
 我们需要在CoreController的handle方法去获取信息并处理，但是如果频繁
 修改可能会影响原先的功能。于是写了一套拦截器，添加功能只需要添加拦截器，
 而无需修改原代码

- 使用说明

    1. 编写类实现com.originit.union.api.wxinterceptor.WXInterceptor
    2. 给实现类加上Interceptor注解
    3. 编写拦截策略以及处理
     
     ```java
     // 通过这个注解，拦截器会自动注入CoreController
     @Interceptor
     @Slf4j
     public class ChatInterceptor implements WXInterceptor {
            
         @Override
         public int intercept(HttpServletRequest request, HttpServletResponse response) throws Exception {
             // 你可以在这里根据自己的需要，获取微信公众号发来的信息进行判断是否是自己需要的
             // 具体的返回值类型在WxInterceptor接口中进行了解释
         }
     
         @Override
         // 可选的异步
         @Async
         public void handle(HttpServletRequest request, HttpServletResponse response, WxXmlMessage message) {      
              // 这里可以去处理拦截后的逻辑
              // 参数中会自动注入WxXmlMessage来获取信息
         }
   
         // 执行的顺序。值越大越靠前，默认为0，没有特殊需求无需考虑
         @Override
         public int order() {
               return Integer.MAX_VALUE;
         }
     }
     ```
         
 ##### 2. shiro的使用(重点)
 
###### 1. 路径拦截
 
 项目中使用shiro来管理用户的身份验证，用户的登录登出逻辑在com.originit.union.api.controller.UserController中,
 因为使用shiro，所以会对整个项目进行拦截，如果你有新的接口，需要在登录之前就能访问，你需要进行配置，配置在com.originit.union.api.shiro.config.ShiroConfig的
 shiroFilterFactory方法中。

###### 2. session管理以及token
 
 项目不使用原本的sessionId，而是自定义token。客户端需要在请求头中带Authorization头，值为登录后返回的token的值。
 因此就有一个问题，微信端每次发消息过来都不会带，就会每次都创建一个Session，如果请求量大，成千上万个session服务器
 绝对撑不住，为此，给微信端写了一个公共的Session，如果识别是微信端的，就返回这个session，代码如下
  **重点:**
 ```java
        // 获取SessionID时，如果是来自微信的，就不单独创建session
        if (request.getParameter("signature") != null
                && request.getParameter("timestamp") != null
                && request.getParameter("nonce") != null ) {
            // 返回微信公共的sessionId
            return WECHAT_COMMON_SESSION_ID;
        }
        
        
        // 获取Session时
        Serializable sessionId = getSessionId(sessionKey);
        // 如果是微信的请求直接返回微信的无效的Session
        if (WECHAT_COMMON_SESSION_ID.equals(sessionId)) {
            return WECHAT_SESSION;
        }
```
 具体参考com.originit.union.api.shiro.ShiroSessionManager

###### 3. use_key
为了快速的查找用户的session用以删除，在登录时在redis中存了一个
key记录当前用户的sessionId，同时在登出时一并移除

##### 3. websocket
项目中使用了WebSocket完成聊天功能，其中聊天相关功能的代码在com.originit.union.api.controller.WsChatController.
这个就需要自己去学了，我整理了一篇博客，你们可以参考一下:https://blog.csdn.net/qq_40985294/article/details/105078751

##### 4. 项目中使用了lombok，需要使用idea环境并下载lombok插件，并且在项目中开启Annotaion Processor，百度即可

#### 5. 项目使用了一个我写的jar包，response.jar，通过这个jar包，我们直接在Controller上标注解即可返回全局统一响应，
代码如下：
```java
// 通过@RestController注解所有的接口方法都会将返回值转换成JSON
// 通过@ResponseResult注解，所有接口都会在返回值的基础上包一层com.xxc.response.result.PlatformResult，这样就不用手写了
// 如果有些接口不需要包裹，就在方法上添加@OriginResponse注解即可
// 注意，当接口正常响应会包裹，若出现未捕获的异常，则会转到全局异常处理器
@RestController
@ResponseResult
@RequestMapping("/resource")
public class ResourceController {
    
}
```

### 四、 项目使用

0. 下载idea，配置lombok
1. 使用idea，clone项目
2. 使用shadowsocket开启网络代理(因为微信公众号有安全ip，我设置为了我的服务器地址,稳定),shadowsockets在项目的根目录
3. 等待gradle完成之后使用gradle窗口的bootRun即可运行项目
4. 若前端代码更新了，前端编译后将产物放在当前项目的api模块的resources/static/下面


#### 五、 学习资料
1. 项目通用架构学习
可以关注一下该博主:https://me.csdn.net/aiyaya_
学习Spring项目通用功能相关的知识，如统一异常处理、统一响应、统一日志打印、RequestContextHolderUtil(项目中已使用)、hibernate validator+Assert参数校验、Restful Api
2. gradle的使用 到掘金找找
3. lombok、mapStruct的使用(非常简单)
4. HTTP协议(学前三章就可以了)
链接：https://pan.baidu.com/s/1nQ5BPojPChpirvNk_MmpfQ 
提取码：qhpc
5. Redis、rabbitmq (前期不要学)
可以在bilibili看尚硅谷的redis，rabbitmq自己找
