# QuickStart
基于SpringCloud体系实现。

# 用到的技术:
- [x] Spring Cloud Eureka 注册中心
- [x] Spring Cloud Config 配置中心
- [x] Spring Cloud security 权限控制(现仅限于配置中心使用)
- [x] Spring Cloud hystrix 断路器
- [x] Spring Cloud Feign/Ribbon 负载请求
- [x] Spring Boot Admin 服务监控
- [x] Swagger2 Api文档输出
- [x] Lombok 代码简化
- [x] Druid 连接池,加密,监控
- [x] Logback 日志框架
- [x] Jenkins 自动化部署
- [x] PageHelper 分页助手
- [ ] Redis NoSql
- [ ] Hibernate Validation 数据校验工具
- [ ] Redis 分布式锁
- [ ] Tcc/Lcn 分布式事务 


# 各模块介绍

| 模块名称        | 端口   |  简介  |
| --------   | -----:  | :----:  |
| admin-server      | 9002   |   服务监控中心，监控所有服务模块    |
| conf-server        |   9004   |   分布式配置中心，结合spring-security/rabbitmq同时使用   |
| eureka-server        |    9003    |  服务注册中心，提供服务注册、发现功能  |
| account-service        |    8080    |  用户服务，提供注册、登录、地址等服务  |
| log-service        |    8085    |  日志服务，提供用户登录登出日志记录、用户操作记录  |
| front-app        |    8088    |  前端服务，结合swagger2提供API管理  |
| game-server        |    9091    |  游戏服务，结合swagger2提供API管理  |
| control        |    9092    |  总控管理系统，给公司内部运营使用。管理开站点等功能  |
| agent        |    9090    |  代理系统，给厅主登录。查询下面所属会员详细信息  |
| lottery-center-service        |    9093    |  开奖中心  |
| slot-game-service        |    8010    |  老虎机游戏服务  |
| fish-game-service        |    8011    |  捕鱼游戏服务  |
| card-game-service        |    8013    |  字牌游戏服务  |
| baccara-game-service        |    8014    |  百家乐游戏服务  |
| bullfight-game-service        |    9095    |  牛牛游戏服务  |
| threecardbrag-service         |    8066    |  炸金花游戏业务服务  |
| threecardbrag-server          |    9065 |  炸金花游戏服务器 |
| baccara_service      |    8021    | 百家乐游戏业务服务  |
| baccara_server        |    8022 |  百家乐游戏服务器 |
# 快速上手
- 1、先启动admin-server,eureka-server,conf-server三个基础服务
- 2、再依次启动account-service/log-service基础业务服务
- 3、启动game-server服务，打开浏览器，输入http://localhost:9091/swagger-ui.html。长连接测试可用谷歌浏览器打开game-server工程的WebsocketChartClient.html文件测试。
- 启动control,打开浏览器，输入http://localhost:9092  。 账号：admin 密码：123456
- 启动agent,打开浏览器，输入http://localhost:9090  。 账号：allen 密码：123456
--启动rabbbitmq,配置user账号baccarat，在baccarat中配置虚拟主机virtual-host baccarat，在到对应的配置中心配置对应的属性
--spring.rabbitmq.host=172.20.100.207
-- spring.rabbitmq.port=5672
-- spring.rabbitmq.username=baccarat
-- spring.rabbitmq.password=123456
-- spring.rabbitmq.virtual-host=baccarat
# 工具的使用
* Druid密码加密
```java
java -cp druid-1.0.16.jar com.alibaba.druid.filter.config.ConfigTools you_password
```
> 参考:https://github.com/alibaba/druid/wiki/%E4%BD%BF%E7%94%A8ConfigFilter

* 代码生成工具
```shell
<!--编码设置-->
set MAVEN_OPTS="-Dfile.encoding=UTF-8"
<!--生成代码-->
mvn mybatis-generator:generate
```

* Eclipse Lombok插件安装  
下载`https://projectlombok.org/download`  
双击运行,找到eclipse.exe的安装目录,点击`install/update`,重启Eclipse


	
	
	


