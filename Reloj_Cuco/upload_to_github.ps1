# Script para subir AlarmApp a GitHub
# Asegúrate de haber creado el repositorio en GitHub primero

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  AlarmApp - GitHub Upload Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Verificar si estamos en el directorio correcto
$currentDir = Get-Location
if ($currentDir.Path -notlike "*AlarmApp*") {
    Write-Host "ERROR: Ejecuta este script desde la carpeta AlarmApp" -ForegroundColor Red
    exit 1
}

# Verificar si Git está instalado
try {
    git --version | Out-Null
} catch {
    Write-Host "ERROR: Git no está instalado o no está en el PATH" -ForegroundColor Red
    exit 1
}

# Solicitar el nombre de usuario de GitHub
Write-Host "Ingresa tu nombre de usuario de GitHub:" -ForegroundColor Yellow
$username = Read-Host

if ([string]::IsNullOrWhiteSpace($username)) {
    Write-Host "ERROR: El nombre de usuario no puede estar vacío" -ForegroundColor Red
    exit 1
}

# Verificar si ya existe un remote
$existingRemote = git remote get-url origin 2>$null

if ($existingRemote) {
    Write-Host "Ya existe un remote configurado: $existingRemote" -ForegroundColor Yellow
    Write-Host "¿Deseas reemplazarlo? (S/N)" -ForegroundColor Yellow
    $replace = Read-Host
    
    if ($replace -eq "S" -or $replace -eq "s") {
        git remote remove origin
        Write-Host "Remote anterior eliminado" -ForegroundColor Green
    } else {
        Write-Host "Operación cancelada" -ForegroundColor Yellow
        exit 0
    }
}

# Configurar el remote
$repoUrl = "https://github.com/$username/alarmapp.git"
Write-Host ""
Write-Host "Configurando remote: $repoUrl" -ForegroundColor Cyan

git remote add origin $repoUrl

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: No se pudo agregar el remote" -ForegroundColor Red
    exit 1
}

Write-Host "Remote configurado correctamente" -ForegroundColor Green

# Verificar estado de Git
Write-Host ""
Write-Host "Verificando estado del repositorio..." -ForegroundColor Cyan
git status --short

# Renombrar rama a main si es necesario
$currentBranch = git branch --show-current
if ($currentBranch -ne "main") {
    Write-Host ""
    Write-Host "Renombrando rama $currentBranch a main..." -ForegroundColor Cyan
    git branch -M main
}

# Preguntar si desea hacer push
Write-Host ""
Write-Host "¿Deseas subir los cambios a GitHub ahora? (S/N)" -ForegroundColor Yellow
$doPush = Read-Host

if ($doPush -eq "S" -or $doPush -eq "s") {
    Write-Host ""
    Write-Host "Subiendo código a GitHub..." -ForegroundColor Cyan
    git push -u origin main
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "========================================" -ForegroundColor Green
        Write-Host "  ¡Código subido exitosamente!" -ForegroundColor Green
        Write-Host "========================================" -ForegroundColor Green
        Write-Host ""
        Write-Host "Tu repositorio está en:" -ForegroundColor Cyan
        Write-Host "  https://github.com/$username/alarmapp" -ForegroundColor White
        Write-Host ""
        Write-Host "Las APKs se generarán automáticamente en:" -ForegroundColor Cyan
        Write-Host "  https://github.com/$username/alarmapp/actions" -ForegroundColor White
        Write-Host ""
        Write-Host "Y estarán disponibles en:" -ForegroundColor Cyan
        Write-Host "  https://github.com/$username/alarmapp/releases" -ForegroundColor White
        Write-Host ""
    } else {
        Write-Host ""
        Write-Host "ERROR: Hubo un problema al subir el código" -ForegroundColor Red
        Write-Host "Asegúrate de que:" -ForegroundColor Yellow
        Write-Host "  1. El repositorio 'alarmapp' existe en GitHub" -ForegroundColor Yellow
        Write-Host "  2. Tienes permisos de escritura en el repositorio" -ForegroundColor Yellow
        Write-Host "  3. Tu autenticación de Git está configurada" -ForegroundColor Yellow
    }
} else {
    Write-Host ""
    Write-Host "Push cancelado. Puedes subirlo manualmente con:" -ForegroundColor Yellow
    Write-Host "  git push -u origin main" -ForegroundColor White
}

Write-Host ""
Write-Host "Presiona Enter para salir..." -ForegroundColor Gray
Read-Host
