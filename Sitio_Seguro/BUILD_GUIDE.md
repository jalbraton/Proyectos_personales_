# 🏗️ Guía de Compilación APK - SecureVault

Esta guía te llevará paso a paso para compilar tu APK de SecureVault.

## 📋 Pre-requisitos

### Software Necesario
- ✅ **Android Studio** Hedgehog (2023.1.1) o superior
  - Descargar: https://developer.android.com/studio
- ✅ **JDK 17** (incluido con Android Studio)
- ✅ **Android SDK 34** (se instala con Android Studio)

### Verificar Instalación
```powershell
# Abrir PowerShell y verificar Java
java -version
# Debe mostrar: openjdk version "17.x.x"

# Verificar Android SDK (si está en PATH)
adb version
```

## 🎯 Método 1: Android Studio (MÁS FÁCIL)

### Paso 1: Abrir el Proyecto
1. Abre **Android Studio**
2. Clic en **File → Open**
3. Navega a: `C:\Users\JoseA\OneDrive\Documentos\Workspace_1\App_encrypt`
4. Clic en **OK**

### Paso 2: Sincronizar Gradle
1. Espera a que Android Studio indexe el proyecto (barra de progreso abajo)
2. Si aparece "Gradle Sync needed", clic en **Sync Now**
3. O manualmente: **File → Sync Project with Gradle Files**
4. Espera a que termine (puede tardar 2-5 minutos la primera vez)

### Paso 3: Compilar APK Debug
1. **Build → Build Bundle(s) / APK(s) → Build APK(s)**
2. Espera el proceso (1-3 minutos)
3. Verás notificación: "APK(s) generated successfully"
4. Clic en **locate** para ver el archivo

**Ubicación del APK:**
```
App_encrypt\app\build\outputs\apk\debug\app-debug.apk
```

### Paso 4 (Opcional): APK Release Firmado

#### 4.1 Crear Keystore (Solo Primera Vez)
1. **Build → Generate Signed Bundle / APK**
2. Selecciona **APK** → Next
3. Clic en **Create new...**
4. Completa los datos:
   ```
   Key store path: C:\Users\JoseA\securevault-keystore.jks
   Password: [Tu contraseña segura]
   Alias: securevault-key
   Password: [Misma contraseña o diferente]
   Validity (years): 25
   
   Certificate:
   First and Last Name: Jose Alberto Pastor
   Organization: Personal
   City: [Tu ciudad]
   State: [Tu estado]
   Country Code: ES (o tu país)
   ```
5. **OK** → **Guarda la contraseña en lugar seguro!**

#### 4.2 Firmar APK
1. Selecciona el keystore creado
2. Introduce passwords
3. **Next**
4. Build Variant: **release**
5. Signature Versions: Marca **V1** y **V2**
6. **Finish**
7. APK firmado en: `app\build\outputs\apk\release\app-release.apk`

## 💻 Método 2: Línea de Comandos

### En Windows PowerShell

```powershell
# Paso 1: Navegar al proyecto
cd "C:\Users\JoseA\OneDrive\Documentos\Workspace_1\App_encrypt"

# Paso 2: Dar permisos al gradlew (primera vez)
# (No necesario en Windows, pero por si acaso)

# Paso 3: Compilar APK Debug
.\gradlew assembleDebug

# Paso 4: Ver ubicación
echo "✅ APK generado en:"
echo "app\build\outputs\apk\debug\app-debug.apk"

# Abrir carpeta del APK
explorer.exe "app\build\outputs\apk\debug\"
```

### Comandos Útiles Adicionales

```powershell
# Limpiar compilación anterior
.\gradlew clean

# Compilar desde cero
.\gradlew clean assembleDebug

# Ver todas las tareas disponibles
.\gradlew tasks

# Compilar Release (sin firmar)
.\gradlew assembleRelease

# Ver versión de Gradle
.\gradlew --version

# Compilar con más información (debugging)
.\gradlew assembleDebug --info
```

### Solución de Problemas

```powershell
# Si Gradle está corrupto
.\gradlew --stop
.\gradlew clean
.\gradlew assembleDebug

# Si hay problemas de permisos
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# Verificar Java
java -version
# Debe ser JDK 17

# Si Java no se encuentra, configurar JAVA_HOME
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
```

