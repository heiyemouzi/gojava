# 02 编码规范

## 通用

1. 仓库即单体（monorepo），Go 与 Java 各自模块独立编译/部署，顶层不互相 import。
2. 配置一律走环境变量或配置中心，**禁止硬编码 IP/账号/密码**。
3. 所有外部接口返回统一结构：
   ```json
   { "code": 0, "msg": "ok", "data": {} }
   ```
   `code=0` 成功；非 0 为业务错误码；HTTP 状态码只表达传输层。
4. 日志必须带 `traceId`（网关生成，全链路透传），格式 JSON。
5. 异常不允许静默吞掉；`catch` 必须打日志并给出用户可读提示。

## Go 规范（gateway/、common-go/）

- 目录分层：`cmd/` 入口、`internal/` 业务（config/middleware/router/proxy/handler）、`pkg/` 可被外部复用的包。
- 包名小写单数，禁止 `util/utils` 这种万能包，按职责命名（`jwt`、`response`、`password`）。
- 错误处理：`if err != nil` 立即处理或包装 `fmt.Errorf("xxx: %w", err)`，禁止裸 `_`。
- 命名：导出标识符必须有注释；接口小而美（`Stringer` 那种粒度）。
- 依赖：标准库优先；HTTP 用 Gin；JWT 用 `github.com/golang-jwt/jwt/v5`；密码用 `golang.org/x/crypto/bcrypt`；限流式 `golang.org/x/time/rate`。
- 测试：每个 `internal/` 包至少有一个 `*_test.go`，中间件必须有表驱动测试。

## Java 规范（biz-java/）

- Spring Boot 3.x，JDK 17，包名 `com.gojava.<模块>`。
- 分层严格：`controller` → `service` → `mapper`，跨域调用走 API 接口/Feign，禁止跨表 JOIN。
- 实体用 MyBatis-Plus；DTO/VO 分离，Entity 禁止直接序列化出参。
- 异常：全局 `@RestControllerAdvice` 统一转成上面的统一响应体。
- 密码：`BCryptPasswordEncoder`，强度 12，**禁止** MD5/SHA/明文/可逆加密。
- JWT：用 `jjwt-api` 0.12.x，公钥验签可选；本骨架先用对称 HS256 起步，生产切 RSA256。

## Git 提交

- Conventional Commits：`feat(auth): xxx` / `fix(gateway): xxx` / `docs: xxx`。
- 分支：`main` 可部署；功能分支 `feat/xxx`；MR 必须过 CI。
