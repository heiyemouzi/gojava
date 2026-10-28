package config

import (
	"fmt"
	"os"

	"gopkg.in/yaml.v3"
)

type Config struct {
	Server    ServerConfig    `yaml:"server"`
	JWT       JWTConfig       `yaml:"jwt"`
	Redis     RedisConfig     `yaml:"redis"`
	Whitelist []string        `yaml:"whitelist"`
	Routes    []RouteConfig   `yaml:"routes"`
	RateLimit RateLimitConfig `yaml:"rate_limit"`
}

type ServerConfig struct {
	Port int `yaml:"port"`
}

type JWTConfig struct {
	Secret string `yaml:"secret"`
	Issuer string `yaml:"issuer"`
}

type RedisConfig struct {
	Addr     string `yaml:"addr"`
	Password string `yaml:"password"`
	DB       int    `yaml:"db"`
}

type RouteConfig struct {
	ID     string `yaml:"id"`
	Prefix string `yaml:"prefix"`
	Target string `yaml:"target"`
}

type RateLimitConfig struct {
	PerSecond int `yaml:"per_second"`
	Burst     int `yaml:"burst"`
}

// Load 从 path 读取配置；同名环境变量 GATEWAY_XXX 可覆盖（预留）。
func Load(path string) (*Config, error) {
	b, err := os.ReadFile(path)
	if err != nil {
		return nil, fmt.Errorf("read config: %w", err)
	}
	var c Config
	if err := yaml.Unmarshal(b, &c); err != nil {
		return nil, fmt.Errorf("parse config: %w", err)
	}
	return &c, nil
}
