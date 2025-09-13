@echo off
echo === 一键安装俄罗斯方块游戏 ===
echo.

REM 检查APK是否存在
set "apk_path=D:\elsfk\build\outputs\apk\debug\ClassicTetris-debug.apk"
if not exist "%apk_path%" (
    echo 正在构建APK...
    gradlew.bat assembleDebug --no-daemon --project-dir d:\elsfk
    if %errorlevel% neq 0 (
        echo 构建失败！请检查错误信息
        pause
        exit /b 1
    )
)

echo APK文件已准备好: %apk_path%
echo.

REM 检查ADB
echo 检查ADB环境...
where adb >nul 2>&1
if %errorlevel% neq 0 (
    echo 警告: ADB未找到，将使用手动安装方法
    echo.
    echo === 手动安装步骤 ===
    echo 1. 用USB线连接手机
    echo 2. 选择"文件传输"模式
    echo 3. 复制APK到手机
    echo 4. 在手机上安装APK
    echo.
    echo APK位置: %apk_path%
    explorer /select,"%apk_path%"
    pause
    exit /b 0
)

echo ADB已找到，检查设备连接...
echo.
echo 当前连接的设备:
adb devices
echo.

echo === 安装选项 ===
echo 1. 自动安装 (需要手机已连接并授权)
echo 2. 手动安装 (推荐)
echo 3. 仅打开APK文件夹

set /p choice=请选择安装方式(1/2/3): 

if "%choice%"=="1" (
    echo 正在尝试自动安装...
    echo 请在手机上确认安装提示！
    adb install -r "%apk_path%"
    if %errorlevel%==0 (
        echo 安装成功！
    ) else (
        echo 自动安装失败，请使用手动安装
        pause
    )
) else if "%choice%"=="2" (
    echo === 手动安装步骤 ===
    echo 1. 用USB线连接手机
    echo 2. 选择"文件传输"模式
    echo 3. 复制APK到手机
    echo 4. 在手机上安装APK
    echo.
    echo 正在打开APK文件夹...
    explorer /select,"%apk_path%"
) else if "%choice%"=="3" (
    explorer /select,"%apk_path%"
) else (
    echo 无效选择
)

pause