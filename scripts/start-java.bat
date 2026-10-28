@echo off
rem 一键启动所有 Java 后端服务（各开一个独立窗口）
chcp 65001 >nul

rem 强制使用 JDK 17（系统默认是 JDK 8，Spring Boot 3 需要 17）
set "JAVA_HOME=E:\soft\openjdk-17\jdk-17"
set "PATH=%JAVA_HOME%\bin;%PATH%"

set "BASE=%~dp0..\biz-java"

echo [gojava] 使用 JDK:
"%JAVA_HOME%\bin\java.exe" -version

echo [gojava] 启动 auth-center :8081
start "gojava-auth"   cmd /k "set JAVA_HOME=%JAVA_HOME% && set PATH=%JAVA_HOME%\bin;%%PATH%% && cd /d %BASE%\auth-center     && mvn spring-boot:run"

timeout /t 2 >nul
echo [gojava] 启动 product-service :8083
start "gojava-product" cmd /k "set JAVA_HOME=%JAVA_HOME% && set PATH=%JAVA_HOME%\bin;%%PATH%% && cd /d %BASE%\product-service && mvn spring-boot:run"

timeout /t 2 >nul
echo [gojava] 启动 order-service :8084
start "gojava-order"  cmd /k "set JAVA_HOME=%JAVA_HOME% && set PATH=%JAVA_HOME%\bin;%%PATH%% && cd /d %BASE%\order-service    && mvn spring-boot:run"

timeout /t 2 >nul
echo [gojava] 启动 tenant-service :8082
start "gojava-tenant" cmd /k "set JAVA_HOME=%JAVA_HOME% && set PATH=%JAVA_HOME%\bin;%%PATH%% && cd /d %BASE%\tenant-service    && mvn spring-boot:run"

echo 已在四个新窗口启动后端服务
