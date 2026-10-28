@echo off
rem gojava web start script
cd /d %~dp0

where node >nul 2>nul
if errorlevel 1 (
    echo [gojava-web] ERROR: node.exe not found in PATH.
    echo [gojava-web] Please install Node.js 18+ from https://nodejs.org/
    pause
    exit /b 1
)

for /f "delims=" %%v in ('node -v') do set NODE_VER=%%v
echo [gojava-web] node version: %NODE_VER%

if not exist node_modules (
    echo [gojava-web] first run, installing dependencies...
    call npm install
    if errorlevel 1 (
        echo [gojava-web] npm install FAILED. If you see " engines " or " Vite requires " errors,
        echo [gojava-web] your Node is too old. Vite 5 needs Node.js 18+.
        pause
        exit /b 1
    )
)

echo [gojava-web] starting dev server at http://localhost:5173
start "gojava-web" cmd /k "npm run dev"
timeout /t 2 >nul
start http://localhost:5173
