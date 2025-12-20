"""
Quick test script to demonstrate NO CHARACTER LIMITS
"""

from tts_generator import NaturalTTS

# Test with short text
print("=" * 60)
print("TEST 1: Short Text")
print("=" * 60)

tts = NaturalTTS(engine='gtts')
tts.generate(
    "Hola, este es un sistema de text-to-speech sin límites de caracteres.",
    "test_short.mp3"
)

print("\n")

# Test with long text (10,000+ characters)
print("=" * 60)
print("TEST 2: Long Text (10,000+ characters)")
print("=" * 60)

long_text = """
La inteligencia artificial está revolucionando el mundo de la tecnología. 
Los sistemas de text-to-speech modernos pueden generar voces increíblemente naturales.
Este sistema en particular no tiene límites de caracteres, lo que significa que puede 
procesar textos muy largos sin problemas. 

El procesamiento de lenguaje natural ha avanzado enormemente en los últimos años.
Ahora es posible crear voces sintéticas que suenan casi indistinguibles de voces humanas reales.
Esto tiene aplicaciones en múltiples campos como la educación, accesibilidad, entretenimiento,
y mucho más.

Los algoritmos de aprendizaje profundo han permitido estos avances increíbles.
Las redes neuronales pueden aprender patrones complejos en el habla humana y reproducirlos
con una fidelidad sorprendente. El futuro de la síntesis de voz es muy prometedor.

""" * 100  # Repeat 100 times to create a very long text

print(f"Text length: {len(long_text)} characters")

tts.generate(long_text, "test_long.mp3")

print("\n" + "=" * 60)
print("✅ Tests completed!")
print("=" * 60)
print("\nGenerated files:")
print("  - test_short.mp3")
print("  - test_long.mp3")
print("\n🎧 Play these files to verify the audio quality!")
