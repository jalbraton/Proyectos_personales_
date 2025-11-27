# 🧪 Guía de Testing y Debug - SecureVault

## 🎯 Probar la App ANTES de Instalar en tu Móvil

Esta guía te muestra cómo probar SecureVault de forma **100% segura** antes de instalarlo en tu dispositivo real.

---

## 🖥️ Opción 1: Emulador Android Studio (MEJOR OPCIÓN)

### Ventajas
- ✅ **100% seguro** - No toca tu móvil
- ✅ **Debugging completo** - Puedes ver logs en tiempo real
- ✅ **Reseteable** - Si algo falla, borras el emulador y creas otro
- ✅ **Gratis** - Incluido con Android Studio
- ✅ **Rápido** - Una vez instalado, es muy fluido

### Paso 1: Abrir Android Studio

```powershell
# Si no tienes Android Studio abierto
# Busca "Android Studio" en el menú inicio de Windows
```

1. Abre **Android Studio**
2. **File → Open** → Selecciona carpeta `App_encrypt`
3. Espera a que sincronice (barra de progreso abajo)

### Paso 2: Crear Emulador (Solo Primera Vez)

1. **Tools → Device Manager** (o ícono de teléfono 📱 en la barra)
2. Click en **Create Device**
3. Selecciona un dispositivo:
   - **Recomendado:** Pixel 6 o Pixel 7
   - Click **Next**
4. Selecciona imagen del sistema:
   - **API Level 34** (Android 14) ← Mejor opción
   - O **API Level 33** (Android 13)
   - O **API Level 30** (Android 11) ← Mínimo para probar bien
   - Click **Download** si no está descargado (tarda 5-10 min)
   - Click **Next**
5. Configurar AVD:
   ```
   AVD Name: Pixel_6_API_34_SecureVault
   Startup orientation: Portrait
   Graphics: Hardware - GLES 2.0
   
   [Show Advanced Settings]
   RAM: 2048 MB (mínimo) o 4096 MB (mejor)
   Internal Storage: 2048 MB (suficiente)
   SD Card: 512 MB (para guardar volúmenes)
   ```
6. Click **Finish**

### Paso 3: Ejecutar en Emulador

1. En Android Studio, arriba verás:
   ```
   [app] [Pixel_6_API_34_SecureVault ▼] [▶ Run]
   ```
2. Click en **▶ Run** (o `Shift + F10`)
3. El emulador se abrirá (tarda 30-60 segundos la primera vez)
4. La app se instalará automáticamente
5. ¡Ya puedes probar!

### Paso 4: Debugging en Tiempo Real

```powershell
# Ver logs de la app en tiempo real:
# En Android Studio:
View → Tool Windows → Logcat

# Filtrar por "SecureVault" para ver solo tus logs
# En el campo de búsqueda de Logcat escribe: SecureVault
```

**Logcat te mostrará:**
- ✅ Cuándo se crea un volumen
- ✅ Cuándo se abre/cierra
- ✅ Errores de cifrado (si hay)
- ✅ Problemas de memoria
- ✅ Crashes con stack trace completo

### Paso 5: Testing Manual en Emulador

**Test 1: Crear Volumen**
```
1. Abre SecureVault en el emulador
2. Concede permisos cuando lo pida
3. Toca "Crear Nuevo Volumen"
   - Nombre: TestVolumen
   - Tamaño: 10 MB (pequeño para probar rápido)
   - Contraseña: Test1234!@#$
   - Confirmar
4. Espera 10-20 segundos (10 MB es rápido)
5. ✅ Debe mostrar "Volumen creado exitosamente"
```

**Test 2: Abrir Volumen**
```
1. Toca "Abrir Volumen"
2. Selecciona "TestVolumen.svlt"
3. Introduce contraseña: Test1234!@#$
4. Toca "Abrir"
5. ✅ Debe abrir el explorador de archivos vacío
```

**Test 3: Agregar Archivo**
```
1. En el explorador, toca botón "+"
2. Selecciona cualquier archivo (imagen, documento, etc.)
3. Espera unos segundos
4. ✅ El archivo debe aparecer en la lista cifrado
```

**Test 4: Extraer Archivo**
```
1. Toca el icono de descarga ⬇️ del archivo
2. Confirma extracción
3. ✅ Debe mostrar ubicación del archivo extraído
4. Verifica con File Manager del emulador que está ahí
```

