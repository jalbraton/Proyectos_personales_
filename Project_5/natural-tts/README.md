# 🎙️ Natural Text-to-Speech Generator

![Python](https://img.shields.io/badge/Python-3.8+-blue.svg)
![License](https://img.shields.io/badge/License-MIT-green.svg)

Convierte cualquier texto a voz con **calidad ultrarrealista** que es prácticamente indistinguible de una voz humana real. Detecta automáticamente el idioma y genera audio en alta calidad.

## ⚡ **SIN LÍMITES DE CARACTERES** - Textos ilimitados

## ✨ Características Principales

- 🌍 **Detección automática de idioma** - Soporta más de 50 idiomas
- 🎯 **Voces neurales ultrarrealistas** - Casi indistinguibles de humanos
- ♾️ **Sin límites de caracteres** - Procesa textos de cualquier longitud
- 🔧 **Múltiples motores TTS**:
  - **gTTS** (Gratis, Sin Límites) - Google TTS con procesamiento en chunks
  - **pyttsx3** (Offline, Sin Límites) - Funciona sin internet
  - **ElevenLabs** (Premium) - La mejor calidad (tiene límites en plan gratuito)
- 💻 **Interfaz CLI y Web** - Úsalo desde terminal o navegador
- 📦 **Sin dependencias complejas** - Instalación simple
- 🎵 **Salida en MP3** - Compatible con cualquier dispositivo

## 🚀 Inicio Rápido

### Instalación

```bash
# Clonar o descargar el proyecto
cd natural-tts

# Instalar dependencias
pip install -r requirements.txt
```

### Uso Básico (CLI)

```bash
# Ejemplo simple - auto-detecta el idioma
python tts_generator.py "Hola, este es un ejemplo de voz natural en español"

# Especificar archivo de salida
python tts_generator.py "Hello, this is natural speech" -o output.mp3

# Desde un archivo de texto
python tts_generator.py -f texto.txt -o audio.mp3

# Especificar idioma manualmente
python tts_generator.py "Bonjour le monde" -l fr -o french.mp3
```

### Interfaz Web

```bash
# Iniciar servidor web
python web_interface.py

# Abrir en el navegador
# http://localhost:5000
```

## 🎯 Ejemplos de Uso

### Ejemplo 1: Texto Simple

```python
from tts_generator import NaturalTTS

# Crear generador
tts = NaturalTTS(engine='auto')

# Generar audio (detecta automáticamente que es español)
tts.generate(
    "Esta es una voz muy natural que suena como una persona real",
    "output.mp3"
)
```

### Ejemplo 2: Con ElevenLabs (Máxima Calidad)

```python
from tts_generator import NaturalTTS

# Usar ElevenLabs (requiere API key)
tts = NaturalTTS(
    engine='elevenlabs',
    api_key='tu_api_key_aqui'  # O usa variable ELEVENLABS_API_KEY
)

# Genera audio con la mejor calidad posible
tts.generate(
    "This voice is incredibly natural and realistic",
    "premium_output.mp3"
)
```

### Ejemplo 3: Múltiples Idiomas

```python
from tts_generator import NaturalTTS

tts = NaturalTTS()

# Español
tts.generate("Hola mundo", "spanish.mp3")

# Inglés
tts.generate("Hello world", "english.mp3")

# Francés
tts.generate("Bonjour le monde", "french.mp3")

# Alemán
tts.generate("Hallo Welt", "german.mp3")
```

### Ejemplo 4: Leer Artículo Completo

```python
from tts_generator import NaturalTTS

tts = NaturalTTS(engine='edge')  # Gratis y buena calidad

# Leer archivo de texto largo
with open('articulo.txt', 'r', encoding='utf-8') as f:
    texto = f.read()

# Generar audio del artículo completo
tts.generate(texto, "articulo_audio.mp3")
```

## 🔧 Motores TTS Disponibles

### 1. gTTS (Recomendado - Sin Límites)

**Ventajas:**
- ✅ Completamente gratis y sin límites
- ✅ Usa Google Text-to-Speech
- ✅ Soporte multiidioma excelente (50+ idiomas)
- ✅ Procesa textos de cualquier longitud (divide en chunks automáticamente)
- ✅ Buena calidad de voz natural

**Uso:**
```bash
python tts_generator.py "texto de cualquier longitud" --engine gtts
```

### 2. pyttsx3 (Alternativa Offline - Sin Límites)

**Ventajas:**
- ✅ Funciona completamente offline
- ✅ Sin límites de caracteres
- ✅ No requiere conexión a internet
- ✅ Muy rápido

**Limitaciones:**
- ⚠️ Calidad más robótica que gTTS o ElevenLabs

**Uso:**
```bash
python tts_generator.py "texto" --engine pyttsx3
```

### 3. ElevenLabs (Premium - Tiene Límites)

**Ventajas:**
- ✅ Voces increíblemente realistas
- ✅ Entonación y emociones naturales
- ✅ Soporte multiidioma excelente

**Limitaciones:**
- ⚠️ Límite de 10,000 caracteres/mes en plan gratuito
- ⚠️ Requiere API Key

**Requisitos:**
- API Key de ElevenLabs (cuenta gratuita: 10,000 caracteres/mes)
- Obtén tu key en: https://elevenlabs.io

```bash
# Configurar API key
export ELEVENLABS_API_KEY='tu_api_key_aqui'

# O pasarla directamente
python tts_generator.py "text" --api-key tu_api_key
```

## 📖 Opciones de CLI

```
usage: tts_generator.py [-h] [-f FILE] [-o OUTPUT] [-l LANG] 
                        [-e {auto,elevenlabs,edge,gtts}] [--api-key API_KEY] 
                        [text]

Natural Text-to-Speech with automatic language detection

positional arguments:
  text                  Text to convert to speech (or use --file)

optional arguments:
  -h, --help            show this help message and exit
  -f FILE, --file FILE  Input text file
  -o OUTPUT, --output OUTPUT
                        Output audio file (default: output.mp3)
  -l LANG, --lang LANG  Language code (e.g., en, es, fr). Auto-detected if not specified
  -e {auto,elevenlabs,edge,gtts}, --engine {auto,elevenlabs,edge,gtts}
                        TTS engine to use (default: auto)
  --api-key API_KEY     ElevenLabs API key
```

## 🌍 Idiomas Soportados

El sistema detecta automáticamente y soporta:

- 🇪🇸 Español (España y Latinoamérica)
- 🇬🇧 English (US, UK, Australia)
- 🇫🇷 Français
- 🇩🇪 Deutsch
- 🇮🇹 Italiano
- 🇵🇹 Português (Brasil y Portugal)
- 🇯🇵 日本語
- 🇨🇳 中文
- 🇰🇷 한국어
- 🇷🇺 Русский
- Y muchos más...

## 💡 Casos de Uso

### 📚 Lectura de Libros y Artículos
Convierte documentos largos a audio para escuchar mientras haces otras actividades.

### 🎓 Material Educativo
Crea audiolibros y material de estudio en múltiples idiomas.

### 🎬 Narración de Videos
Genera voces naturales para narración de videos y presentaciones.

### ♿ Accesibilidad
Ayuda a personas con dificultades de lectura o discapacidad visual.

### 🌐 Contenido Multiidioma
Crea contenido en múltiples idiomas automáticamente.

## 🎨 Interfaz Web

La interfaz web incluye:

- ✅ Editor de texto con contador de caracteres
- ✅ Selector de idioma (o auto-detección)
- ✅ Generación en tiempo real
- ✅ Descarga directa del audio
- ✅ Diseño moderno y responsive

<div align="center">
  <img src="docs/screenshot.png" alt="Interfaz Web" width="600px">
</div>

## 🔐 Configuración Avanzada

### Variables de Entorno

```bash
# API Key de ElevenLabs
export ELEVENLABS_API_KEY='tu_api_key'

# Motor por defecto
export TTS_ENGINE='edge'
```

### Personalizar Voces

Edita `tts_generator.py` para cambiar las voces:

```python
# Para Edge-TTS - cambiar voces por idioma
EDGE_VOICES = {
    'es': 'es-ES-AlvaroNeural',    # Cambiar a voz femenina: es-ES-ElviraNeural
    'en': 'en-US-GuyNeural',        # Cambiar a: en-US-JennyNeural
    # ...
}
```

Lista completa de voces Edge-TTS:
```bash
edge-tts --list-voices
```

## 📊 Comparación de Calidad

| Motor | Calidad | Velocidad | Costo | Límites | Idiomas |
|-------|---------|-----------|-------|---------|---------|
| gTTS | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | Gratis | ♾️ Sin límites | 50+ |
| pyttsx3 | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | Gratis | ♾️ Sin límites | 50+ |
| ElevenLabs | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | 💰💰 | 10k chars/mes | 29 |

## 🛠️ Solución de Problemas

### Error: "No TTS engine available"

```bash
# Instalar gTTS (recomendado - sin límites)
pip install gtts

# O instalar pyttsx3 (offline - sin límites)
pip install pyttsx3
```

### Error 403 con servicios en la nube

Usa gTTS que es más estable y sin límites:
```bash
python tts_generator.py "texto" --engine gtts
```

### Error de detección de idioma

```bash
# Especificar idioma manualmente
python tts_generator.py "texto" -l es
```

### Textos muy largos

```bash
# gTTS divide automáticamente en chunks
python tts_generator.py -f libro_completo.txt --engine gtts

# Sin problemas con textos de 100,000+ caracteres
# Nota: Si tienes pydub instalado, concatenará los chunks automáticamente
pip install pydub
```

## 📁 Estructura del Proyecto

```
natural-tts/
├── tts_generator.py      # Motor principal de TTS
├── web_interface.py      # Servidor web Flask
├── requirements.txt      # Dependencias
├── templates/
│   └── index.html       # Interfaz web
├── generated_audio/     # Audios generados (creado automáticamente)
└── README.md
```

## 🤝 Contribuir

¡Las contribuciones son bienvenidas! Algunas ideas:

- [ ] Agregar más voces y configuraciones
- [ ] Soporte para SSML (Speech Synthesis Markup Language)
- [ ] Cache de audios generados
- [ ] API REST completa
- [ ] Soporte para streaming de audio
- [ ] Interfaz de escritorio (GUI)

## 📄 Licencia

MIT License - Uso libre para proyectos personales y comerciales.

## 🙏 Agradecimientos

- [gTTS](https://github.com/pndurette/gTTS) - Google Text-to-Speech (sin límites con chunks)
- [ElevenLabs](https://elevenlabs.io) - API de voces neurales premium
- [pyttsx3](https://github.com/nateshmbhat/pyttsx3) - TTS offline multiplataforma
- [langdetect](https://github.com/Mimino666/langdetect) - Detección de idioma
- [pydub](https://github.com/jiaaro/pydub) - Concatenación de audio

---

<div align="center">
  <strong>🎙️ Hecho con ❤️ para crear voces naturales</strong>
</div>
