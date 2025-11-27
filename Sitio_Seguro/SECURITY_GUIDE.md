# 🔐 Guía de Seguridad y Mejores Prácticas - SecureVault

## 🎯 Introducción

SecureVault implementa encriptación de **nivel militar** usando estándares probados y seguros. Sin embargo, la seguridad final depende también de cómo uses la aplicación.

## 🔑 Contraseñas Maestras

### ✅ Contraseña FUERTE (Recomendado)

```
Ejemplos de contraseñas fuertes:
✅ M1Cl@v3S3gur@2024!
✅ P@ssw0rd#Fuerte$789
✅ S3cur3V@ult!2024$
✅ Encr1pt@M0v1l#2024

Características:
• Mínimo 16 caracteres (app requiere 12, pero usa más)
• Mayúsculas y minúsculas mezcladas
• Números
• Símbolos especiales (!@#$%^&*)
• No uses palabras del diccionario
• No uses fechas de cumpleaños
• No uses nombres de familiares
```

### ❌ Contraseñas DÉBILES (NO usar)

```
❌ password123
❌ 12345678
❌ miclave
❌ nombreperro2024
❌ 01/01/1990
```

### 📝 Cómo Crear Contraseñas Memorables y Fuertes

**Método de Frase:**
```
Frase: "Mi perro Toby nació en 2015 en Madrid"
Contraseña: MpTn@2015!eM

Frase: "Compré mi primer iPhone 12 en Navidad 2020"
Contraseña: Cmp1^iP12eN2020!
```

**Generador de Contraseñas:**
- Windows: `pwsh -Command "Add-Type -AssemblyName System.Web; [System.Web.Security.Membership]::GeneratePassword(16,4)"`
- Linux/Mac: `openssl rand -base64 16`

### 💾 Guardar Contraseñas Seguramente

**✅ Recomendado:**
- **Gestor de contraseñas:** Bitwarden, 1Password, KeePassXC
- **Papel físico:** Escrito en papel, guardado en caja fuerte
- **Cifrado adicional:** Archivo de texto cifrado con GPG/PGP

**❌ NO Recomendado:**
- Notas sin cifrar en el teléfono
- Email sin cifrar
- WhatsApp/Telegram
- Screenshots guardados en galería

## 📦 Seguridad de los Volúmenes

### Ubicación Segura

```
✅ SEGURO:
• Almacenamiento interno cifrado del dispositivo
• MicroSD con cifrado de hardware (si disponible)
• Backup en nube cifrada (Google Drive con cifrado extra)
• Disco duro externo cifrado

❌ INSEGURO:
• Carpeta de descargas pública
• MicroSD sin cifrado en dispositivo compartido
• Nube sin cifrado adicional
• USB sin cifrado físico
```

### Backup de Volúmenes

```powershell
# Copiar volumen a ubicación segura
# Ejemplo en Windows:
Copy-Item "C:\...\Mi_Volumen_Seguro.svlt" -Destination "E:\Backup_Seguro\"

# En Android:
# Usa un explorador de archivos para copiar .svlt a:
# - Google Drive (carpeta privada)
# - MicroSD cifrada
# - PC vía USB
```

**Importante:**
- Haz backup **ANTES** de actualizaciones del sistema
- Verifica que el backup funciona (intenta abrirlo)
- Mantén al menos 2 copias en ubicaciones diferentes
- Cifra el backup si lo subes a la nube

## 🛡️ Configuración de Seguridad

### Configuración Óptima en la App

```
Ajustes recomendados:
✅ Timeout de sesión: 5 minutos (default)
   Para más seguridad: 2 minutos
   Para comodidad: 10 minutos

✅ Protección de pantalla: Activado (default)
   Previene screenshots y grabaciones

✅ Limpiar memoria: Automático (siempre activo)
   Borra claves de RAM al cerrar volúmenes
```

### Configuración de Android

```
Recomendaciones del sistema:
✅ Cifrado de disco completo: Activado
   Ajustes → Seguridad → Encriptar teléfono

✅ Bloqueo de pantalla: PIN/Patrón/Huella (mínimo PIN de 6 dígitos)
   Ajustes → Seguridad → Bloqueo de pantalla

✅ Bloqueo automático: 1-2 minutos
   Ajustes → Pantalla → Suspensión

✅ Verificación en 2 pasos: Activada (Google)
   Protege tu cuenta de respaldo en la nube

✅ Google Play Protect: Activado
   Escanea apps en busca de malware

❌ Depuración USB: Desactivar cuando no uses
   Opciones de desarrollador → Depuración USB (OFF)

❌ Orígenes desconocidos: Desactivar después de instalar
   Ajustes → Seguridad → Orígenes desconocidos (OFF)
```

## ⚠️ Limitaciones de Seguridad

### Lo que SecureVault PUEDE proteger:

✅ Archivos dentro de volúmenes (cifrado AES-256-XTS)
✅ Contraseñas en memoria (limpieza automática)
✅ Acceso no autorizado sin contraseña
✅ Screenshots del contenido de la app (FLAG_SECURE)
✅ Ataques de fuerza bruta (PBKDF2 con 100k iteraciones)

### Lo que SecureVault NO PUEDE proteger:

❌ **Malware con acceso root:** Si tu dispositivo está rooteado y comprometido
❌ **Keyloggers:** Si un malware captura tus pulsaciones de teclado
❌ **Shoulder surfing:** Alguien mirando tu pantalla cuando introduces la contraseña
❌ **Dispositivo desbloqueado:** Si alguien accede a tu Android desbloqueado
❌ **Backup sin cifrar:** Si haces backup del volumen sin cifrado adicional
❌ **Hardware comprometido:** Ataques a nivel de chip/firmware

### Modelo de Amenaza

SecureVault es seguro contra:
- 🟢 Robo del dispositivo bloqueado
- 🟢 Acceso físico breve sin conocer contraseña
- 🟢 Extracción de datos del almacenamiento
- 🟢 Análisis forense del volumen cerrado
- 🟢 Ataques de red (datos no transmitidos)

SecureVault NO es seguro contra:
- 🔴 Dispositivo comprometido con malware avanzado
- 🔴 Coerción para revelar contraseña (ataque de $5 wrench)
- 🔴 Análisis de volumen **mientras está abierto**
- 🔴 Ataques dirigidos por agencias estatales con recursos ilimitados

## 🎭 Mejores Prácticas de Uso

### Al Crear Volúmenes

```
1. Crea volúmenes con tamaño apropiado:
   - Documentos: 50-100 MB
   - Fotos: 500 MB - 2 GB
   - Videos: 2-5 GB
   - Backup completo: 5-10 GB (máximo permitido)

2. Usa nombres descriptivos pero no reveladores:
   ✅ Docs_Trabajo.svlt
   ✅ Backup_2024.svlt
   ❌ Passwords_Banco.svlt (¡muy obvio!)
   ❌ Secretos.svlt

3. Verifica la contraseña inmediatamente:
   - Cierra el volumen después de crear
   - Ábrelo de nuevo para confirmar que funciona
   - Guarda la contraseña en tu gestor antes de seguir
```

### Al Usar Volúmenes

```
1. Abre volúmenes solo cuando los necesites
   - No los dejes abiertos innecesariamente
   - Respeta el timeout de sesión

2. Cierra volúmenes antes de:
   - Dejar el dispositivo desatendido
   - Conectar a WiFi pública
   - Instalar apps desconocidas
   - Hacer backup del sistema

3. Extrae archivos a ubicación segura:
   - Por defecto van a: /Android/data/.../files/extracted/
   - BORRA los archivos extraídos después de usarlos
   - No los dejes en carpetas públicas como /Download/

4. Verifica el espacio disponible:
   - La barra de progreso muestra uso
   - No llenes el volumen al 100%
   - Deja al menos 10-20% libre para metadata
```

### Al Agregar Archivos

```
Tipos de archivos recomendados para cifrar:
✅ Documentos confidenciales (PDFs, DOCX, XLSX)
✅ Fotos privadas (JPG, PNG, HEIC)
✅ Videos personales (MP4, MOV)
✅ Archivos de contraseñas (KDBX de KeePass)
✅ Claves privadas (SSH, GPG)
✅ Backups de wallets de criptomonedas

Archivos que NO necesitan cifrado (ya están cifrados):
• APKs de apps (no son privadas)
• Videos de YouTube descargados (no son tuyos)
• Instaladores de programas
```

## 🔄 Mantenimiento y Actualizaciones

### Rutina Mensual

```
Checklist de seguridad mensual:
□ Cambiar contraseña maestra (opcional, cada 3-6 meses)
□ Verificar que los backups funcionan
□ Limpiar archivos extraídos que ya no necesitas
□ Actualizar Android a última versión de seguridad
□ Revisar apps instaladas (desinstala las que no uses)
□ Verificar espacio disponible en volúmenes
```

### Actualizar SecureVault

```
Si sale una nueva versión:
1. ANTES de actualizar:
   ✅ Cierra todos los volúmenes
   ✅ Haz backup de tus volúmenes .svlt
   ✅ Anota tus contraseñas (están en tu gestor, ¿verdad?)

2. Actualizar:
   - Desinstala versión anterior
   - Instala nueva versión
   - Concede permisos de nuevo

3. DESPUÉS de actualizar:
   ✅ Verifica que puedes abrir volúmenes antiguos
   ✅ Prueba crear un volumen nuevo de prueba
   ✅ Verifica que el cifrado sigue funcionando
```

## 🚨 Qué Hacer Si...

### Olvidaste tu Contraseña

