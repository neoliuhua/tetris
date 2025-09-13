@echo off
echo 检查ADB环境和设备连接状态...
echo.

:: 检查ADB命令
where adb >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ ADB命令未找到
    echo.
    echo 请确保：
    echo 1. Android SDK已安装
    echo 2. SDK的platform-tools目录已添加到PATH环境变量
    echo.
    echo 当前PATH包含：
    echo %PATH%
    echo.
    pause
    exit /b 1
)

echo ✅ ADB命令已找到
adb version
echo.

echo 检查已连接的设备...
echo.
adb devices
echo.

:: 检查项目构建状态
echo 检查项目配置...
if exist gradlew.bat (
    echo ✅ Gradle Wrapper已找到
) else (
    echo ❌ Gradle Wrapper未找到
)

if exist local.properties (
    echo ✅ local.properties已找到
    type local.properties | findstr "sdk.dir"
) else (
    echo ⚠️  local.properties未找到，可能需要配置SDK路径
)

echo.
echo 按任意键继续...
pause