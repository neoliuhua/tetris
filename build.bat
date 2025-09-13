@echo off
setlocal

echo ========================================
echo Classic Tetris Build Helper
echo ========================================
echo.

set JAVA_HOME=C:\Program Files\Java\jdk-22
set ANDROID_HOME=C:\Users\%USERNAME%\AppData\Local\Android\Sdk

rem Check Java installation
if not exist "%JAVA_HOME%" (
    echo ERROR: Java JDK not found at %JAVA_HOME%
    echo.
    echo Please install Java JDK 17 or later from:
    echo https://adoptium.net/
    echo.
    exit /b 1
)

echo ✓ Java found: %JAVA_HOME%

rem Check Android SDK
if not exist "%ANDROID_HOME%" (
    echo WARNING: Android SDK not found at %ANDROID_HOME%
    echo.
    echo Please install Android Studio from:
    echo https://developer.android.com/studio
    echo.
    echo After installation, Android Studio will setup the SDK automatically.
    echo.
)

rem Try to find gradle
set GRADLE_PATH=
if exist "%USERPROFILE%\.gradle\wrapper\dists\gradle-9.0.0-bin\*\gradle-9.0.0\bin\gradle.bat" (
    for /f "delims=" %%i in ('dir "%USERPROFILE%\.gradle\wrapper\dists\gradle-9.0.0-bin\*\gradle-9.0.0\bin\gradle.bat" /b /s 2^>nul') do set GRADLE_PATH=%%i
)

if "%GRADLE_PATH%"=="" (
    echo.
    echo ERROR: Gradle not found on this system.
    echo.
    echo RECOMMENDED SOLUTION:
    echo 1. Install Android Studio
    echo 2. Open the project in Android Studio
    echo 3. Android Studio will automatically setup Gradle
    echo.
    echo ALTERNATIVE:
    echo 1. Download Gradle from: https://gradle.org/install/
    echo 2. Extract and add to PATH
    echo 3. Run 'gradle build' manually
    echo.
    exit /b 1
)

echo ✓ Gradle found: %GRADLE_PATH%
echo.
echo Building project...
echo.

"%GRADLE_PATH%" build

if errorlevel 1 (
    echo.
    echo ❌ Build failed with error code %errorlevel%
    echo.
    echo TROUBLESHOOTING:
    echo 1. Make sure Android Studio is installed
    echo 2. Open project in Android Studio
    echo 3. Let IDE resolve dependencies
    echo.
    exit /b %errorlevel%
) else (
    echo.
    echo ✅ Build completed successfully!
    echo.
    echo Next steps:
    echo 1. Connect Android device or start emulator
    echo 2. Run: %GRADLE_PATH% installDebug
    echo 3. Enjoy Classic Tetris!
    echo.
    exit /b 0
)