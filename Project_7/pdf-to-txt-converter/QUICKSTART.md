# 🚀 Inicio Rápido - PDF to TXT Converter

## ⚡ Instalación Express (3 pasos)

### Windows

1. **Instalar dependencias**:
   ```bash
   pip install -r requirements.txt
   ```

2. **Ejecutar el script de inicio**:
   - Doble clic en `run.bat` o `run.ps1`
   - O desde terminal: `streamlit run app.py`

3. **¡Listo!** La aplicación se abre en tu navegador

### Linux/Mac

1. **Instalar dependencias**:
   ```bash
   pip install -r requirements.txt
   ```

2. **Ejecutar**:
   ```bash
   streamlit run app.py
   ```

3. **Acceder**: Abre http://localhost:8501 en tu navegador

## 📝 Uso en 4 pasos

### Paso 1: Subir PDFs
- Clic en "Browse files"
- Selecciona uno o varios PDFs (Ctrl+clic para múltiples)
- Puedes subir PDFs de cualquier tamaño

### Paso 2: Configurar (opcional)
En el sidebar izquierdo:
- **Método de extracción**: Deja en "auto" (recomendado)
- **Procesamiento paralelo**: Activado (más rápido)
- **Hilos**: 4-8 (según tu CPU)

### Paso 3: Convertir
- Clic en "🚀 Convertir todos los PDFs"
- Espera a que termine (verás el progreso)

### Paso 4: Descargar
En la pestaña "📥 Descargas":
- **Individual**: Botones para cada archivo .txt
- **Todo junto**: Botón "📦 Descargar todos como ZIP"

## 🎯 Casos de Uso Rápidos

### Convertir un solo PDF
```
1. Subir 1 PDF
2. Clic en "Convertir"
3. Descargar el .txt
```

### Convertir múltiples PDFs
```
1. Subir varios PDFs (Ctrl+clic)
2. Activar "Procesamiento paralelo"
3. Clic en "Convertir"
4. Descargar como ZIP
```

### PDF problemático (no se convierte)
```
1. Cambiar método a "PyMuPDF"
2. Intentar de nuevo
3. Si falla, probar "pdfplumber"
```

## ⚙️ Configuración Recomendada

### Para PDFs simples (cartas, contratos)
```
Método: auto o PyPDF2
Paralelo: ✓ Activado
Hilos: 4
```

### Para PDFs complejos (libros, reportes)
```
Método: PyMuPDF
Paralelo: ✓ Activado
Hilos: 4-6
```

### Para PDFs con tablas
```
Método: pdfplumber
Paralelo: ✓ Activado
Hilos: 4
```

### Para muchos PDFs pequeños
```
Método: auto
Paralelo: ✓ Activado
Hilos: 8-10
```

### Para pocos PDFs muy grandes
```
Método: PyMuPDF
Paralelo: ✗ Desactivado (usa menos RAM)
Hilos: 1-2
```

## 🔍 Vista Previa

Antes de descargar:
1. Ve a la pestaña "📋 Resultados"
2. Clic en el archivo que quieres revisar
3. Lee los primeros 1000 caracteres
4. Si está bien, descarga

## 💾 Formatos de Salida

Los archivos TXT mantienen:
- ✅ Todo el texto del PDF
- ✅ Saltos de línea
- ✅ Párrafos
- ✅ Encoding UTF-8

**No mantienen**:
- ❌ Formato (negritas, cursivas)
- ❌ Imágenes
- ❌ Colores
- ❌ Fuentes

## 🐛 Problemas Comunes

### "ModuleNotFoundError"
```bash
pip install -r requirements.txt
```

### PDF no se convierte
- Cambia el método de extracción
- Prueba: auto → PyMuPDF → pdfplumber → PyPDF2

### Aplicación muy lenta
- Activa procesamiento paralelo
- Aumenta número de hilos
- Procesa menos PDFs a la vez

### Error de memoria
- Desactiva procesamiento paralelo
- Reduce número de hilos a 1-2
- Procesa PDFs en lotes más pequeños

## 📱 Compatibilidad

### Navegadores soportados
- ✅ Chrome/Edge (recomendado)
- ✅ Firefox
- ✅ Safari
- ⚠️ Internet Explorer (no recomendado)

### Sistemas operativos
- ✅ Windows 10/11
- ✅ macOS 10.14+
- ✅ Linux (Ubuntu, Debian, etc.)

### Python
- ✅ Python 3.8+
- ✅ Python 3.9 (recomendado)
- ✅ Python 3.10
- ✅ Python 3.11

## 🎓 Tips y Trucos

1. **Usa "auto"**: Deja que la app elija el mejor método
2. **Procesamiento paralelo**: Siempre activado para múltiples PDFs
3. **Vista previa**: Revisa antes de descargar
4. **ZIP para muchos**: Si tienes +5 PDFs, descarga el ZIP
5. **Método específico**: Si sabes que tus PDFs son complejos, usa PyMuPDF directamente

## 🔗 Enlaces Útiles

- **Streamlit Docs**: https://docs.streamlit.io/
- **PyPDF2**: https://pypdf2.readthedocs.io/
- **pdfplumber**: https://github.com/jsvine/pdfplumber
- **PyMuPDF**: https://pymupdf.readthedocs.io/

## ❓ Preguntas Frecuentes

**¿Hay límite de tamaño?**
No, puedes convertir PDFs de cualquier tamaño.

**¿Hay límite de archivos?**
No, pero tu navegador puede ralentizarse con +50 archivos.

**¿Es seguro?**
Sí, todo se procesa localmente en tu PC. No se envía nada a internet.

**¿Guarda mis archivos?**
No, todo se borra cuando cierras la aplicación.

**¿Funciona offline?**
Sí, una vez instaladas las dependencias.

---

**¿Problemas?** Consulta el README.md completo o abre un issue.
