package jwt

import (
	"errors"
	"time"

	"github.com/golang-jwt/jwt/v5"
)

// CustomClaims 与 Java auth-center 签发的 claims 对齐
type CustomClaims struct {
	UserID   string `json:"uid"`
	TenantID string `json:"tid"`
	Username string `json:"usr"`
	Roles    string `json:"roles"`
	jwt.RegisteredClaims
}

var (
	ErrInvalidToken = errors.New("invalid token")
	ErrExpiredToken = errors.New("token expired")
)

// Parse 校验签名并返回 claims；secret 至少 32 字节
func Parse(tokenStr, secret, issuer string) (*CustomClaims, error) {
	tok, err := jwt.ParseWithClaims(tokenStr, &CustomClaims{}, func(t *jwt.Token) (interface{}, error) {
		if _, ok := t.Method.(*jwt.SigningMethodHMAC); !ok {
			return nil, ErrInvalidToken
		}
		return []byte(secret), nil
	})
	if err != nil {
		if errors.Is(err, jwt.ErrTokenExpired) {
			return nil, ErrExpiredToken
		}
		return nil, ErrInvalidToken
	}
	claims, ok := tok.Claims.(*CustomClaims)
	if !ok || !tok.Valid {
		return nil, ErrInvalidToken
	}
	if issuer != "" && claims.Issuer != issuer {
		return nil, ErrInvalidToken
	}
	if claims.ExpiresAt != nil && claims.ExpiresAt.Before(time.Now()) {
		return nil, ErrExpiredToken
	}
	return claims, nil
}