## 📱 Instalar APK en Android

### Opción A: Transferencia Manual

1. **Conectar por USB**
   - Conecta tu Android por USB
   - Activa **Depuración USB** en:
     - Ajustes → Acerca del teléfono → Toca 7 veces "Número de compilación"
     - Ajustes → Opciones de desarrollador → Depuración USB (ON)

2. **Copiar APK**
   ```powershell
   # Copiar a descargas del teléfono
   adb push app\build\outputs\apk\debug\app-debug.apk /sdcard/Download/
   ```

3. **Instalar desde el teléfono**
   - Abre **Mis archivos** o **Archivos**
   - Navega a **Descargas**
   - Toca `app-debug.apk`
   - Permite instalación de orígenes desconocidos si pregunta
   - Toca **Instalar**

### Opción B: ADB Install (Directo)

```powershell
# Verificar que el dispositivo está conectado
adb devices

# Instalar directamente
adb install app\build\outputs\apk\debug\app-debug.apk

# Si ya está instalado, reinstalar
adb install -r app\build\outputs\apk\debug\app-debug.apk

# Desinstalar (si necesario)
adb uninstall com.securevault

# Abrir app después de instalar
adb shell am start -n com.securevault/.ui.MainActivity
```

### Opción C: Google Drive / Email

1. Sube el APK a Google Drive o envíalo por email
2. Descárgalo desde tu Android
3. Abre el archivo descargado
4. Instala (permite orígenes desconocidos si pregunta)

## ✅ Verificar Instalación

```powershell
# Verificar que la app está instalada
adb shell pm list packages | findstr securevault

# Ver información de la app
adb shell dumpsys package com.securevault

# Ver logs en tiempo real
adb logcat | findstr SecureVault
```

## 🐛 Solución de Problemas Comunes

### Error: "SDK location not found"
```powershell
# Crear local.properties
echo "sdk.dir=C:\\Users\\JoseA\\AppData\\Local\\Android\\Sdk" > local.properties
```

### Error: "Gradle version incompatible"
```powershell
# Actualizar Gradle Wrapper
.\gradlew wrapper --gradle-version=8.2
```

### Error: "Execution failed for task ':app:compileDebugKotlin'"
```powershell
# Invalidar cachés en Android Studio
# File → Invalidate Caches → Invalidate and Restart

# O desde terminal
.\gradlew clean
rd /s /q .gradle
rd /s /q app\build
.\gradlew assembleDebug
```

### Error: "Insufficient storage" al instalar
- Libera espacio en tu Android (al menos 100 MB)
- Desinstala la versión anterior primero

### APK instalado pero no aparece
```powershell
# Reinstalar forzando
adb install -r -d app\build\outputs\apk\debug\app-debug.apk
```

## 📊 Tamaños de APK Esperados

- **Debug APK**: ~5-8 MB (sin ProGuard)
- **Release APK**: ~3-5 MB (con ProGuard y compresión)

## 🔐 Notas de Seguridad

### Para Testing (Debug)
- El APK debug **NO está firmado para producción**
- Solo para pruebas personales
- No compartir en tiendas de apps

### Para Distribución (Release)
- Usa keystore para firmar
- **GUARDA TU KEYSTORE Y CONTRASEÑAS** en lugar seguro
- Si pierdes el keystore, no podrás actualizar la app
- Backup del keystore: Copia `securevault-keystore.jks` a USB/nube cifrada

## 📝 Checklist Final

Antes de compilar, verifica:

- [ ] Android Studio instalado y actualizado
- [ ] Proyecto sincronizado con Gradle (sin errores rojos)
- [ ] Java 17 configurado
- [ ] Permisos de almacenamiento concedidos en tu Android
- [ ] Depuración USB activada (para adb install)
- [ ] Espacio suficiente en Android (100 MB+)

## 🚀 Compilación Exitosa

Si todo funcionó, deberías ver:

```
BUILD SUCCESSFUL in 2m 34s
45 actionable tasks: 45 executed
✅ APK generado en: app\build\outputs\apk\debug\app-debug.apk
```

¡Listo para instalar en tu dispositivo Android! 🎉

---

**¿Problemas?** Revisa la sección de solución de problemas o verifica los logs con `.\gradlew assembleDebug --stacktrace`
