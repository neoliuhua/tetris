@echo off
echo === Easy APK Install ===
echo.

echo APK is ready at: D:\elsfk\build\outputs\apk\debug\ClassicTetris-debug.apk
echo.
echo Opening APK folder for easy access...
echo.
echo To install on your phone:
echo 1. Connect phone with USB cable
echo 2. Select "File Transfer" mode on phone
echo 3. Copy the APK file to your phone
echo 4. Find and install the APK on your phone
echo.
echo Press any key to open the APK folder...

pause >nul
explorer "D:\elsfk\build\outputs\apk\debug\"
echo Done!