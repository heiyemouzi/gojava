// Package password 提供 bcrypt 密码哈希与校验，与 Java BCryptPasswordEncoder 输出兼容。
package password

import "golang.org/x/crypto/bcrypt"

const DefaultCost = 12

// Hash 返回 bcrypt 哈希串（含 salt），直接存库即可。
func Hash(raw string) (string, error) {
	b, err := bcrypt.GenerateFromPassword([]byte(raw), DefaultCost)
	return string(b), err
}

// Verify 恒定时间比较；返回 nil 表示密码正确。
func Verify(raw, hash string) error {
	return bcrypt.CompareHashAndPassword([]byte(hash), []byte(raw))
}
