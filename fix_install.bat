@echo off
echo === 修复Android USB安装问题 ===
echo.

REM 检查ADB是否可用
echo 1. 检查ADB环境...
adb version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: ADB未找到，请确保Android SDK已安装
    pause
    exit /b 1
)

echo ADB版本信息:
adb version
echo.

REM 检查连接的设备
echo 2. 检查连接的设备...
adb devices
echo.

REM 检查设备授权状态
echo 3. 检查设备授权状态...
adb get-state
echo.

REM 尝试重新授权
echo 4. 重新授权ADB连接...
echo 请在手机上确认"允许USB调试"提示...
adb kill-server
adb start-server
timeout /t 3 /nobreak >nul

REM 检查开发者选项设置
echo 5. 检查设备设置...
echo 请确保手机上已启用以下选项：
echo   - 开发者选项
echo   - USB调试
echo   - 允许安装未知来源应用
echo   - (小米/红米) 需要额外开启"USB安装"和"USB调试(安全设置)"
echo.

REM 尝试重新安装
echo 6. 重新尝试安装...
echo 如果看到安装提示，请在手机上点击"允许"或"安装"
call gradlew.bat installDebug --no-daemon --project-dir d:\elsfk

if %errorlevel% neq 0 (
    echo.
    echo 安装仍然失败，请检查：
    echo 1. 手机是否弹出安装确认对话框
    echo 2. 是否启用了"USB调试"和"USB安装"
    echo 3. 是否使用了原装数据线
    echo 4. 是否关闭了手机管家类应用的拦截
    echo.
    echo 小米/红米手机额外检查：
    echo - 设置 → 更多设置 → 开发者选项 → USB安装
    echo - 设置 → 更多设置 → 开发者选项 → USB调试(安全设置)
    pause
) else (
    echo.
    echo 安装成功！
    pause
)