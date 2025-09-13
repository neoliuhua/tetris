@echo off
echo === ADB Setup and Android USB Install Guide ===
echo.

REM Search for ADB in common locations
setlocal enabledelayedexpansion
echo Searching for ADB...
echo.

set "adb_found=0"
set "adb_path="

REM Common ADB locations
set "paths[0]=C:\Users\%USERNAME%\AppData\Local\Android\Sdk\platform-tools\adb.exe"
set "paths[1]=C:\Program Files\Android\Android Studio\platform-tools\adb.exe"
set "paths[2]=C:\Program Files (x86)\Android\android-sdk\platform-tools\adb.exe"
set "paths[3]=C:\Android\android-sdk\platform-tools\adb.exe"
set "paths[4]=D:\Android\android-sdk\platform-tools\adb.exe"

for /L %%i in (0,1,4) do (
    if exist "!paths[%%i]!" (
        set "adb_path=!paths[%%i]!"
        set "adb_found=1"
        echo Found ADB at: !paths[%%i]!
        goto :found_adb
n    )
)

:found_adb
if %adb_found%==1 (
    echo.
    echo Adding ADB to PATH temporarily...
    set "PATH=%PATH%;%~dp1"
    echo.
    echo Testing ADB...
    "%adb_path%" version
    if %errorlevel%==0 (
        echo.
        echo ADB is working!
        echo.
        echo Connected devices:
        "%adb_path%" devices
        echo.
        echo To install the app, please:
        echo 1. Make sure your phone is connected via USB
        echo 2. Enable USB debugging in Developer Options
        echo 3. When prompted on your phone, allow USB debugging
        echo 4. When installing, allow the app installation
        echo.
        echo Running installation...
        timeout /t 5 /nobreak >nul
        gradlew.bat installDebug --no-daemon --project-dir d:\elsfk
    ) else (
        echo ADB found but not working properly
    )
) else (
    echo ADB not found in common locations.
    echo.
    echo Please install Android SDK or Android Studio first:
    echo 1. Download Android Studio: https://developer.android.com/studio
    echo 2. Install Android SDK Platform Tools
    echo 3. Add SDK platform-tools to your PATH
    echo.
    echo Or download standalone SDK:
    echo https://developer.android.com/studio/releases/platform-tools
)

pause