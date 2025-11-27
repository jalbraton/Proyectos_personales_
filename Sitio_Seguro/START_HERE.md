# 🚀 START HERE - SecureVault

## ⚡ Compilación Rápida (2 minutos)

### Windows PowerShell (Recomendado)
```powershell
cd "C:\Users\JoseA\OneDrive\Documentos\Workspace_1\App_encrypt"
.\build.ps1
# Selecciona opción 1 (Compilar APK Debug)
```

### Windows CMD
```cmd
cd C:\Users\JoseA\OneDrive\Documentos\Workspace_1\App_encrypt
build.bat
```

### Línea de Comandos Directo
```powershell
.\gradlew assembleDebug
```

**APK generado en:** `app\build\outputs\apk\debug\app-debug.apk`

---

## 📱 Instalar en Android

### Método 1: ADB (Más Rápido)
```powershell
adb install app\build\outputs\apk\debug\app-debug.apk
```

### Método 2: Manual
1. Copia `app-debug.apk` a tu teléfono (USB/Bluetooth/Drive)
2. Abre el APK en tu Android
3. Permite "Orígenes desconocidos" si pregunta
4. Toca "Instalar"

---

## 🎯 Primer Uso

1. **Abrir SecureVault** (icono verde con candado)
2. **Conceder permisos** de almacenamiento cuando lo pida
3. **Crear volumen:**
   - Toca "Crear Nuevo Volumen"
   - Nombre: `Test` 
   - Tamaño: `50` MB
   - Contraseña: `Test1234!@#$` (mínimo 12 caracteres)
   - Confirma contraseña
   - Espera 1 minuto
4. **Abrir volumen:**
   - Toca "Abrir Volumen"
   - Selecciona `Test.svlt`
   - Introduce contraseña
5. **Agregar archivo:**
   - Toca botón verde "+"
   - Selecciona cualquier archivo
   - ¡Se cifra automáticamente!
6. **Cerrar volumen:**
   - Toca "Cerrar Volumen"
   - Confirma

---

## 📚 Documentación Completa

- **README.md** - Vista general completa
- **BUILD_GUIDE.md** - Guía detallada de compilación
- **SECURITY_GUIDE.md** - Mejores prácticas de seguridad
- **PROJECT_SUMMARY.md** - Resumen técnico completo
- **IMPLEMENTATION_GUIDE.md** - Detalles de implementación
- **QUICK_START.md** - Inicio rápido de usuario

---

## 🔒 Seguridad

SecureVault usa:
- ✅ **AES-256-XTS** (cifrado militar)
- ✅ **PBKDF2** con 100,000 iteraciones
- ✅ **Limpieza automática** de memoria
- ✅ **Session timeout** (5 minutos)
- ✅ **Protección de screenshots**

**¡Usa contraseñas fuertes!** Mínimo 16 caracteres con mayúsculas, minúsculas, números y símbolos.

---

## ✅ Checklist Rápido

Antes de compilar:
- [ ] Android Studio instalado (opcional, pero recomendado)
- [ ] Java 17 (incluido con Android Studio)
- [ ] 5 GB de espacio libre

Para instalar en Android:
- [ ] Android 8.0 o superior
- [ ] Depuración USB activada (para adb)
- [ ] 100 MB de espacio libre

---

## 🆘 Problemas Comunes

**Error: "SDK location not found"**
```powershell
echo "sdk.dir=C:\\Users\\JoseA\\AppData\\Local\\Android\\Sdk" > local.properties
```

**Error: "gradlew no reconocido"**
- Ejecuta desde la carpeta `App_encrypt\`
- Usa `.\gradlew` (con punto y barra)

**Error al instalar: "App not installed"**
- Verifica espacio en Android
- Desinstala versión anterior primero
- Permite "Orígenes desconocidos"

---

## 🎉 ¡Listo!

Si todo funcionó, tienes:
- ✅ APK compilado (~5-8 MB)
- ✅ App instalada en tu Android
- ✅ Cifrado AES-256 funcionando
- ✅ Interfaz Mint Linux limpia y moderna

**¿Necesitas ayuda?** Lee `BUILD_GUIDE.md` para solución de problemas detallada.

---

**Desarrollado por José Alberto Pastor Llorente**  
**Versión 1.0.0 - 2024**
