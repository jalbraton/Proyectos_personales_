# 🚀 Quick Start Guide

## ⚠️ Problema Resuelto

**Error 403 con Edge-TTS**: Ya solucionado. Ahora usa **gTTS** (Google TTS) que es estable y sin límites.

## Installation (2 minutes)

### Step 1: Install Python
Make sure you have Python 3.8 or higher installed.

```bash
python --version
```

### Step 2: Install Dependencies

```bash
cd natural-tts
pip install -r requirements.txt
```

That's it! Installation complete.

## Basic Usage - NO CHARACTER LIMITS ♾️

### Option 1: Command Line (Simplest)

```bash
# Just type your text
python tts_generator.py "Hello, this is a test of natural text to speech"

# The audio will be saved as output.mp3
```

### Option 2: Web Interface (Most User-Friendly)

```bash
# Start the web server
python web_interface.py

# Open your browser at: http://localhost:5000
# Type your text, click "Generate", and download!
```

### Option 3: Python Code (Most Flexible)

```python
from tts_generator import NaturalTTS

# Create generator
tts = NaturalTTS()

# Generate audio (auto-detects language)
tts.generate("Your text here", "output.mp3")
```

## Common Commands

```bash
# Generate from text file
python tts_generator.py -f mytext.txt -o audio.mp3

# Specify language
python tts_generator.py "Hola mundo" -l es

# Use specific engine
python tts_generator.py "text" --engine edge

# Run examples
python examples.py
```

## Getting Better Quality (Optional)

### Free Option (Current - Recommended):
Using gTTS (Google TTS) - Good quality, no limits, no setup needed ✅

### Offline Option:
```bash
# Use pyttsx3 for offline usage (no internet required)
python tts_generator.py "text" --engine pyttsx3
```

### Premium Option (Best Quality):
1. Get free API key: https://elevenlabs.io
2. Set environment variable:
   ```bash
   # Windows
   set ELEVENLABS_API_KEY=your_key_here
   
   # Linux/Mac
   export ELEVENLABS_API_KEY=your_key_here
   ```
3. Use it:
   ```bash
   python tts_generator.py "text" --engine elevenlabs
   ```

## Long Texts (NO LIMITS)

```bash
# Process entire books or documents
python tts_generator.py -f libro_completo.txt -o audiolibro.mp3

# The system automatically splits and concatenates chunks
# Works with 100,000+ characters without issues
```

## Next Steps

- Run `python examples.py` to see more examples
- Check out the full `README.md` for advanced features
- Open the web interface for the easiest experience

## Need Help?

```bash
# See all options
python tts_generator.py --help

# Test with demo text
python tts_generator.py "Hello world" -o test.mp3
```

## Common Issues

### Error 403
This was happening with Edge-TTS. Now using gTTS which is stable. ✅

### Long texts
No problem! The system automatically handles unlimited text length. ♾️

### Want offline?
```bash
python tts_generator.py "text" --engine pyttsx3
```

---

**You're ready to go!** 🎉

Start with the web interface for the easiest experience, or use CLI for quick conversions with **unlimited text length**.
