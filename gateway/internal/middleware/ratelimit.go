package middleware

import (
	"sync"

	"github.com/gin-gonic/gin"
	"github.com/gojava/gateway/pkg/response"
	"golang.org/x/time/rate"
)

type ipLimiter struct {
	mu  sync.Mutex
	ips map[string]*rate.Limiter
	rps rate.Limit
	bst int
}

func newIPLimiter(rps int, burst int) *ipLimiter {
	return &ipLimiter{
		ips: map[string]*rate.Limiter{},
		rps: rate.Limit(rps),
		bst: burst,
	}
}

func (l *ipLimiter) get(ip string) *rate.Limiter {
	l.mu.Lock()
	defer l.mu.Unlock()
	lim, ok := l.ips[ip]
	if !ok {
		lim = rate.NewLimiter(l.rps, l.bst)
		l.ips[ip] = lim
	}
	return lim
}

func RateLimit(rps, burst int) gin.HandlerFunc {
	lim := newIPLimiter(rps, burst)
	return func(c *gin.Context) {
		if !lim.get(c.ClientIP()).Allow() {
			response.Fail(c, 429, 429, "too many requests")
			c.Abort()
			return
		}
		c.Next()
	}
}
