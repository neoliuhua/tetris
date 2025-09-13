@echo off
echo 正在通过USB安装俄罗斯方块游戏到手机...
echo.
echo 请确保：
echo 1. 手机已开启USB调试模式
echo 2. 手机已通过USB连接到电脑
echo 3. 已安装手机驱动程序
echo.

:: 检查ADB是否可用
adb devices
if %errorlevel% neq 0 (
    echo 错误：ADB未找到或未正确配置
    echo 请确保Android SDK已安装并配置了环境变量
    pause
    exit /b 1
)

echo.
echo 正在构建并安装应用...

:: 使用Gradle构建并安装
call gradlew.bat installDebug

if %errorlevel% neq 0 (
    echo.
    echo 安装失败！请检查：
    echo 1. 手机是否已连接并被识别
    echo 2. 是否已开启USB调试
    echo 3. 是否允许了USB安装
    pause
    exit /b 1
)

echo.
echo 安装成功！
echo 请在手机上查看应用列表，找到"Classic Tetris"
pause