**Test 5: Eliminar Archivo**
```
1. Toca el icono de basura 🗑️
2. Confirma eliminación
3. ✅ El archivo debe desaparecer de la lista
```

**Test 6: Cerrar Volumen**
```
1. Toca "Cerrar Volumen"
2. Confirma
3. ✅ Debe volver a la pantalla principal
```

**Test 7: Reabrir Volumen (Persistencia)**
```
1. Abre el volumen de nuevo con la contraseña
2. ✅ Los archivos que agregaste deben seguir ahí (no el eliminado)
```

**Test 8: Contraseña Incorrecta**
```
1. Intenta abrir el volumen con contraseña incorrecta
2. ✅ Debe mostrar error "Contraseña incorrecta"
```

### Paso 6: Probar Escenarios de Error

**Test de Memoria:**
```
1. Crea un volumen de 500 MB
2. Agrega varios archivos grandes (50-100 MB)
3. Monitorea Logcat
4. ✅ NO debe haber OutOfMemoryError
5. ✅ La app debe seguir fluida
```

**Test de Session Timeout:**
```
1. Abre un volumen
2. Espera 5 minutos sin tocar nada
3. ✅ Al volver, debe haber cerrado automáticamente
```

**Test de Protección de Pantalla:**
```
1. Abre un volumen
2. Intenta hacer screenshot (Power + Volume Down en emulador)
3. ✅ La pantalla debe salir negra en el screenshot
```

---

## 📱 Opción 2: Dispositivo Real con USB Debugging (Seguro)

Si quieres probar en tu móvil PERO con red de seguridad:

### Preparación Segura

1. **Hacer Backup Completo:**
   ```
   Ajustes → Sistema → Copia de seguridad → Hacer copia ahora
   ```

2. **Activar Depuración USB:**
   ```
   Ajustes → Acerca del teléfono → Número de compilación (tocar 7 veces)
   Ajustes → Opciones de desarrollador → Depuración USB (ON)
   ```

3. **Conectar por USB:**
   ```powershell
   # Conecta el cable USB
   # En el móvil, acepta "Permitir depuración USB"
   
   # Verificar conexión:
   adb devices
   # Debe mostrar tu dispositivo
   ```

4. **Instalar con Logging:**
   ```powershell
   # Instalar APK
   adb install app\build\outputs\apk\debug\app-debug.apk
   
   # Ver logs en tiempo real
   adb logcat | Select-String "SecureVault"
   ```

5. **Testing con ADB:**
   ```powershell
   # Si la app se cuelga, puedes:
   
   # Ver logs del crash
   adb logcat -d > crash_log.txt
   
   # Forzar cierre
   adb shell am force-stop com.securevault
   
   # Desinstalar si hay problemas
   adb uninstall com.securevault
   
   # Reiniciar dispositivo remotamente
   adb reboot
   ```

### Testing Seguro en Móvil Real

**Crea un volumen de prueba PEQUEÑO:**
```
Nombre: TestPrueba
Tamaño: 10 MB ← PEQUEÑO para no ocupar espacio
Contraseña: Test1234!@#$
```

**Usa archivos NO importantes:**
- ❌ NO uses fotos personales
- ❌ NO uses documentos importantes
- ✅ Usa archivos de prueba (capturas viejas, archivos temporales)

**Monitorea todo:**
```powershell
# En otra ventana PowerShell, mantén abierto:
adb logcat | Select-String -Pattern "SecureVault|Error|Exception"
```

---

## 🐛 Opción 3: Debugging Avanzado

### Debug con Android Studio (Breakpoints)

1. En Android Studio, abre cualquier archivo `.kt`
2. Click en el margen izquierdo junto a una línea de código (aparece punto rojo 🔴)
3. Click en **🐞 Debug** (junto a Run)
4. La app se pausará en ese punto
5. Puedes:
   - Ver valores de variables
   - Ejecutar paso a paso
   - Inspeccionar memoria
   - Ver call stack

**Puntos clave para poner breakpoints:**
```kotlin
// En CreateVolumeActivity.kt - línea de createVolume()
// En AESCipher.kt - línea de encrypt()
// En VolumeManager.kt - línea de openVolume()
```

### Profiler de Android Studio

```
View → Tool Windows → Profiler
```

