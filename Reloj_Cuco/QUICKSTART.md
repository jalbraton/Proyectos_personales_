# 🚀 AlarmApp - Inicio Rápido

## ⚡ Subir a GitHub en 3 pasos

### Paso 1: Crear el repositorio en GitHub

1. Ve a https://github.com/new
2. Nombre del repositorio: `alarmapp`
3. Descripción: `Aplicación de alarmas para Android con modos específico y aleatorio`
4. **NO marques** ninguna opción de inicialización
5. Clic en "Create repository"

### Paso 2: Ejecutar el script de upload

Haz doble clic en: **`upload_to_github.ps1`**

O ejecuta en PowerShell:
```powershell
cd "C:\Users\JoseA\OneDrive\Documentos\Workspace_1\AlarmApp"
.\upload_to_github.ps1
```

El script te pedirá:
1. Tu nombre de usuario de GitHub (ejemplo: `jalbraton`)
2. Confirmación para subir los cambios

### Paso 3: Esperar el build automático

1. Ve a tu repositorio: `https://github.com/TU_USUARIO/alarmapp`
2. Haz clic en la pestaña **Actions**
3. Espera ~5 minutos a que termine el build
4. Ve a **Releases** y descarga la APK

## 🎉 ¡Listo!

Tu app está en GitHub con CI/CD automático.

---

## 📱 Compilar localmente (opcional)

Si quieres compilar en tu PC:

1. **Opción A - Con script**:
   - Doble clic en `build_local.bat`

2. **Opción B - Línea de comandos**:
   ```bash
   .\gradlew.bat assembleDebug
   ```

La APK estará en: `app\build\outputs\apk\debug\app-debug.apk`

---

## 📚 Documentación completa

- **README.md** - Documentación general
- **GITHUB_SETUP.md** - Guía detallada de GitHub
- **PROJECT_OVERVIEW.md** - Resumen técnico completo

---

## ❓ Problemas comunes

### "Git no reconocido"
- Instala Git: https://git-scm.com/download/win
- Reinicia PowerShell

### "No puedo ejecutar el script .ps1"
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### "Error al subir a GitHub"
- Verifica que creaste el repositorio en GitHub
- Verifica que iniciaste sesión en Git: `git config --global user.name "Tu Nombre"`

---

## 🎯 Características de la App

✅ Alarmas con hora exacta
✅ Modo canción específica (mp3, flac, wav, ogg, m4a, aac, wma)
✅ Modo aleatorio (carpeta de canciones)
✅ Interfaz minimalista Material Design
✅ Notificaciones modernas
✅ Compatible con Android 7+

---

**¿Listo? ¡Ejecuta `upload_to_github.ps1` y tu app estará en GitHub en minutos!** 🚀
