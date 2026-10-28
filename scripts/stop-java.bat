@echo off
rem 一键关闭所有 gojava 后端窗口（Java + Go）
chcp 65001 >nul
echo [gojava] 关闭后端...
taskkill /F /FI "WINDOWTITLE eq gojava-auth*"    >nul 2>&1
taskkill /F /FI "WINDOWTITLE eq gojava-product*" >nul 2>&1
taskkill /F /FI "WINDOWTITLE eq gojava-order*"   >nul 2>&1
taskkill /F /FI "WINDOWTITLE eq gojava-tenant*"  >nul 2>&1
taskkill /F /FI "WINDOWTITLE eq gojava-gateway*" >nul 2>&1
echo 已关闭
