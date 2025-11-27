@echo off
chcp 65001 >nul
echo.
echo ═══════════════════════════════════════════════════════
echo     TRADUCTOR DE EPUB - Instalación Automática
echo ═══════════════════════════════════════════════════════
echo.

echo [1/2] Verificando Python...
python --version >nul 2>&1
if errorlevel 1 (
    echo ✗ Python no está instalado o no está en el PATH
    echo   Por favor, instala Python 3.7 o superior desde https://python.org
    pause
    exit /b 1
)
python --version
echo ✓ Python detectado correctamente
echo.

echo [2/2] Instalando dependencias...
pip install -r requirements.txt
if errorlevel 1 (
    echo ✗ Error al instalar dependencias
    pause
    exit /b 1
)
echo.
echo ✓ Instalación completada exitosamente!
echo.
echo ═══════════════════════════════════════════════════════
echo Para ejecutar el traductor, usa:
echo     python traductor_epub.py
echo ═══════════════════════════════════════════════════════
echo.
pause
