package handler

import (
	"github.com/gin-gonic/gin"
	"github.com/gojava/gateway/pkg/response"
)

func Ping(c *gin.Context) {
	response.OK(c, gin.H{"gateway": "up"})
}
