@echo off
chcp 65001 >nul
title 资产管理系统一键启动【后端 + 前端】

echo ==============================================
echo      MySQL已经设置开机自动启动，无需手动开启
echo ==============================================

echo.
echo ==============================================
echo            正在启动 SpringBoot 后端
echo ==============================================
cd /d "%~dp0backend\target"
start "后端服务" cmd /k "java -jar asset-management-1.0.0.jar"

timeout /t 4 /nobreak >nul

echo.
echo ==============================================
echo            正在启动 Vue 前端
echo ==============================================
cd /d "%~dp0frontend"
start "前端服务" cmd /k "npm run dev"

:: 等待前端服务初始化，可根据你的启动速度增减秒数
timeout /t 3 /nobreak >nul

echo.
echo ✅ 全部启动命令执行完毕！
echo 后端地址：http://localhost:8080
echo 前端地址：http://localhost:5173
echo 正在自动打开浏览器访问前端页面...
start http://localhost:5173
echo ==============================================
pause
