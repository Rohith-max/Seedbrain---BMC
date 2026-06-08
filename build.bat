@echo off
cd /d "C:\2026proj\R&D AND RESEARCH PAPER\seedbrain app"
call gradlew.bat assembleDebug
echo Build complete - check app\build\outputs\apk\debug\ for APK
