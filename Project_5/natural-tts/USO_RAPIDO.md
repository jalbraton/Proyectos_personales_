# 🎯 GUÍA DE USO RÁPIDO - Natural TTS

## ✅ Sistema Instalado y Funcionando

### 📌 Estado Actual:
- ✅ Dependencias instaladas
- ✅ gTTS (Google TTS) - Sin límites
- ✅ pyttsx3 (Offline) - Sin límites  
- ✅ Sistema probado y funcionando

---

## 🚀 OPCIÓN 1: Comando Simple (RECOMENDADO)

La forma más fácil y confiable:

```bash
cd "C:\Users\JoseA\OneDrive\Documentos\Workspace_1\Project_5\natural-tts"
python tts_generator.py "Tu texto aquí" -o audio.mp3
```

### Ejemplos:

```bash
# Texto corto en español (auto-detectado)
python tts_generator.py "Hola mundo, este es un test" -o test.mp3

# Texto en inglés
python tts_generator.py "Hello world, this is a test" -o test_en.mp3

# Desde archivo de texto (sin límites)
python tts_generator.py -f mi_documento.txt -o documento_audio.mp3

# Especificar idioma manualmente
python tts_generator.py "Bonjour le monde" -l fr -o frances.mp3
```

---

## 🚀 OPCIÓN 2: Script Python Simple

Más fácil para textos largos que editas frecuentemente.

### Paso 1: Edita `generar_simple.py`

Abre el archivo y cambia el texto:

```python
texto = """
Aquí pega todo tu texto.
Puede ser tan largo como quieras.
Sin límites!
"""
```

### Paso 2: Ejecuta

```bash
python generar_simple.py
```

¡Listo! El audio se guarda en `mi_audio.mp3`

---

## 🚀 OPCIÓN 3: Interfaz Web

Si prefieres usar el navegador:

### Iniciar servidor:

```bash
python web_interface.py
```

### Abrir navegador:

http://127.0.0.1:5000

**Nota:** Si tienes error "Failed to fetch", usa la OPCIÓN 1 o 2.

---

## 📝 Comandos Útiles

### Ver todas las opciones:
```bash
python tts_generator.py --help
```

### Cambiar motor TTS:
```bash
# Usar offline (sin internet)
python tts_generator.py "texto" --engine pyttsx3 -o audio.mp3

# Usar Google TTS (por defecto)
python tts_generator.py "texto" --engine gtts -o audio.mp3
```

### Procesar múltiples archivos:

```powershell
# Procesar todos los .txt en un directorio
Get-ChildItem *.txt | ForEach-Object {
    python tts_generator.py -f $_.Name -o "$($_.BaseName).mp3"
}
```

---

## 💡 Tips Importantes

### ✅ SIN LÍMITES:
- Puedes procesar textos de 100,000+ caracteres
- El sistema divide automáticamente en chunks
- Se concatenan al final para un solo archivo

### 🌍 IDIOMAS:
- Auto-detección funciona bien
- Soporta 50+ idiomas
- Si falla, especifica manualmente con `-l es` (español), `-l en` (inglés), etc.

### 📁 ARCHIVOS:
- Salida: Siempre MP3
- Entrada: Texto plano (.txt recomendado)
- Codificación: UTF-8

---

## 🔧 Solución de Problemas

### Error: "No TTS engine available"
```bash
pip install gtts pyttsx3
```

### Error: "Failed to fetch" (interfaz web)
**Solución:** Usa la línea de comandos (OPCIÓN 1)

### Texto muy largo tarda mucho
**Normal:** Textos de 50,000+ caracteres pueden tardar varios minutos.
Se ve el progreso: "Chunk 1/10...", etc.

### Calidad de voz baja
gTTS tiene buena calidad, pero si quieres mejor:
- Obtén API key gratis de ElevenLabs: https://elevenlabs.io
- Configura: `set ELEVENLABS_API_KEY=tu_key`
- Usa: `--engine elevenlabs`

---

## 📋 Resumen de Archivos

- `tts_generator.py` - Motor principal (CLI)
- `generar_simple.py` - Script editable simple
- `web_interface.py` - Interfaz web
- `examples.py` - Ejemplos de código
- `test_unlimited.py` - Test de límites
- `TROUBLESHOOTING.md` - Solución de problemas detallada

---

## ⚡ Quick Start de 30 segundos

```bash
# 1. Ir al directorio
cd "C:\Users\JoseA\OneDrive\Documentos\Workspace_1\Project_5\natural-tts"

# 2. Generar audio
python tts_generator.py "Hola, esto funciona perfecto!" -o test.mp3

# 3. Reproducir test.mp3
```

¡Listo! 🎉

---

**Recomendación:** Para uso diario, usa la **OPCIÓN 1** (línea de comandos).
Es la más rápida, confiable y sin problemas de conexión.
