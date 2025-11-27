@echo off
echo ===================================
echo  Verificando errores de compilacion
echo ===================================
cd /d "%~dp0"

echo.
echo [1/3] Limpiando build anterior...
call gradlew.bat clean

echo.
echo [2/3] Compilando proyecto...
call gradlew.bat assembleDebug --stacktrace

echo.
echo [3/3] Resultado:
if %ERRORLEVEL% EQU 0 (
    echo [OK] Compilacion exitosa!
    echo APK generado en: app\build\outputs\apk\debug\app-debug.apk
) else (
    echo [ERROR] Hubo errores de compilacion. Ver arriba para detalles.
)

pause
