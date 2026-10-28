package middleware

import (
	"log"
	"runtime/debug"

	"github.com/gin-gonic/gin"
	"github.com/gojava/gateway/pkg/response"
)

func Recovery() gin.HandlerFunc {
	return func(c *gin.Context) {
		defer func() {
			if r := recover(); r != nil {
				log.Printf("panic: %v\n%s", r, debug.Stack())
				response.Fail(c, 500, 500, "internal server error")
				c.Abort()
			}
		}()
		c.Next()
	}
}
