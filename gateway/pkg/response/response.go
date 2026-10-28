package response

import "github.com/gin-gonic/gin"

// Body 统一响应体：code=0 成功，非 0 业务错误
type Body struct {
	Code int         `json:"code"`
	Msg  string      `json:"msg"`
	Data interface{} `json:"data,omitempty"`
}

func OK(c *gin.Context, data interface{}) {
	c.JSON(200, Body{Code: 0, Msg: "ok", Data: data})
}

func Fail(c *gin.Context, httpStatus, code int, msg string) {
	c.JSON(httpStatus, Body{Code: code, Msg: msg})
}