```
❌ NO HAY FORMA DE RECUPERARLA
SecureVault NO tiene backdoor ni recuperación de contraseñas.
Esto es una CARACTERÍSTICA de seguridad, no un bug.

Opciones:
1. Intenta variaciones de tu contraseña (mayúsculas/minúsculas)
2. Busca en tu gestor de contraseñas
3. Revisa backups de papel
4. Si usaste un patrón, intenta recordarlo

Si definitivamente la olvidaste:
→ Los datos están PERDIDOS PARA SIEMPRE
→ No hay herramienta de "cracking" efectiva (PBKDF2 es resistente)
→ Elimina el volumen y crea uno nuevo
```

### Sospechas que tu Dispositivo está Comprometido

```
ACCIÓN INMEDIATA:
1. Cierra TODOS los volúmenes en SecureVault
2. Desinstala SecureVault
3. Copia los archivos .svlt a un dispositivo limpio
4. Restablece tu Android a valores de fábrica
5. Reinstala SecureVault desde fuente confiable
6. Considera cambiar contraseñas de los volúmenes
```

### Perdiste tu Dispositivo

```
Si tu Android se pierde o es robado:

Inmediatamente:
1. Usa "Find My Device" de Google para:
   - Ubicar el dispositivo
   - Bloquearlo remotamente
   - Borrar todos los datos (último recurso)

2. Tus volúmenes están seguros SI:
   ✅ El dispositivo está cifrado
   ✅ Tienes bloqueo de pantalla fuerte
   ✅ Los volúmenes están cerrados
   ✅ El ladrón NO conoce tu contraseña maestra

3. Si tenías backup:
   - Restaura volúmenes en nuevo dispositivo
   - Considera cambiar contraseñas por precaución
```

## 📊 Indicadores de Compromiso

### Señales de que algo puede estar mal:

```
🔴 ALERTA ROJA - Actúa inmediatamente:
• SecureVault pide permisos que no debería (contactos, cámara, etc.)
• Aparecen archivos en el volumen que no agregaste
• La app se comporta de forma extraña o crashea frecuentemente
• El dispositivo está muy lento sin razón
• Apps que no instalaste aparecen en tu Android

🟡 ALERTA AMARILLA - Investiga:
• Batería se descarga muy rápido
• Tráfico de datos elevado sin explicación
• Notificaciones raras de apps desconocidas
• Popups de "optimización del sistema"

✅ TODO NORMAL:
• SecureVault solo pide permisos de almacenamiento
• La app funciona fluida y sin crasheos
• Los volúmenes abren con tu contraseña correcta
• No hay comportamiento inesperado
```

## 📚 Recursos Adicionales

### Aprender Más Sobre Criptografía

- **AES-256:** https://en.wikipedia.org/wiki/Advanced_Encryption_Standard
- **XTS Mode:** https://en.wikipedia.org/wiki/Disk_encryption_theory#XTS
- **PBKDF2:** https://en.wikipedia.org/wiki/PBKDF2
- **VeraCrypt:** https://www.veracrypt.fr/en/Documentation.html

### Herramientas Complementarias

- **Gestores de contraseñas:** Bitwarden (gratuito, open-source)
- **Autenticación 2FA:** Google Authenticator, Authy
- **Mensajería cifrada:** Signal, Threema
- **VPN:** ProtonVPN, Mullvad (para WiFi pública)
- **Navegador privado:** Brave, Firefox con uBlock Origin

## ✅ Checklist de Seguridad Final

Antes de usar SecureVault en producción:

```
Dispositivo:
□ Android cifrado completamente
□ Bloqueo de pantalla con PIN/Patrón fuerte
□ Google Play Protect activado
□ Sin root (o Magisk Hide configurado correctamente)
□ Apps de fuentes confiables únicamente

SecureVault:
□ Instalado desde APK compilado por ti o fuente confiable
□ Permisos de almacenamiento concedidos
□ Protección de pantalla activada
□ Timeout de sesión configurado (5 min)
□ Probado con volumen de prueba primero

Contraseñas:
□ Contraseña maestra > 16 caracteres
□ Incluye mayúsculas, minúsculas, números, símbolos
□ Guardada en gestor de contraseñas
□ Backup en papel en lugar físico seguro
□ No compartida con nadie

Backup:
□ Volumen copiado a ubicación externa segura
□ Backup verificado (puede abrirse)
□ Ubicación cifrada o física segura
□ Actualizado mensualmente

Uso:
□ Cierra volúmenes cuando no los uses
□ Borra archivos extraídos después de usarlos
□ No dejes el dispositivo desbloqueado desatendido
□ Verifica apps instaladas regularmente
```

---

## 🎓 Recuerda

> **"La seguridad es un proceso, no un producto"** - Bruce Schneier

La mejor criptografía del mundo no sirve si:
- Usas contraseñas débiles
- Compartes tu dispositivo desbloqueado
- Instalas malware
- No haces backups

**SecureVault es una herramienta poderosa, pero TÚ eres el eslabón más importante en la cadena de seguridad.**

---

**¿Dudas sobre seguridad?** Revisa la documentación técnica en `docs/IMPLEMENTATION_GUIDE.md`
