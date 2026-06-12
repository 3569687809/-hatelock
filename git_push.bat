@echo off
chcp 65001 > nul
title Hatelock Git 发布工具

echo ==============================
echo   Hatelock Git 发布工具
echo ==============================
echo.

echo [1] 检查 Git 状态...
git rev-parse --is-inside-work-tree >nul 2>&1
if errorlevel 1 (
    echo ❌ 当前目录不是 Git 仓库
    pause
    exit /b
)

echo.
echo [当前修改文件]
git status -s

echo.
echo [2] 检查是否有改动...

git diff --quiet
if %errorlevel%==0 (
    echo 没有检测到任何改动，退出。
    pause
    exit /b
)

echo 有改动，继续...

echo.
set /p msg=请输入本次提交说明：

if "%msg%"=="" (
    echo ❌ 提交说明不能为空
    pause
    exit /b
)

echo.
echo [3] add...
git add .

echo.
echo [4] commit...
git commit -m "%msg%"

if errorlevel 1 (
    echo ❌ commit 失败
    pause
    exit /b
)

echo.
echo [5] push...
git push

if errorlevel 1 (
    echo ❌ push 失败
    pause
    exit /b
)

echo.
echo ==============================
echo         完成
echo ==============================
pause