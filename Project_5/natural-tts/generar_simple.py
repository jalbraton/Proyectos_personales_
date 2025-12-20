"""
Simple script to generate TTS without web interface
Edit the 'texto' variable with your text and run this script
"""

from tts_generator import NaturalTTS

# ============================================
# CONFIGURA TU TEXTO AQUÍ
# ============================================

texto = """
Escribe o pega aquí el texto que quieres convertir a voz.
Puedes poner textos tan largos como quieras.
No hay límites de caracteres.

El sistema detectará automáticamente el idioma.
"""

# Archivo de salida
archivo_salida = "mi_audio.mp3"

# Idioma (opcional, déjalo None para auto-detección)
idioma = None  # Ejemplos: 'es', 'en', 'fr', 'de', etc.

# ============================================
# NO NECESITAS CAMBIAR NADA MÁS ABAJO
# ============================================

if __name__ == "__main__":
    print("=" * 60)
    print("  Natural TTS Generator - Simple Mode")
    print("=" * 60)
    print()
    
    # Verificar que hay texto
    if not texto.strip():
        print("❌ Error: No hay texto configurado")
        print("   Edita este archivo y agrega tu texto en la variable 'texto'")
        exit(1)
    
    print(f"📝 Texto a procesar: {len(texto)} caracteres")
    print(f"📁 Archivo de salida: {archivo_salida}")
    print()
    
    try:
        # Crear generador TTS
        print("⚙️  Inicializando TTS...")
        tts = NaturalTTS(engine='gtts')
        
        # Generar audio
        print("🎙️  Generando audio...")
        tts.generate(texto, archivo_salida, lang=idioma)
        
        print()
        print("=" * 60)
        print("✅ ¡LISTO!")
        print("=" * 60)
        print(f"📥 Audio guardado en: {archivo_salida}")
        print("🎧 Puedes reproducirlo con cualquier reproductor de audio")
        print()
        
    except Exception as e:
        print()
        print("=" * 60)
        print("❌ ERROR")
        print("=" * 60)
        print(f"   {e}")
        print()
        import traceback
        traceback.print_exc()
