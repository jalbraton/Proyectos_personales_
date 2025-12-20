"""
Example script demonstrating Natural TTS usage
"""

from tts_generator import NaturalTTS
import os

def example_basic():
    """Basic usage example"""
    print("=" * 60)
    print("Example 1: Basic Usage")
    print("=" * 60)
    
    tts = NaturalTTS(engine='auto')
    
    # Spanish text (auto-detected)
    tts.generate(
        "Hola, esta es una demostración de voz natural en español. "
        "La calidad es muy alta y suena casi humana.",
        "example_spanish.mp3"
    )
    
    print("\n✅ Audio saved: example_spanish.mp3\n")


def example_multiple_languages():
    """Generate audio in multiple languages"""
    print("=" * 60)
    print("Example 2: Multiple Languages")
    print("=" * 60)
    
    tts = NaturalTTS(engine='edge')  # Using Edge-TTS (free)
    
    texts = {
        'spanish': "Esta es una prueba en español con voz muy natural.",
        'english': "This is a test in English with very natural voice.",
        'french': "Ceci est un test en français avec une voix très naturelle.",
        'german': "Dies ist ein Test auf Deutsch mit sehr natürlicher Stimme.",
        'italian': "Questo è un test in italiano con voce molto naturale."
    }
    
    for lang, text in texts.items():
        output = f"example_{lang}.mp3"
        tts.generate(text, output)
        print(f"✅ Generated: {output}")
    
    print()


def example_from_file():
    """Read text from file"""
    print("=" * 60)
    print("Example 3: From Text File")
    print("=" * 60)
    
    # Create sample text file
    sample_text = """
    La inteligencia artificial está transformando el mundo.
    Los sistemas de text-to-speech ahora pueden generar voces
    prácticamente indistinguibles de humanos reales.
    Esta tecnología tiene aplicaciones en educación, accesibilidad,
    entretenimiento y mucho más.
    """
    
    with open('sample_text.txt', 'w', encoding='utf-8') as f:
        f.write(sample_text)
    
    # Generate from file
    tts = NaturalTTS()
    
    with open('sample_text.txt', 'r', encoding='utf-8') as f:
        text = f.read()
    
    tts.generate(text, "example_from_file.mp3")
    
    print("✅ Audio generated from text file: example_from_file.mp3\n")
    
    # Cleanup
    os.remove('sample_text.txt')


def example_elevenlabs():
    """Example using ElevenLabs (requires API key)"""
    print("=" * 60)
    print("Example 4: ElevenLabs (Premium Quality)")
    print("=" * 60)
    
    api_key = os.getenv('ELEVENLABS_API_KEY')
    
    if not api_key:
        print("⚠️  Skipping: Set ELEVENLABS_API_KEY environment variable")
        print("   Get your free API key at: https://elevenlabs.io")
        print()
        return
    
    try:
        tts = NaturalTTS(engine='elevenlabs', api_key=api_key)
        
        tts.generate(
            "This is premium quality text-to-speech using ElevenLabs. "
            "The voice quality is absolutely incredible and sounds completely human.",
            "example_elevenlabs.mp3"
        )
        
        print("✅ Premium audio generated: example_elevenlabs.mp3\n")
    
    except Exception as e:
        print(f"❌ Error: {e}\n")


def example_long_text():
    """Generate audio from longer text"""
    print("=" * 60)
    print("Example 5: Long Text")
    print("=" * 60)
    
    long_text = """
    El aprendizaje automático es una rama de la inteligencia artificial
    que permite a los sistemas aprender y mejorar automáticamente a partir
    de la experiencia sin ser programados explícitamente.
    
    Los algoritmos de aprendizaje automático construyen modelos basados en
    datos de muestra, conocidos como datos de entrenamiento, para hacer
    predicciones o tomar decisiones sin estar programados explícitamente
    para realizar esa tarea.
    
    Esta tecnología está presente en numerosas aplicaciones cotidianas,
    desde los sistemas de recomendación en plataformas de streaming,
    hasta los asistentes virtuales en nuestros teléfonos inteligentes.
    """
    
    tts = NaturalTTS()
    tts.generate(long_text.strip(), "example_long.mp3")
    
    print("✅ Long text audio generated: example_long.mp3\n")


def main():
    """Run all examples"""
    print("\n")
    print("🎙️" * 30)
    print(" " * 20 + "NATURAL TTS - EXAMPLES")
    print("🎙️" * 30)
    print("\n")
    
    try:
        example_basic()
        example_multiple_languages()
        example_from_file()
        example_elevenlabs()
        example_long_text()
        
        print("=" * 60)
        print("✨ All examples completed!")
        print("=" * 60)
        print("\nGenerated files:")
        print("  - example_spanish.mp3")
        print("  - example_english.mp3")
        print("  - example_french.mp3")
        print("  - example_german.mp3")
        print("  - example_italian.mp3")
        print("  - example_from_file.mp3")
        print("  - example_long.mp3")
        if os.path.exists("example_elevenlabs.mp3"):
            print("  - example_elevenlabs.mp3")
        print("\n🎧 Play these files to hear the natural voices!\n")
    
    except Exception as e:
        print(f"\n❌ Error running examples: {e}\n")
        import traceback
        traceback.print_exc()


if __name__ == '__main__':
    main()
