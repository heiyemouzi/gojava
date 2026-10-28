# 本地开发启动手册

## 1. 起依赖

```powershell
cd D:\workgo\gojava\deploy
docker compose up -d
```

验证：
- MySQL: `mysql -h127.0.0.1 -uroot -proot123 gojava`（init.sql 已自动执行）
- Nacos 控制台: http://127.0.0.1:8848/nacos （nacos/nacos）
- MinIO 控制台: http://127.0.0.1:9001 （minioadmin/minioadmin）

## 2. 起 Java auth-center

```powershell
cd D:\workgo\gojava\biz-java\auth-center
mvn spring-boot:run
```
监听 8081。

## 3. 起 Go gateway

```powershell
cd D:\workgo\gojava\gateway
go mod tidy
go run ./cmd/server -f etc/gateway.yaml
```
监听 8080。

## 4. 联调

```powershell
# 登录
curl -X POST http://localhost:8080/api/auth/login `
  -H "Content-Type: application/json" `
  -d '{"username":"admin","password":"Admin@123"}'

# 拿 access_token 后访问受保护接口
curl http://localhost:8080/api/tenant/users/me `
  -H "Authorization: Bearer <access_token>"
```

预期：登录返回 `{code:0, data:{accessToken, refreshToken}}`；
带 token 访问 `/api/tenant/users/me` 返回当前用户身份。