Monitorea:
- **CPU:** Debe estar bajo (~5-20%)
- **Memory:** Debe ser estable (50-100 MB)
- **Network:** Debe estar en 0 (la app no usa red)

---

## 🔍 Checklist de Testing Completo

### Tests Funcionales
- [ ] Crear volumen (10 MB, 100 MB, 1 GB)
- [ ] Abrir volumen con contraseña correcta
- [ ] Rechazar contraseña incorrecta
- [ ] Agregar archivo pequeño (1 MB)
- [ ] Agregar archivo mediano (10 MB)
- [ ] Agregar archivo grande (50 MB)
- [ ] Extraer archivo y verificar integridad
- [ ] Eliminar archivo
- [ ] Cerrar volumen
- [ ] Reabrir y verificar persistencia
- [ ] Crear múltiples volúmenes

### Tests de Seguridad
- [ ] Screenshot bloqueado (pantalla negra)
- [ ] Session timeout funciona (5 min)
- [ ] Memoria limpiada al cerrar volumen
- [ ] Contraseña débil rechazada (< 12 caracteres)
- [ ] Indicador de fuerza funciona
- [ ] Permisos solicitados correctamente

### Tests de Performance
- [ ] Crear volumen 100 MB < 2 minutos
- [ ] Abrir volumen < 5 segundos
- [ ] Cifrar archivo 10 MB < 10 segundos
- [ ] Descifrar archivo 10 MB < 10 segundos
- [ ] Sin memory leaks (Profiler estable)
- [ ] Sin crasheos durante 30 minutos de uso

### Tests de UI
- [ ] Todos los textos en español
- [ ] Colores Mint Linux correctos
- [ ] Botones responden al toque
- [ ] Progress bars aparecen en operaciones largas
- [ ] Diálogos de error legibles
- [ ] Teclado aparece en campos de texto
- [ ] Scroll funciona en lista de archivos
- [ ] Rotación de pantalla no crashea (opcional)

### Tests de Edge Cases
- [ ] Volumen lleno (no se puede agregar más)
- [ ] Archivo más grande que espacio libre
- [ ] Cerrar app durante creación de volumen
- [ ] Permisos denegados
- [ ] Almacenamiento del dispositivo lleno
- [ ] Contraseña con emojis (debe funcionar)
- [ ] Nombre de volumen con caracteres especiales

---

## 🚨 Qué Hacer Si Encuentras Bugs

### Bug Menor (UI, textos)
```
1. Anota el problema
2. Toma screenshot
3. Copia logs de Logcat
4. Puedes seguir probando
```

### Bug Grave (Crash, pérdida de datos)
```
1. ¡NO instales en tu móvil todavía!
2. Guarda logs completos:
   adb logcat -d > bug_report.txt
3. Anota los pasos exactos para reproducir
4. Comparte los logs para análisis
```

### Logs Importantes

```powershell
# Capturar todo en un archivo
adb logcat -d > full_log.txt

# Solo errores
adb logcat *:E > errors_only.txt

# Solo SecureVault
adb logcat | Select-String "SecureVault" > securevault_log.txt
```

---

## 📊 Verificación de Integridad de Archivos

### Test de Cifrado Correcto

1. **Preparación:**
   - Crea un archivo de texto con contenido conocido
   - Ejemplo: `test.txt` con "HOLA MUNDO 123"
   - Calcula hash: `Get-FileHash test.txt -Algorithm SHA256`

2. **Agregar al volumen:**
   - Agrega `test.txt` a SecureVault
   - El archivo se cifra

3. **Extraer del volumen:**
   - Extrae el archivo como `test_extracted.txt`
   - Calcula hash: `Get-FileHash test_extracted.txt -Algorithm SHA256`

4. **Verificar:**
   ```powershell
   # Comparar hashes
   $original = Get-FileHash test.txt -Algorithm SHA256
   $extracted = Get-FileHash test_extracted.txt -Algorithm SHA256
   
   if ($original.Hash -eq $extracted.Hash) {
       Write-Host "✅ Archivo idéntico - Cifrado correcto" -ForegroundColor Green
   } else {
       Write-Host "❌ Archivo corrupto - HAY UN BUG" -ForegroundColor Red
   }
   ```

### Script de Verificación Automática

