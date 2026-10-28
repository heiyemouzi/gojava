# gojava

Go + Java 混合微服务 SaaS 系统。**Go 扛接入，Java 扛业务，Vue3 前端。**

## 架构

```
Vue3 (5173)
   │  /api/*
   ▼
Go Gateway (8080)  ── JWT 校验 / 限流 / 路由转发
   │
   ├── /api/auth/*    → auth-center     (8081)  登录发 JWT
   ├── /api/product/* → product-service(8083)  商品/库存/价格
   └── /api/order/*   → order-service  (8084)  下单全链路
                            │ HTTP 内网调用
                            ▼
                       product-service /internal/product/*
```

## 服务清单

| 服务 | 语言 | 端口 | 库 | 职责 |
|---|---|---|---|---|
| gateway | Go | 8080 | - | 网关：JWT 校验、限流、反代 |
| web | Vue3+Vite | 5173 | - | 前端 |
| auth-center | Java | 8081 | gojava_auth | 登录、签发 JWT、bcrypt 密码校验 |
| product-service | Java | 8083 | gojava_product | 商品列表、价格、库存扣减 |
| order-service | Java | 8084 | gojava_order | 下单、订单列表、取消 |

## 业务功能（全链路）

1. **登录**：admin/Admin@123 → 拿 JWT。
2. **创建订单**：选商品 → 前端读库存/价格 → 提交 → 后端校验 → 扣库存 → 写订单 → 返回。
3. **商品库存页**（额外加）：查看所有上架商品实时库存与价格。
4. **我的订单**（额外加）：查看历史订单、取消订单。

## 启动

### 1. 初始化数据库（本地 MySQL root/root）

```powershell
mysql -uroot -proot < D:\workgo\gojava\scripts\init.sql
```

### 2. 启动后端三个 Java 服务（各开一个终端）

```powershell
cd D:\workgo\gojava\biz-java\auth-center    ; mvn spring-boot:run
cd D:\workgo\gojava\biz-java\product-service; mvn spring-boot:run
cd D:\workgo\gojava\biz-java\order-service  ; mvn spring-boot:run
```

### 3. 启动 Go 网关

```powershell
cd D:\workgo\gojava\gateway
go run ./cmd/server -f etc/gateway.yaml
```

### 4. 启动前端

```powershell
cd D:\workgo\gojava\web
npm install
npm run dev
```

浏览器打开 http://localhost:5173 ，用 `admin / Admin@123` 登录。

## 目录

```
gojava/
├── gateway/           # Go 网关
├── common-go/         # Go 公共库（bcrypt 等）
├── biz-java/
│   ├── auth-center/    # 登录/JWT
│   ├── product-service/# 商品/库存/价格
│   └── order-service/  # 订单
├── web/               # Vue3 + Vite + Element Plus
├── deploy/            # docker-compose（可选）
├── scripts/init.sql   # 建库建表 + 种子数据
└── docs/              # 架构/规范/鉴权/密码 文档
```
