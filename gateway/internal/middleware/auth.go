package middleware

import (
	"strings"

	"github.com/gin-gonic/gin"
	jwtpkg "github.com/gojava/gateway/pkg/jwt"
	"github.com/gojava/gateway/pkg/response"
)

// Auth JWT 校验中间件：
//   - 从 Authorization: Bearer xxx 取 token
//   - 验签 + 过期
//   - 把用户身份注入 X-User-Id / X-Tenant-Id / X-Username / X-Roles 转发给后端
func Auth(secret, issuer string, whitelist []string) gin.HandlerFunc {
	white := map[string]struct{}{}
	for _, p := range whitelist {
		white[p] = struct{}{}
	}
	return func(c *gin.Context) {
		path := c.Request.URL.Path
		//除了登陆全部走jwt鉴权
		if _, ok := white[path]; ok {
			return
		}

		auth := c.GetHeader("Authorization")
		if !strings.HasPrefix(auth, "Bearer ") {
			response.Fail(c, 401, 401, "missing token")
			c.Abort()
			return
		}
		tok := strings.TrimPrefix(auth, "Bearer ")
		claims, err := jwtpkg.Parse(tok, secret, issuer)
		if err != nil {
			response.Fail(c, 401, 401, "unauthorized: "+err.Error())
			c.Abort()
			return
		}

		// 透传给后端；后端必须只信任内网网关
		c.Request.Header.Set("X-User-Id", claims.UserID)
		c.Request.Header.Set("X-Tenant-Id", claims.TenantID)
		c.Request.Header.Set("X-Username", claims.Username)
		c.Request.Header.Set("X-Roles", claims.Roles)
		// 删掉外部可能伪造的同名头之后再写
		c.Next()
	}
}
