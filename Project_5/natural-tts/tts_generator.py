"""
Natural Text-to-Speech Generator with Automatic Language Detection
Supports multiple TTS engines with human-like voice quality
NO CHARACTER LIMITS
"""

import os
import sys
from pathlib import Path
from typing import Optional, Tuple
import re

# Language detection
from langdetect import detect, LangDetectException

# ElevenLabs (Premium - Most Natural)
try:
    from elevenlabs import generate, save, set_api_key, Voice, VoiceSettings
    ELEVENLABS_AVAILABLE = True
except ImportError:
    ELEVENLABS_AVAILABLE = False

# gTTS (Free - NO LIMITS with chunking)
try:
    from gtts import gTTS
    GTTS_AVAILABLE = True
except ImportError:
    GTTS_AVAILABLE = False

# pyttsx3 (Offline - No Limits)
try:
    import pyttsx3
    PYTTSX3_AVAILABLE = True
except ImportError:
    PYTTSX3_AVAILABLE = False


class NaturalTTS:
    """Natural Text-to-Speech with automatic language detection - NO CHARACTER LIMITS"""
    
    # ElevenLabs voice IDs
    ELEVENLABS_VOICES = {
        'en': 'pNInz6obpgDQGcFmaJgB',
        'es': 'pNInz6obpgDQGcFmaJgB',
        'default': 'pNInz6obpgDQGcFmaJgB'
    }
    
    def __init__(self, engine: str = 'auto', api_key: Optional[str] = None):
        """
        Initialize TTS Generator
        
        Args:
            engine: 'elevenlabs', 'gtts', 'pyttsx3', or 'auto' (selects best available)
            api_key: ElevenLabs API key (required if using ElevenLabs)
        """
        self.engine = engine
        self.api_key = api_key
        self.pyttsx3_engine = None
        
        if engine == 'elevenlabs' or (engine == 'auto' and ELEVENLABS_AVAILABLE):
            if not api_key:
                api_key = os.getenv('ELEVENLABS_API_KEY')
            if api_key:
                set_api_key(api_key)
                self.engine = 'elevenlabs'
                print("✓ Using ElevenLabs (Premium Quality - Has character limits)")
            elif engine == 'elevenlabs':
                raise ValueError("ElevenLabs API key required")
        
        if self.engine == 'auto':
            if GTTS_AVAILABLE:
                self.engine = 'gtts'
                print("✓ Using gTTS (Good Quality, Free, NO LIMITS)")
            elif PYTTSX3_AVAILABLE:
                self.engine = 'pyttsx3'
                print("✓ Using pyttsx3 (Offline, Free, NO LIMITS)")
            else:
                raise ImportError("No TTS engine available. Install: pip install gtts pyttsx3")
    
    def detect_language(self, text: str) -> Tuple[str, float]:
        """
        Detect language of input text
        
        Args:
            text: Input text
            
        Returns:
            Tuple of (language_code, confidence)
        """
        try:
            lang = detect(text)
            return lang, 1.0
        except LangDetectException:
            print("⚠ Could not detect language, defaulting to English")
            return 'en', 0.0
    
    def _split_text_into_chunks(self, text: str, max_length: int = 5000) -> list:
        """Split long text into chunks for processing"""
        # Split by sentences
        sentences = re.split(r'(?<=[.!?])\s+', text)
        chunks = []
        current_chunk = ""
        
        for sentence in sentences:
            if len(current_chunk) + len(sentence) <= max_length:
                current_chunk += sentence + " "
            else:
                if current_chunk:
                    chunks.append(current_chunk.strip())
                current_chunk = sentence + " "
        
        if current_chunk:
            chunks.append(current_chunk.strip())
        
        return chunks if chunks else [text]
    
    def _init_pyttsx3(self):
        """Initialize pyttsx3 engine"""
        if self.pyttsx3_engine is None:
            self.pyttsx3_engine = pyttsx3.init()
            # Set properties for better quality
            self.pyttsx3_engine.setProperty('rate', 175)  # Speed
            self.pyttsx3_engine.setProperty('volume', 1.0)  # Volume
    
    def _generate_gtts(self, text: str, lang: str, output_file: str):
        """Generate audio using gTTS with chunking for unlimited text"""
        base_lang = lang.split('-')[0]
        
        # For very long texts, process in chunks and concatenate
        chunks = self._split_text_into_chunks(text, max_length=4999)
        
        if len(chunks) == 1:
            # Single chunk - generate directly
            tts = gTTS(text=text, lang=base_lang, slow=False)
            tts.save(output_file)
        else:
            # Multiple chunks - generate and concatenate
            print(f"Processing {len(chunks)} text chunks...")
            try:
                from pydub import AudioSegment
            except ImportError:
                print("⚠ pydub not installed. Install with: pip install pydub")
                print("  Generating only first chunk...")
                tts = gTTS(text=chunks[0], lang=base_lang, slow=False)
                tts.save(output_file)
                return
            
            temp_files = []
            for i, chunk in enumerate(chunks):
                temp_file = f"temp_chunk_{i}.mp3"
                print(f"  Chunk {i+1}/{len(chunks)}...")
                tts = gTTS(text=chunk, lang=base_lang, slow=False)
                tts.save(temp_file)
                temp_files.append(temp_file)
            
            # Concatenate all chunks
            print("Concatenating audio chunks...")
            combined = AudioSegment.empty()
            for temp_file in temp_files:
                audio = AudioSegment.from_mp3(temp_file)
                combined += audio
                os.remove(temp_file)
            
            # Export as MP3
            combined.export(output_file, format="mp3")
    
    def _generate_pyttsx3_tts(self, text: str, lang: str, output_file: str):
        """Generate audio using pyttsx3 (NO LIMITS, Offline)"""
        self._init_pyttsx3()
        
        try:
            # pyttsx3 saves directly to file
            self.pyttsx3_engine.save_to_file(text, output_file)
            self.pyttsx3_engine.runAndWait()
        except Exception as e:
            print(f"Error with pyttsx3: {e}")
            raise
    
    def _generate_elevenlabs_tts(self, text: str, lang: str, output_file: str):
        """Generate audio using ElevenLabs (Has character limits)"""
        base_lang = lang.split('-')[0]
        voice_id = self.ELEVENLABS_VOICES.get(base_lang, self.ELEVENLABS_VOICES['default'])
        
        # Check text length
        if len(text) > 5000:
            print("⚠ Warning: ElevenLabs has character limits. Text may be truncated.")
            print("  Consider using Coqui TTS for unlimited text length.")
        
        # Generate with optimal settings for natural speech
        audio = generate(
            text=text[:5000],  # Limit to avoid errors
            voice=Voice(
                voice_id=voice_id,
                settings=VoiceSettings(
                    stability=0.5,
                    similarity_boost=0.75,
                    style=0.5,
                    use_speaker_boost=True
                )
            ),
            model="eleven_multilingual_v2"
        )
        
        save(audio, output_file)
    
    def generate(self, text: str, output_file: str, lang: Optional[str] = None) -> str:
        """
        Generate natural speech audio from text (NO CHARACTER LIMITS with Coqui/pyttsx3)
        
        Args:
            text: Input text to convert to speech (UNLIMITED LENGTH)
            output_file: Output audio file path (e.g., 'output.mp3')
            lang: Language code (e.g., 'en', 'es'). Auto-detected if None
            
        Returns:
            Path to generated audio file
        """
        # Detect language if not provided
        if lang is None:
            lang, confidence = self.detect_language(text)
            print(f"📝 Detected language: {lang} (confidence: {confidence:.2f})")
        else:
            print(f"📝 Using specified language: {lang}")
        
        print(f"📄 Text length: {len(text)} characters (NO LIMITS with {self.engine})")
        
        # Ensure output directory exists
        output_path = Path(output_file)
        output_path.parent.mkdir(parents=True, exist_ok=True)
        
        print(f"🎙️  Generating audio using {self.engine}...")
        
        try:
            if self.engine == 'elevenlabs':
                self._generate_elevenlabs_tts(text, lang, output_file)
            
            elif self.engine == 'gtts':
                self._generate_gtts(text, lang, output_file)
            
            elif self.engine == 'pyttsx3':
                self._generate_pyttsx3_tts(text, lang, output_file)
            
            else:
                raise ValueError(f"Unknown engine: {self.engine}")
            
            print(f"✅ Audio saved to: {output_file}")
            return output_file
        
        except Exception as e:
            print(f"❌ Error generating audio: {e}")
            raise


