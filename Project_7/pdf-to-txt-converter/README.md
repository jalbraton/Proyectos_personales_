# 📄 PDF to TXT Converter

Aplicación web moderna para convertir múltiples archivos PDF a TXT de forma rápida y eficiente.

## 🌟 Características

- ✅ **Conversión múltiple**: Procesa varios PDFs simultáneamente
- ✅ **Sin límite de tamaño**: Maneja PDFs grandes y pequeños sin problemas
- ✅ **3 métodos de extracción**: PyPDF2, pdfplumber y PyMuPDF para máxima compatibilidad
- ✅ **Modo automático**: Selecciona automáticamente el mejor método para cada PDF
- ✅ **Procesamiento paralelo**: Utiliza múltiples hilos para mayor velocidad
- ✅ **Interfaz web moderna**: Desarrollada con Streamlit
- ✅ **Descarga individual o ZIP**: Descarga archivos uno por uno o todos juntos
- ✅ **Vista previa**: Visualiza el contenido extraído antes de descargar

## 🚀 Instalación

### 1. Clonar o descargar el proyecto

```bash
cd Project_7/pdf-to-txt-converter
```

### 2. Instalar dependencias

```bash
pip install -r requirements.txt
```

## 📖 Uso

### Iniciar la aplicación

```bash
streamlit run app.py
```

La aplicación se abrirá automáticamente en tu navegador en `http://localhost:8501`

### Pasos para convertir PDFs

1. **Subir archivos**: Haz clic en "Browse files" y selecciona uno o varios PDFs
   - Puedes seleccionar múltiples archivos con Ctrl (Windows) o Cmd (Mac)

2. **Configurar opciones** (sidebar):
   - Método de extracción (recomendado: "auto")
   - Activar/desactivar procesamiento paralelo
   - Ajustar número de hilos

3. **Convertir**: Haz clic en "🚀 Convertir todos los PDFs"

4. **Descargar resultados**:
   - Descarga individual: Botones para cada archivo
   - Descarga ZIP: Todos los archivos en un solo archivo comprimido

## 🔧 Métodos de Extracción

### PyPDF2
- **Velocidad**: ⚡⚡⚡ Muy rápido
- **Compatibilidad**: ⭐⭐ Básica
- **Uso**: PDFs simples con texto estándar

### pdfplumber
- **Velocidad**: ⚡⚡ Rápido
- **Compatibilidad**: ⭐⭐⭐ Alta
- **Uso**: PDFs con tablas, columnas múltiples y layouts complejos

### PyMuPDF (fitz)
- **Velocidad**: ⚡⚡ Rápido
- **Compatibilidad**: ⭐⭐⭐⭐ Muy alta
- **Uso**: PDFs grandes, complejos, o con texto vectorial

### Auto (Recomendado)
Prueba automáticamente los 3 métodos en orden de efectividad hasta encontrar uno que funcione.

## 💡 Características Avanzadas

### Procesamiento Paralelo
- Activa "Procesamiento paralelo" en el sidebar
- Ajusta el número de hilos según tu CPU
- Recomendado: 4-8 hilos para mejor rendimiento

### Manejo de Errores
- Si un archivo falla, la aplicación continúa con los demás
- Los errores se muestran en la pestaña "❌ Errores"
- Sugerencia automática de métodos alternativos

## 📊 Métricas y Estadísticas

La aplicación muestra:
- ✅ Número de conversiones exitosas
- ❌ Número de fallos
- 📝 Total de caracteres extraídos
- 💾 Tamaño total de archivos procesados
- ⏱️ Progreso en tiempo real

## 🎨 Interfaz

La aplicación cuenta con:
- **Dashboard principal**: Subida y conversión de archivos
- **Sidebar**: Configuración avanzada
- **Tabs de resultados**:
  - 📋 Vista general de resultados
  - 📥 Descargas
  - ❌ Errores (si los hay)
- **Vista previa**: Primeros 1000 caracteres de cada archivo

## 🔐 Privacidad y Seguridad

- ✅ Todo el procesamiento es **local**
- ✅ No se envían archivos a servidores externos
- ✅ No se almacenan datos después de cerrar la aplicación
- ✅ Los archivos temporales se limpian automáticamente

## 📝 Ejemplos de Uso

### Caso 1: PDFs académicos
```
Método recomendado: PyMuPDF o auto
Procesamiento paralelo: Activado
Ideal para: Papers, libros digitales, documentos escaneados
```

### Caso 2: Documentos corporativos con tablas
```
Método recomendado: pdfplumber
Procesamiento paralelo: Activado
Ideal para: Reportes, facturas, documentos con tablas complejas
```

### Caso 3: PDFs simples (contratos, cartas)
```
Método recomendado: PyPDF2 o auto
Procesamiento paralelo: Activado
Ideal para: Documentos de texto simple sin elementos complejos
```

## 🐛 Solución de Problemas

### El PDF no se convierte correctamente
- **Solución**: Cambia el método de extracción
- Prueba en este orden: PyMuPDF → pdfplumber → PyPDF2

### La conversión es lenta
- **Solución**: Activa procesamiento paralelo
- Aumenta el número de hilos (4-8 recomendado)

### Error de memoria con PDFs muy grandes
- **Solución**: Desactiva procesamiento paralelo
- Reduce el número de hilos
- Procesa menos archivos a la vez

### El texto extraído está desordenado
- **Solución**: Usa pdfplumber o PyMuPDF
- Estos métodos mantienen mejor el layout original

## 🔄 Actualizaciones Futuras

Posibles mejoras:
- [ ] OCR para PDFs escaneados (Tesseract)
- [ ] Conversión a otros formatos (DOCX, Markdown)
- [ ] Configuración de encoding de salida
- [ ] Filtros de limpieza de texto
- [ ] API REST para integración

## 🤝 Contribuciones

Este es un proyecto personal, pero las sugerencias son bienvenidas.

## 📄 Licencia

Proyecto de uso libre para fines educativos y personales.

## 👨‍💻 Autor

José Alberto Pastor Llorente

---

**¿Necesitas ayuda?** Abre un issue o consulta la documentación de las librerías:
- [PyPDF2 Docs](https://pypdf2.readthedocs.io/)
- [pdfplumber Docs](https://github.com/jsvine/pdfplumber)
- [PyMuPDF Docs](https://pymupdf.readthedocs.io/)
- [Streamlit Docs](https://docs.streamlit.io/)
