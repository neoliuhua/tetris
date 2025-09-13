@echo off
echo === Android USB Install Fix ===
echo.

REM Check ADB
echo 1. Checking ADB...
adb version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: ADB not found. Please install Android SDK.
    pause
    exit /b 1
)

echo ADB Version:
adb version
echo.

REM Check devices
echo 2. Checking connected devices...
adb devices
echo.

REM Check device state
echo 3. Device state:
adb get-state
echo.

REM Restart ADB server
echo 4. Restarting ADB server...
adb kill-server
adb start-server
timeout /t 3 /nobreak >nul

echo.
echo 5. Please check your phone:
echo    - Make sure USB debugging is enabled
echo    - Allow USB debugging when prompted
echo    - For Xiaomi/Redmi: enable "USB installation"
echo    - Allow installation from unknown sources
echo.

REM Try to install
echo 6. Attempting installation...
echo When prompted on your phone, tap "Allow" or "Install"
echo.

gradlew.bat installDebug --no-daemon --project-dir d:\elsfk

if %errorlevel% neq 0 (
    echo.
    echo Installation failed. Please:
    echo 1. Check if installation prompt appeared on phone
    echo 2. Enable "USB debugging" and "USB installation"
    echo 3. Use original USB cable
    echo 4. Disable any security apps blocking installation
    echo.
    pause
) else (
    echo.
    echo Installation successful!
    pause
)