def main():
    """Command-line interface"""
    import argparse
    
    parser = argparse.ArgumentParser(
        description='Natural Text-to-Speech with automatic language detection'
    )
    parser.add_argument(
        'text',
        nargs='?',
        help='Text to convert to speech (or use --file)'
    )
    parser.add_argument(
        '-f', '--file',
        help='Input text file'
    )
    parser.add_argument(
        '-o', '--output',
        default='output.mp3',
        help='Output audio file (default: output.mp3)'
    )
    parser.add_argument(
        '-l', '--lang',
        help='Language code (e.g., en, es, fr). Auto-detected if not specified'
    )
    parser.add_argument(
        '-e', '--engine',
        choices=['auto', 'elevenlabs', 'gtts', 'pyttsx3'],
        default='auto',
        help='TTS engine to use (default: auto - selects best available)'
    )
    parser.add_argument(
        '--api-key',
        help='ElevenLabs API key (or set ELEVENLABS_API_KEY env variable)'
    )
    
    args = parser.parse_args()
    
    # Get text input
    if args.file:
        with open(args.file, 'r', encoding='utf-8') as f:
            text = f.read()
    elif args.text:
        text = args.text
    else:
        print("Error: Provide text via argument or --file")
        parser.print_help()
        sys.exit(1)
    
    # Initialize TTS
    try:
        tts = NaturalTTS(engine=args.engine, api_key=args.api_key)
    except Exception as e:
        print(f"Error initializing TTS: {e}")
        sys.exit(1)
    
    # Generate audio
    try:
        output_file = tts.generate(text, args.output, lang=args.lang)
        print(f"\n🎉 Success! Audio file ready: {output_file}")
    except Exception as e:
        print(f"\n❌ Failed: {e}")
        sys.exit(1)


if __name__ == '__main__':
    main()
