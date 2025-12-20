@echo off
echo ========================================
echo PDF to TXT Converter - Iniciando...
echo ========================================
echo.

REM Verificar si Python está instalado
python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Python no está instalado o no está en el PATH
    echo Por favor instala Python 3.8 o superior
    pause
    exit /b 1
)

REM Verificar si las dependencias están instaladas
python -c "import streamlit" >nul 2>&1
if %errorlevel% neq 0 (
    echo Instalando dependencias...
    pip install -r requirements.txt
    if %errorlevel% neq 0 (
        echo ERROR: No se pudieron instalar las dependencias
        pause
        exit /b 1
    )
)

echo.
echo Iniciando aplicación web...
echo La aplicación se abrirá en tu navegador en unos segundos
echo Presiona Ctrl+C para detener el servidor
echo.
echo ========================================
echo.

streamlit run app.py

pause
