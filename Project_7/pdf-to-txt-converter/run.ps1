# PDF to TXT Converter - PowerShell Launcher

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "PDF to TXT Converter - Iniciando..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Verificar Python
try {
    $pythonVersion = python --version 2>&1
    Write-Host "✓ Python encontrado: $pythonVersion" -ForegroundColor Green
} catch {
    Write-Host "✗ ERROR: Python no está instalado" -ForegroundColor Red
    Write-Host "Por favor instala Python 3.8 o superior desde python.org" -ForegroundColor Yellow
    Read-Host "Presiona Enter para salir"
    exit 1
}

# Verificar dependencias
Write-Host "Verificando dependencias..." -ForegroundColor Yellow
try {
    python -c "import streamlit" 2>$null
    if ($LASTEXITCODE -ne 0) {
        throw "Streamlit no instalado"
    }
    Write-Host "✓ Dependencias verificadas" -ForegroundColor Green
} catch {
    Write-Host "Instalando dependencias..." -ForegroundColor Yellow
    pip install -r requirements.txt
    if ($LASTEXITCODE -ne 0) {
        Write-Host "✗ ERROR: No se pudieron instalar las dependencias" -ForegroundColor Red
        Read-Host "Presiona Enter para salir"
        exit 1
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Iniciando aplicación web..." -ForegroundColor Green
Write-Host "La aplicación se abrirá en tu navegador" -ForegroundColor Green
Write-Host "Presiona Ctrl+C para detener el servidor" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Iniciar Streamlit
streamlit run app.py

Read-Host "Presiona Enter para salir"
