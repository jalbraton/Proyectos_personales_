#!/bin/bash

echo ""
echo "═══════════════════════════════════════════════════════"
echo "    TRADUCTOR DE EPUB - Instalación Automática"
echo "═══════════════════════════════════════════════════════"
echo ""

echo "[1/2] Verificando Python..."
if ! command -v python3 &> /dev/null; then
    echo "✗ Python no está instalado"
    echo "  Por favor, instala Python 3.7 o superior"
    exit 1
fi
python3 --version
echo "✓ Python detectado correctamente"
echo ""

echo "[2/2] Instalando dependencias..."
pip3 install -r requirements.txt
if [ $? -ne 0 ]; then
    echo "✗ Error al instalar dependencias"
    exit 1
fi

echo ""
echo "✓ Instalación completada exitosamente!"
echo ""
echo "═══════════════════════════════════════════════════════"
echo "Para ejecutar el traductor, usa:"
echo "    python3 traductor_epub.py"
echo "═══════════════════════════════════════════════════════"
echo ""
