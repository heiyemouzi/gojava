package router

import (
	"github.com/gin-gonic/gin"
	"github.com/gojava/gateway/internal/config"
	"github.com/gojava/gateway/internal/handler"
	"github.com/gojava/gateway/internal/middleware"
	"github.com/gojava/gateway/internal/proxy"
)

func New(cfg *config.Config) *gin.Engine {
	gin.SetMode(gin.ReleaseMode)
	r := gin.New()

	r.Use(middleware.Recovery())
	r.Use(middleware.CORS())
	r.Use(middleware.RateLimit(cfg.RateLimit.PerSecond, cfg.RateLimit.Burst))

	r.GET("/ping", handler.Ping)

	// JWT 鉴权（白名单已在中间件内处理）
	r.Use(middleware.Auth(cfg.JWT.Secret, cfg.JWT.Issuer, cfg.Whitelist))

	proxy.Register(r, cfg.Routes)
	return r
}
 