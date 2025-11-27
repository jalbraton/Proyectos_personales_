# 🎯 AlarmApp - Resumen del Proyecto

## 📱 Descripción
AlarmApp es una aplicación de alarmas para Android con interfaz minimalista y funcionalidades avanzadas de reproducción de audio.

## ✨ Características Principales

### 🎵 Modos de Alarma
1. **Canción Específica**: Selecciona un archivo de audio concreto
2. **Modo Aleatorio**: Elige una carpeta y reproduce una canción diferente cada vez

### 🎨 Diseño
- Interfaz **minimalista** y **cuidada**
- Material Design 3
- Tema moderno con colores suaves
- Fácil de usar

### 📁 Formatos de Audio Soportados
- MP3
- FLAC
- WAV
- OGG
- M4A
- AAC
- WMA

## 🏗️ Arquitectura del Proyecto

```
AlarmApp/
├── 📱 MainActivity.kt              - Pantalla principal con lista de alarmas
├── ⏰ AlarmScheduler.kt            - Programa alarmas con AlarmManager
├── 📡 AlarmReceiver.kt             - Recibe eventos de alarma
├── 🎵 AlarmService.kt              - Reproduce audio en foreground
├── 🔔 AlarmRingingActivity.kt     - Pantalla cuando suena la alarma
└── 🎨 Layouts XML                  - Diseños de UI
```

## 🔧 Tecnologías Utilizadas

| Componente | Tecnología |
|------------|------------|
| Lenguaje | Kotlin |
| UI | Material Design 3 |
| Gestión de Alarmas | AlarmManager |
| Reproducción | MediaPlayer |
| Permisos | AndroidX |
| Build Tool | Gradle (Kotlin DSL) |
| CI/CD | GitHub Actions |

## 🚀 Flujo de Trabajo

### Para el Usuario:
1. Abre la app
2. Toca el botón `+`
3. Selecciona modo (archivo específico o carpeta aleatoria)
4. Elige el audio/carpeta
5. Configura la hora
6. ¡Listo!

### Técnicamente:
```
Usuario crea alarma
    ↓
MainActivity guarda datos
    ↓
AlarmScheduler programa con AlarmManager
    ↓
Sistema Android dispara alarma a la hora
    ↓
AlarmReceiver recibe evento
    ↓
AlarmService reproduce audio
    ↓
AlarmRingingActivity muestra pantalla
    ↓
Usuario detiene o pospone
```

## 📦 Estructura de Archivos

### Código Principal (5 archivos Kotlin)
- `MainActivity.kt` - 320 líneas - Lógica principal y UI
- `AlarmScheduler.kt` - 60 líneas - Programación de alarmas
- `AlarmReceiver.kt` - 30 líneas - Receptor de broadcasts
- `AlarmService.kt` - 180 líneas - Servicio de reproducción
- `AlarmRingingActivity.kt` - 40 líneas - Pantalla de alarma sonando

### Layouts (4 archivos XML)
- `activity_main.xml` - Pantalla principal
- `item_alarm.xml` - Elemento de lista de alarma
- `dialog_add_alarm.xml` - Diálogo para agregar alarma
- `activity_alarm_ringing.xml` - Pantalla de alarma activa

### Configuración
- `AndroidManifest.xml` - Permisos y componentes
- `build.gradle.kts` - Dependencias y configuración de build
- `strings.xml`, `colors.xml`, `themes.xml` - Recursos

## 🔐 Permisos Requeridos

```xml
✅ SCHEDULE_EXACT_ALARM      - Para alarmas exactas
✅ USE_EXACT_ALARM           - API 33+
✅ POST_NOTIFICATIONS        - Mostrar notificaciones
✅ WAKE_LOCK                 - Despertar dispositivo
✅ VIBRATE                   - Vibración
✅ READ_MEDIA_AUDIO          - Leer archivos de audio (API 33+)
✅ READ_EXTERNAL_STORAGE     - Leer archivos (API < 33)
✅ FOREGROUND_SERVICE        - Servicio en primer plano
```

## 🎯 Build Automático con GitHub Actions

El workflow `.github/workflows/android.yml` genera automáticamente:

1. **APK Debug** - Para desarrollo y testing
2. **APK Release** - Para distribución (sin firmar)
3. **Release en GitHub** - Con versionado automático

### Triggers:
- ✅ Push a `main` o `master`
- ✅ Pull Request
- ✅ Manual dispatch

## 📊 Estadísticas del Proyecto

| Métrica | Valor |
|---------|-------|
| Archivos Kotlin | 5 |
| Archivos XML | 11 |
| Líneas de código Kotlin | ~630 |
| Líneas de XML | ~400 |
| Dependencias | 7 principales |
| API mínima | Android 7.0 (API 24) |
| API objetivo | Android 14 (API 34) |

## 🔄 Ciclo de Vida de una Alarma

```
CREACIÓN
    ↓
PROGRAMADA (AlarmManager)
    ↓
ESPERANDO...
    ↓
DISPARADA (hora exacta)
    ↓
SONANDO (AlarmService)
    ↓
DETENIDA (usuario)
    ↓
REPROGRAMADA (para mañana)
```

## 🎨 Paleta de Colores

- **Primary**: `#6200EE` (Morado vibrante)
- **Primary Dark**: `#3700B3` (Morado oscuro)
- **Accent**: `#03DAC5` (Turquesa)
- **Background**: `#F5F5F5` (Gris claro)

## 📱 Compatibilidad

| Android Version | API Level | Soportado |
|----------------|-----------|-----------|
| Android 14 | 34 | ✅ Totalmente |
| Android 13 | 33 | ✅ Totalmente |
| Android 12 | 31-32 | ✅ Totalmente |
| Android 11 | 30 | ✅ Totalmente |
| Android 10 | 29 | ✅ Totalmente |
| Android 9 | 28 | ✅ Totalmente |
| Android 8 | 26-27 | ✅ Totalmente |
| Android 7 | 24-25 | ✅ Totalmente |
| Android 6 y anteriores | < 24 | ❌ No soportado |

## 🚀 Próximas Funcionalidades

- [ ] Función snooze (posponer 5 minutos)
- [ ] Etiquetas personalizadas
- [ ] Repetición semanal (L, M, Mi, J, V, S, D)
- [ ] Vibración personalizable
- [ ] Volumen gradual (fade in)
- [ ] Temas claro/oscuro
- [ ] Widget de pantalla principal
- [ ] Backup/Restauración de alarmas
- [ ] Estadísticas de uso

## 📝 Notas del Desarrollador

### Decisiones de Diseño:
1. **AlarmManager** en lugar de WorkManager para precisión exacta
2. **Foreground Service** para reproducción confiable
3. **Material Design 3** para UI moderna
4. **SharedPreferences** para almacenamiento simple (futuro: Room DB)
5. **MediaPlayer** nativo (alternativa: ExoPlayer para más formatos)

### Limitaciones Conocidas:
- En modo Doze extremo, algunas alarmas pueden retrasarse
- Archivos muy grandes pueden tardar en cargar
- Sin soporte para streaming online (solo archivos locales)

## 🤝 Contribuciones

Las contribuciones son bienvenidas en:
- https://github.com/jalbraton/alarmapp

## 📄 Licencia

MIT License - Libre uso para fines personales y comerciales

---

**Versión**: 1.0.0  
**Fecha**: Noviembre 2025  
**Autor**: Jose Albraton  
**GitHub**: [@jalbraton](https://github.com/jalbraton)
