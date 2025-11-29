@echo off
echo ========================================
echo   AlarmApp - Build Script
echo ========================================
echo.

cd /d "%~dp0"

echo Verificando Gradle...
if not exist "gradlew.bat" (
    echo ERROR: gradlew.bat no encontrado
    pause
    exit /b 1
)

echo.
echo Limpiando proyecto...
call gradlew.bat clean

echo.
echo Compilando APK Debug...
call gradlew.bat assembleDebug

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo   Build exitoso!
    echo ========================================
    echo.
    echo La APK debug esta en:
    echo   app\build\outputs\apk\debug\app-debug.apk
    echo.
    explorer.exe "app\build\outputs\apk\debug"
) else (
    echo.
    echo ========================================
    echo   Error en el build
    echo ========================================
    echo.
)

pause
