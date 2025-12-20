# 🎙️ Natural TTS - Demostración

## ✅ Sistema Instalado y Funcionando

### Problema Resuelto:
- ❌ Edge-TTS tenía error 403 (bloqueado por Microsoft)
- ✅ Ahora usa **gTTS** (Google TTS) - Sin límites y estable

### Características:
- ♾️ **SIN LÍMITES DE CARACTERES** - Textos ilimitados
- 🌍 Detección automática de idioma
- 🎯 Buena calidad de voz natural
- 📥 Descarga en formato MP3

## 🚀 Uso Rápido

### 1. Texto Simple (CLI):
```bash
python tts_generator.py "Tu texto aquí" -o audio.mp3
```

### 2. Archivo de Texto Largo:
```bash
python tts_generator.py -f archivo.txt -o audio_largo.mp3
```

### 3. Interfaz Web:
```bash
python web_interface.py
# Abrir: http://localhost:5000
```

## 📝 Ejemplo de Demo

Ya generado: `demo.mp3`
- Texto en español detectado automáticamente
- 134 caracteres procesados
- Audio listo para descargar

## 🔧 Motores Disponibles:

1. **gTTS** (Por defecto - Recomendado)
   - ✅ Sin límites de caracteres
   - ✅ Buena calidad
   - ✅ 50+ idiomas
   - ✅ Completamente gratis

2. **pyttsx3** (Alternativa Offline)
   - ✅ Funciona sin internet
   - ✅ Sin límites
   - ⚠️ Calidad más robótica

3. **ElevenLabs** (Premium)
   - ✅ Máxima calidad
   - ⚠️ Requiere API key
   - ⚠️ 10k caracteres/mes gratis

## 💡 Textos Largos:

El sistema divide automáticamente textos largos en chunks y los concatena:

```bash
# Procesa libro completo sin problemas
python tts_generator.py -f libro_50000_palabras.txt -o audiolibro.mp3
```

## 🌍 Idiomas Soportados:

Auto-detecta y funciona con:
- 🇪🇸 Español
- 🇬🇧 English
- 🇫🇷 Français
- 🇩🇪 Deutsch
- 🇮🇹 Italiano
- 🇵🇹 Português
- 🇯🇵 日本語
- 🇨🇳 中文
- Y 40+ más...

## ✨ Ventajas vs Otros Sistemas:

| Característica | Este Sistema | Otros |
|---------------|--------------|-------|
| Límite de caracteres | ♾️ Sin límites | ⚠️ 5k-10k |
| Requiere API Key | ❌ No (con gTTS) | ⚠️ Sí |
| Costo | ✅ Gratis | 💰 Pago |
| Instalación | ✅ Simple | ⚠️ Compleja |
| Idiomas | ✅ 50+ | ⚠️ Limitado |

## 🎯 Casos de Uso:

1. **Audiolibros**: Convierte novelas completas
2. **Estudios**: Material educativo en audio
3. **Accesibilidad**: Para personas con problemas de visión
4. **Contenido Multiidioma**: Automático
5. **Narración de Videos**: Voces naturales

---

**¡Listo para usar!** 🎉

El error 403 está solucionado. Ahora puedes procesar textos de cualquier longitud.
