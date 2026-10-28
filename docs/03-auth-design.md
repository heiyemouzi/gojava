# 03 鉴权设计（JWT + 网关校验）

## 模型

- **谁签发**：Java `auth-center` 负责登录、校验密码、签发 JWT。
- **谁校验**：Go `gateway` 负责校验 JWT 签名/过期，**后端 Java 服务不再验签**，只信任网关透传的身份头。
- **为什么这样**：所有外部请求必经网关，验签集中在一处，后端服务零鉴权代码、专注业务。

## Token 结构

JWT Claims：

| Claim | 说明 |
|---|---|
| `sub` | 用户 ID |
| `tenantId` | 租户 ID（多租户隔离关键字段） |
| `username` | 登录名 |
| `roles` | 角色列表，逗号分隔 |
| `exp` | 过期时间（access token 2h） |
| `jti` | token 唯一 ID（用于登出拉黑） |

**双 token**：
- `access_token`：2 小时，放 Header `Authorization: Bearer xxx`。
- `refresh_token`：7 天，存 Redis（key=`refresh:{userId}`），用于换新 access。

## 流程

```
登录  POST /api/auth/login {username,password}
  └─► auth-center 查库 → BCrypt.matches → 签 access+refresh → 写 Redis

业务  GET /api/tenant/users
  └─► gateway 取 Bearer → 验签/过期/黑名单 → 注入 X-User-Id/X-Tenant-Id/X-Roles
        └─► 转发 tenant-service（内网）

刷新  POST /api/auth/refresh {refreshToken}
  └─► auth-center 校验 Redis 是否存在 → 重发 access

登出  POST /api/auth/logout
  └─► gateway 把 jti 写 Redis 黑名单(until exp) → 删 refresh
```

## 网关必须做的中间件顺序

1. Recovery（panic 兜底）
2. AccessLog（打 traceId）
3. CORS
4. 白名单放行（`/api/auth/login`、`/api/auth/refresh`、`/ping`）
5. JWT 校验 → 注入身份头
6. 路由转发

## 安全红线

- JWT 密钥至少 32 字节，从配置中心读，**不进 git**。
- 网关与后端之间走内网/VPC，后端端口不对公网暴露。
- 密码错误次数限制：同一账号 5 次失败锁 10 分钟（Redis 计数）。
- 敏感接口（改密码、改权限）要求二次校验密码或 MFA（预留）。