```powershell
# verificar_integridad.ps1
# Guarda este script en la carpeta App_encrypt

$testFile = "test_integrity.txt"
$testContent = "SecureVault Integrity Test - " + (Get-Date).ToString()

# Crear archivo de prueba
Set-Content -Path $testFile -Value $testContent
$hashOriginal = (Get-FileHash $testFile -Algorithm SHA256).Hash

Write-Host "1. Archivo creado: $testFile" -ForegroundColor Cyan
Write-Host "   Hash original: $hashOriginal" -ForegroundColor Gray
Write-Host ""
Write-Host "2. Ahora:" -ForegroundColor Yellow
Write-Host "   - Abre SecureVault" -ForegroundColor White
Write-Host "   - Agrega el archivo $testFile al volumen" -ForegroundColor White
Write-Host "   - Extráelo de nuevo" -ForegroundColor White
Write-Host "   - Nómbralo 'test_integrity_extracted.txt'" -ForegroundColor White
Write-Host ""
Write-Host "3. Presiona ENTER cuando esté listo para verificar..." -ForegroundColor Yellow
Read-Host

$extractedFile = "test_integrity_extracted.txt"
if (Test-Path $extractedFile) {
    $hashExtracted = (Get-FileHash $extractedFile -Algorithm SHA256).Hash
    
    Write-Host ""
    Write-Host "Hash original:  $hashOriginal" -ForegroundColor Gray
    Write-Host "Hash extraído:  $hashExtracted" -ForegroundColor Gray
    Write-Host ""
    
    if ($hashOriginal -eq $hashExtracted) {
        Write-Host "✅ ¡PERFECTO! El cifrado funciona correctamente" -ForegroundColor Green
        Write-Host "   Los archivos son idénticos byte por byte" -ForegroundColor Green
    } else {
        Write-Host "❌ ERROR: Los archivos son diferentes" -ForegroundColor Red
        Write-Host "   Hay un problema con el cifrado/descifrado" -ForegroundColor Red
    }
} else {
    Write-Host "❌ No se encontró el archivo extraído" -ForegroundColor Red
    Write-Host "   Asegúrate de haberlo nombrado correctamente" -ForegroundColor Yellow
}
```

---

## ✅ Criterios para Instalar en Móvil Real

**Solo instala en tu móvil si TODO esto pasa:**

- ✅ **Funcionalidad:** Todos los tests funcionales pasan
- ✅ **Estabilidad:** Cero crashes en 30 minutos de uso
- ✅ **Performance:** Operaciones en tiempo razonable
- ✅ **Memoria:** Sin leaks (Profiler estable)
- ✅ **Seguridad:** Todos los tests de seguridad pasan
- ✅ **Integridad:** Archivos extraídos = archivos originales (mismo hash)
- ✅ **UI:** Interfaz fluida y responsive

---

## 🎓 Tips Finales

### Para Testing Rápido

```powershell
# Crear volumen pequeño para pruebas rápidas
Tamaño: 5-10 MB
Tiempo creación: ~10 segundos
Perfecto para iterar rápido
```

### Para Testing Completo

```powershell
# Crear volumen realista
Tamaño: 100-500 MB
Agregar 10-20 archivos reales
Usar durante varios días en emulador
```

### Automatización (Avanzado)

```powershell
# UI Automator para tests automáticos
# Esto requiere configuración adicional pero es posible

.\gradlew connectedAndroidTest
# Ejecuta tests instrumentados si los creas
```

---

## 📞 Resumen Ejecutivo

**Para probar de forma 100% segura:**

1. **Usa el emulador de Android Studio** (Pixel 6, API 34)
2. **Compila en debug:** `.\gradlew assembleDebug`
3. **Run en emulador:** Click en ▶ Run
4. **Prueba todo:** Crear/abrir/agregar/extraer/eliminar
5. **Monitorea Logcat:** Ve logs en tiempo real
6. **Verifica integridad:** Usa el script de verificación
7. **Solo si TODO funciona:** Instala en tu móvil

**Señales de que está listo para tu móvil:**
- ✅ Cero crashes en emulador
- ✅ Archivos se cifran/descifran correctamente
- ✅ Performance aceptable
- ✅ UI fluida sin bugs

**Señales de que NO está listo:**
- ❌ Crashes frecuentes
- ❌ Archivos corruptos al extraer
- ❌ Memory leaks visibles
- ❌ UI que no responde

---

**¡Testing primero, instalación después! Tu móvil te lo agradecerá.** 🛡️
