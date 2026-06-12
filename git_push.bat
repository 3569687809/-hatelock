@echo off
chcp 65001 > nul

echo ========================
echo Hatelock Git 自动上传
echo ========================
echo.

set /p msg=请输入本次更新说明：

if "%msg%"=="" (
echo.
echo 更新说明不能为空！
pause
exit /b
)

echo.
echo [1/3] 添加文件...
git add .

echo.
echo [2/3] 提交更新...
git commit -m "%msg%"

echo.
echo [3/3] 上传到 GitHub...
git push

echo.
echo ========================
echo 上传完成
echo ========================
pause
