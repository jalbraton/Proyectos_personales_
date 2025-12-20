"""
Simple web interface for Natural TTS Generator
"""

from flask import Flask, render_template, request, send_file, jsonify
from flask_cors import CORS
import os
from pathlib import Path
from tts_generator import NaturalTTS
import tempfile
import uuid
import traceback

app = Flask(__name__)
CORS(app)  # Enable CORS for all routes
app.config['MAX_CONTENT_LENGTH'] = 50 * 1024 * 1024  # 50MB max (NO TEXT LIMITS)

# Output directory for generated audio
OUTPUT_DIR = Path("generated_audio")
OUTPUT_DIR.mkdir(exist_ok=True)

# Initialize TTS
tts = None

def get_tts():
    """Get or create TTS instance"""
    global tts
    if tts is None:
        api_key = os.getenv('ELEVENLABS_API_KEY')
        tts = NaturalTTS(engine='auto', api_key=api_key)
    return tts


@app.route('/')
def index():
    """Main page"""
    return render_template('index.html')


@app.route('/generate', methods=['POST'])
def generate():
    """Generate speech from text"""
    try:
        data = request.get_json()
        text = data.get('text', '').strip()
        lang = data.get('lang')  # Optional
        
        if not text:
            return jsonify({'error': 'No text provided'}), 400
        
        if len(text) > 500000:  # 500k characters limit (very generous)
            return jsonify({'error': 'Text too long (max 500,000 characters)'}), 400
        
        print(f"📝 Generating audio for {len(text)} characters...")
        
        # Generate unique filename
        file_id = str(uuid.uuid4())
        output_file = OUTPUT_DIR / f"{file_id}.mp3"
        
        # Generate audio
        tts_instance = get_tts()
        tts_instance.generate(text, str(output_file), lang=lang)
        
        print(f"✅ Audio generated: {file_id}")
        
        return jsonify({
            'success': True,
            'file_id': file_id,
            'download_url': f'/download/{file_id}'
        })
    
    except Exception as e:
        error_msg = str(e)
        print(f"❌ Error: {error_msg}")
        traceback.print_exc()
        return jsonify({'error': error_msg}), 500


@app.route('/download/<file_id>')
def download(file_id):
    """Download generated audio file"""
    try:
        # Validate file_id (UUID format)
        uuid.UUID(file_id)
        
        file_path = OUTPUT_DIR / f"{file_id}.mp3"
        
        if not file_path.exists():
            return "File not found", 404
        
        return send_file(
            file_path,
            as_attachment=True,
            download_name='speech.mp3',
            mimetype='audio/mpeg'
        )
    
    except ValueError:
        return "Invalid file ID", 400
    except Exception as e:
        return f"Error: {e}", 500


@app.route('/health')
def health():
    """Health check endpoint"""
    return jsonify({'status': 'ok'})


if __name__ == '__main__':
    print("🚀 Starting Natural TTS Web Interface")
    print("📝 Open your browser at: http://localhost:5000")
    print("💡 Using gTTS engine (NO CHARACTER LIMITS)")
    app.run(debug=False, host='127.0.0.1', port=5000, threaded=True)
