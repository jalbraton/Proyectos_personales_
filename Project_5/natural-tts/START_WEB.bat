@echo off
echo.
echo ========================================
echo   Natural TTS - Web Interface
echo ========================================
echo.
echo Starting server...
echo Open: http://localhost:5000
echo.
echo Press Ctrl+C to stop
echo.

cd /d "%~dp0"
python web_interface.py

pause
