@echo off
rem 启动 Go 网关 :8080
chcp 65001 >nul
cd /d %~dp0..\gateway
echo [gojava] 启动 gateway :8080
start "gojava-gateway" cmd /k "go run ./cmd/server -f etc/gateway.yaml"
