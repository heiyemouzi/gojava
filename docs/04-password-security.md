# 04 密码管理规范

## 存储原则

1. **永远不许存明文**，也不许存可逆加密后的密文（如 AES）。
2. 一律使用 **bcrypt**，cost=12（Java `BCryptPasswordEncoder(12)`，Go `bcrypt.GenerateFromPassword(pw, 12)`）。
3. bcrypt 自带随机 salt，**不要**自己再拼 salt 再 md5。
4. 数据库字段 `password_hash VARCHAR(72)`，长度够 bcrypt 输出（60 字符）。

## 禁止做法（红线）

- ❌ MD5 / SHA1 / SHA256 直接存
- ❌ 自己写"加密"算法（异或、base64、自定义混淆）
- ❌ 把密码当参数写日志、写监控、写报错信息
- ❌ 密码通过 URL query 传输
- ❌ 数据库dump/导出文件里出现真实哈希后还能被外部下载

## 密码策略

| 项 | 规则 |
|---|---|
| 长度 | 8 ~ 64 |
| 字符 | 至少含大写、小写、数字、特殊符号中的 3 类 |
| 与用户名 | 不能等于或包含用户名 |
| 历史 | 最近 3 次不能重复（预留表 `password_history`） |
| 失败锁定 | 连续失败 5 次锁 10 分钟 |
| 有效期 | 90 天提醒改密（可选） |

## 注册/改密流程

1. 前端收集明文 → HTTPS POST 到 `/api/auth/register` 或 `/api/auth/change-password`。
2. 后端先做策略校验（长度/复杂度）。
3. `BCrypt.hashpw(raw, cost=12)` 得到 hash。
4. 存 `password_hash`；旧 hash 写入 `password_history`。
5. 登录时 `BCrypt.verify(raw, hash)` 恒定时间比较，天然抗时序攻击。

## 示例代码

Java：
```java
PasswordEncoder enc = new BCryptPasswordEncoder(12);
String hash = enc.encode(rawPassword);          // 注册/改密
boolean ok  = enc.matches(rawPassword, hash);    // 登录
```

Go：
```go
hash, _ := bcrypt.GenerateFromPassword([]byte(raw), 12)
err := bcrypt.CompareHashAndPassword(hash, []byte(raw)) // err==nil 即通过
```

## 运维要求

- 数据库账号密码、JWT 密钥、第三方 AK/SK 全部走 Nacos/etcd 配置中心 + 命名空间隔离。
- 生产库 `password_hash` 字段禁止 `SELECT *` 导出到非生产环境。
- 员工离职立即回收配置中心权限；怀疑泄露时全量强制改密。
