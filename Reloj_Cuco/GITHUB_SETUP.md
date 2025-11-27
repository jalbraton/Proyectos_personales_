# 📋 Guía para subir AlarmApp a GitHub

## Pasos para crear el repositorio en GitHub y subir el código

### 1. Crear el repositorio en GitHub

1. Ve a [GitHub](https://github.com) e inicia sesión
2. Haz clic en el botón `+` en la esquina superior derecha y selecciona `New repository`
3. Configura el repositorio:
   - **Repository name**: `alarmapp`
   - **Description**: "Aplicación de alarmas minimalista para Android con soporte para archivos específicos y modo aleatorio"
   - **Visibility**: Public (o Private si prefieres)
   - **NO marques** "Initialize this repository with a README" (ya tenemos uno)
   - Haz clic en `Create repository`

### 2. Conectar el repositorio local con GitHub

Abre PowerShell en la carpeta del proyecto y ejecuta:

```powershell
cd "C:\Users\JoseA\OneDrive\Documentos\Workspace_1\AlarmApp"

# Agregar el remote de GitHub (reemplaza 'jalbraton' con tu usuario si es diferente)
git remote add origin https://github.com/jalbraton/Reloj_cuco.git

# Verificar que se agregó correctamente
git remote -v

# Subir el código a GitHub
git branch -M main
git push -u origin main
```

### 3. Verificar la configuración de GitHub Actions

1. Ve a tu repositorio en GitHub: `https://github.com/jalbraton/alarmapp`
2. Ve a la pestaña `Actions`
3. Deberías ver el workflow "Android CI - Build APK"
4. El workflow se ejecutará automáticamente en cada push a `main`

### 4. Obtener la APK

Después de que el workflow se ejecute:

1. Ve a la pestaña `Actions` en tu repositorio
2. Haz clic en el último workflow ejecutado
3. Baja hasta `Artifacts` y descarga:
   - `alarmapp-debug.apk` - Versión de desarrollo
   - `alarmapp-release.apk` - Versión de release (sin firmar)

O también puedes encontrar las APKs en la sección `Releases`:
- Ve a `Releases` en la página principal del repositorio
- Descarga la APK desde la última versión

### 5. Instalar en tu dispositivo Android

1. Descarga la APK en tu dispositivo o transfiérela desde tu PC
2. Habilita "Instalar desde fuentes desconocidas" en Configuración
3. Abre el archivo APK y sigue las instrucciones de instalación

## 🔐 Configuración adicional (opcional)

### Firmar la APK de release

Para crear una APK firmada para publicar en Play Store:

1. Genera un keystore:
```bash
keytool -genkey -v -keystore alarmapp.keystore -alias alarmapp -keyalg RSA -keysize 2048 -validity 10000
```

2. Agrega esto a `app/build.gradle.kts`:
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("../alarmapp.keystore")
            storePassword = "tu_password"
            keyAlias = "alarmapp"
            keyPassword = "tu_password"
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            // ...
        }
    }
}
```

## 🎉 ¡Listo!

Tu app AlarmApp ahora está en GitHub con CI/CD automático. Cada vez que hagas un push a main, se generará automáticamente una nueva APK.

## 📱 Características implementadas

✅ Interfaz minimalista y cuidada
✅ Selección de archivo de audio específico (mp3, flac, wav, ogg, m4a, aac, wma)
✅ Modo aleatorio con carpeta de canciones
✅ Permisos modernos (Android 13+)
✅ Alarmas exactas con AlarmManager
✅ Servicio en foreground para reproducción
✅ Notificaciones
✅ Build automático en GitHub Actions

## 🚀 Próximas mejoras sugeridas

- [ ] Función de snooze (posponer)
- [ ] Etiquetas personalizadas para alarmas
- [ ] Repetir alarmas (diario, días específicos)
- [ ] Vibración personalizada
- [ ] Volumen gradual
- [ ] Tema oscuro/claro
- [ ] Widgets de pantalla principal
