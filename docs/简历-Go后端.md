# 黄浦（Go 后端 / 云原生方向）

13年工作经验 | 求职意向：Go 后端开发 / 微服务架构 | 期望薪资：18-22K | 期望城市：成都

男 | 37岁 | 15184448970 | 503807332@qq.com

---

## 个人优势

13 年高并发服务端开发（Java 起步，近 1 年深度转 Go），经历过单服 8000 在线的长连接系统设计与性能调优。能用 Go+Gin 独立完成微服务网关（JWT 鉴权、限流、反向代理）、中间件开发、后台管理系统。理解 goroutine 并发模型、GMP 调度、channel、sync 原语，熟悉 Go+Java 混合架构下的服务治理（Nacos、OpenFeign、MyBatis-Plus）。具备线上故障排查、JVM/Go GC 调优、Linux 部署实战经验。

## 专业技能

**Go**：熟练 Go/Gin/goroutine/channel/sync；熟悉 Go module、pprof、gRPC、WebSocket；能独立设计微服务网关

**Java 生态**：精通 Spring Boot、Spring MVC、MyBatis-Plus；熟悉 Spring Cloud Alibaba（Nacos、OpenFeign、LoadBalancer）

**中间件**：MySQL（索引优化）、Redis（多级缓存/分布式锁）、RocketMQ、MongoDB

**云原生**：Docker、基础 K8s、Jenkins CI/CD、Nginx、Linux 部署

**工程**：Git、Maven、代码评审、敏捷开发

**AI 研发效能**：熟练使用 Cursor / Claude Code / GitHub Copilot 作为结对编程工具，独立完成 gojava 微服务框架从 0 到 1 开发；坚持 AI 生成代码必过人工 Review，重点把控并发安全、SQL 注入、密钥管理、异常兜底

---

## 工作经历

### 成都繁英网络科技有限公司　服务端主程　2025.12-至今
- MMORPG 手游后端技术负责人
- **独立使用 Go+Gin+Vue+Gorm 搭建公司内部微服务后台**，覆盖运营配置、数据报表、玩家管理

### 成都佰联盛科技有限公司　Java 主程　2022.02-2025.11
- MMORPG 项目技术架构、代码规范、线上稳定性保障

### 成都墨非科技　主程　2017.12-2022.01
- ARPG 页游架构迭代、性能优化、研发工具链建设

### 成都墨龙科技　服务端　2013.07-2017.06
- 商业页游业务开发、渠道 SDK、上线运维

---

## 项目经历

### gojava 微服务 SaaS 框架（个人项目）　2026.10-至今
**技术栈**：Go/Gin / Spring Boot 3 / MyBatis-Plus / MySQL / Redis / Vue3

**项目描述**：按生产架构设计的 Go+Java 混合微服务系统，Go 做接入层网关，Java 做业务域，演示云原生服务治理。

**核心工作**：
1. **Go 网关（Gin）**：实现 JWT 校验中间件、IP 限流（golang.org/x/time/rate）、CORS、Recovery、反向代理（httputil.ReverseProxy）；校验通过后将用户身份（X-User-Id/X-Tenant-Id）透传后端
2. **鉴权中心（Spring Boot）**：BCrypt 密码加密、JWT 双 token 签发
3. **服务间调用**：OpenFeign 声明式客户端，产品服务/订单服务解耦
4. **数据层**：MyBatis-Plus LambdaQueryWrapper，条件 UPDATE 防超卖
5. **部署**：docker-compose 本地基础设施，bat 一键启停

### 仙帝神兵 MMORPG 手游　服务端主程　2025.12-至今
**技术栈**：Java Netty / Protobuf / Redis / MongoDB / Go+Gin
**业绩**：
1. 重构 Netty 长连接调度，按 OS 适配 NIO/Epoll，高峰断线率显著下降
2. JVM 调优 + 压测，同等内存单服承载提升 25%
3. 基于 Go+Gin+Vue 搭建新业务后台，替代老旧系统

### 勇者之塔 MMORPG　服务端主程　2022.02-2025.11
**业绩**：单服峰值 8000；三层架构搭建；Protobuf 代码生成工具提效 70%；Redis 多级缓存

---

## 教育经历

**成都信息工程大学**　本科 软件工程　2009-2013
