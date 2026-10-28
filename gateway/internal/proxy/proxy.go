package proxy

import (
	"log"
	"net/http/httputil"
	"net/url"
	"strings"

	"github.com/gin-gonic/gin"
	"github.com/gojava/gateway/internal/config"
	"github.com/gojava/gateway/pkg/response"
)

// Register 按 route 表注册反向代理
func Register(r *gin.Engine, routes []config.RouteConfig) {
	for _, rt := range routes {
		target, err := url.Parse(rt.Target)
		if err != nil {
			log.Fatalf("bad route target %s: %v", rt.Target, err)
		}
		rp := httputil.NewSingleHostReverseProxy(target)

		prefix := rt.Prefix
		r.Any(prefix+"/*any", func(c *gin.Context) {
			// 防止外部伪造身份头：清掉再让中间件重写
			c.Request.Header.Del("X-User-Id")
			c.Request.Header.Del("X-Tenant-Id")
			c.Request.Header.Del("X-Username")
			c.Request.Header.Del("X-Roles")

			// 去掉 gateway 的前缀（如果后端希望干净路径）
			c.Request.URL.Path = strings.TrimPrefix(c.Request.URL.Path, strings.TrimSuffix(prefix, "/"))
			rp.ServeHTTP(c.Writer, c.Request)
		})
		log.Printf("route %s -> %s", prefix, rt.Target)
	}

	// 兜底
	r.NoRoute(func(c *gin.Context) {
		response.Fail(c, 404, 404, "route not found")
	})
}
