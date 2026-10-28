@echo off
rem gojava web stop script
echo [gojava-web] stopping dev server on port 5173...

for /f "tokens=5" %%a in ('netstat -ano ^| findstr :5173 ^| findstr LISTENING') do (
    echo   killing PID %%a
    taskkill /F /PID %%a >nul 2>&1
)

taskkill /F /IM node.exe >nul 2>&1
echo [gojava-web] stopped.
timeout /t 1 >nul
