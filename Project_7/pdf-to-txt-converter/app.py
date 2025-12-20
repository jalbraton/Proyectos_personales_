"""
PDF to TXT Converter - Web Application
Conversión masiva de PDFs a TXT con interfaz web moderna
"""

import streamlit as st
from pathlib import Path
import zipfile
import io
import os
from typing import List, Tuple
from concurrent.futures import ThreadPoolExecutor, as_completed

# Importaciones opcionales - usa lo que esté disponible
try:
    import PyPDF2
    HAS_PYPDF2 = True
except ImportError:
    HAS_PYPDF2 = False

try:
    import pdfplumber
    HAS_PDFPLUMBER = True
except ImportError:
    HAS_PDFPLUMBER = False

try:
    import fitz  # PyMuPDF
    HAS_PYMUPDF = True
except ImportError:
    HAS_PYMUPDF = False

# Configuración de la página
st.set_page_config(
    page_title="PDF to TXT Converter",
    page_icon="📄",
    layout="wide",
    initial_sidebar_state="expanded"
)

# CSS personalizado
st.markdown("""
    <style>
    .main-header {
        text-align: center;
        padding: 1rem;
        background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
        color: white;
        border-radius: 10px;
        margin-bottom: 2rem;
    }
    .success-box {
        padding: 1rem;
        background-color: #d4edda;
        border: 1px solid #c3e6cb;
        border-radius: 5px;
        margin: 1rem 0;
    }
    .info-box {
        padding: 1rem;
        background-color: #d1ecf1;
        border: 1px solid #bee5eb;
        border-radius: 5px;
        margin: 1rem 0;
    }
    .stDownloadButton button {
        background-color: #667eea;
        color: white;
    }
    </style>
""", unsafe_allow_html=True)

# Título principal
st.markdown("""
    <div class="main-header">
        <h1>📄 PDF to TXT Converter</h1>
        <p>Convierte múltiples PDFs a TXT de forma rápida y eficiente</p>
    </div>
""", unsafe_allow_html=True)


class PDFConverter:
    """
    Conversor avanzado de PDF a TXT usando múltiples métodos para máxima compatibilidad
    """
    
    @staticmethod
    def extract_with_pypdf2(pdf_file) -> Tuple[str, bool]:
        """Extrae texto usando PyPDF2 (rápido, básico)"""
        if not HAS_PYPDF2:
            return "PyPDF2 no está instalado. Ejecuta: pip install PyPDF2", False
        try:
            pdf_reader = PyPDF2.PdfReader(pdf_file)
            text = ""
            for page in pdf_reader.pages:
                text += page.extract_text() + "\n\n"
            return text, bool(text.strip())
        except Exception as e:
            return f"Error PyPDF2: {str(e)}", False
    
    @staticmethod
    def extract_with_pdfplumber(pdf_file) -> Tuple[str, bool]:
        """Extrae texto usando pdfplumber (mejor para tablas y layouts complejos)"""
        if not HAS_PDFPLUMBER:
            return "pdfplumber no está instalado. Ejecuta: pip install pdfplumber", False
        try:
            with pdfplumber.open(pdf_file) as pdf:
                text = ""
                for page in pdf.pages:
                    page_text = page.extract_text()
                    if page_text:
                        text += page_text + "\n\n"
            return text, bool(text.strip())
        except Exception as e:
            return f"Error pdfplumber: {str(e)}", False
    
    @staticmethod
    def extract_with_pymupdf(pdf_file) -> Tuple[str, bool]:
        """Extrae texto usando PyMuPDF/fitz (excelente para PDFs complejos)"""
        if not HAS_PYMUPDF:
            return "PyMuPDF no está instalado. Ejecuta: pip install PyMuPDF", False
        try:
            # Guardar temporalmente si es BytesIO
            if hasattr(pdf_file, 'read'):
                pdf_file.seek(0)
                pdf_bytes = pdf_file.read()
                doc = fitz.open(stream=pdf_bytes, filetype="pdf")
            else:
                doc = fitz.open(pdf_file)
            
            text = ""
            for page_num in range(len(doc)):
                page = doc[page_num]
                text += page.get_text() + "\n\n"
            
            doc.close()
            return text, bool(text.strip())
        except Exception as e:
            return f"Error PyMuPDF: {str(e)}", False
    
    @classmethod
    def convert_pdf_to_txt(cls, pdf_file, filename: str, method: str = "auto") -> Tuple[str, str, bool]:
        """
        Convierte un PDF a TXT usando el método especificado
        
        Returns:
            (texto_extraído, nombre_archivo, éxito)
        """
        methods = {
            "PyPDF2": cls.extract_with_pypdf2,
            "pdfplumber": cls.extract_with_pdfplumber,
            "PyMuPDF": cls.extract_with_pymupdf,
        }
        
        if method == "auto":
            # Probar métodos en orden de efectividad
            for method_name, method_func in methods.items():
                text, success = method_func(pdf_file)
                if success:
                    return text, filename, True
            # Si ninguno funciona, retornar el último intento
            return text, filename, False
        else:
            text, success = methods.get(method, cls.extract_with_pymupdf)(pdf_file)
            return text, filename, success


def process_single_pdf(pdf_file, filename: str, method: str) -> dict:
    """Procesa un solo PDF y retorna los resultados"""
    try:
        text, name, success = PDFConverter.convert_pdf_to_txt(pdf_file, filename, method)
        
        return {
            "filename": filename,
            "success": success,
            "text": text,
            "size": len(text),
            "pages": text.count("\n\n") if success else 0
        }
    except Exception as e:
        return {
            "filename": filename,
            "success": False,
            "text": f"Error: {str(e)}",
            "size": 0,
            "pages": 0
        }


def create_zip_file(results: List[dict]) -> bytes:
    """Crea un archivo ZIP con todos los TXTs generados"""
    zip_buffer = io.BytesIO()
    
    with zipfile.ZipFile(zip_buffer, 'w', zipfile.ZIP_DEFLATED) as zip_file:
        for result in results:
            if result['success']:
                # Cambiar extensión a .txt
                txt_filename = Path(result['filename']).stem + '.txt'
                zip_file.writestr(txt_filename, result['text'])
    
    zip_buffer.seek(0)
    return zip_buffer.getvalue()


# Verificar librerías instaladas
available_methods = ["auto"]
if HAS_PYPDF2:
    available_methods.append("PyPDF2")
if HAS_PDFPLUMBER:
    available_methods.append("pdfplumber")
if HAS_PYMUPDF:
    available_methods.append("PyMuPDF")

if len(available_methods) == 1:
    st.error("⚠️ No hay librerías PDF instaladas. Instala al menos una:")
    st.code("pip install PyPDF2 pdfplumber PyMuPDF")
    st.stop()

# Sidebar - Configuración
with st.sidebar:
    st.header("⚙️ Configuración")
    
    # Mostrar solo métodos disponibles
    conversion_method = st.selectbox(
        "Método de extracción",
        available_methods,
        help="""
        - **auto**: Prueba automáticamente el mejor método
        - **PyPDF2**: Rápido, para PDFs simples
        - **pdfplumber**: Mejor para tablas y layouts complejos
        - **PyMuPDF**: Excelente para PDFs complejos y grandes
        """
    )
    
    # Mostrar qué librerías están disponibles
    st.divider()
    st.write("**📚 Librerías disponibles:**")
    st.write(f"{'✅' if HAS_PYPDF2 else '❌'} PyPDF2")
    st.write(f"{'✅' if HAS_PDFPLUMBER else '❌'} pdfplumber")
    st.write(f"{'✅' if HAS_PYMUPDF else '❌'} PyMuPDF")
    
    if not HAS_PYPDF2 or not HAS_PDFPLUMBER or not HAS_PYMUPDF:
        st.warning("💡 Para mejor compatibilidad, instala todas las librerías:")
        st.code("pip install PyPDF2 pdfplumber PyMuPDF")
    
    st.divider()
    
    parallel_processing = st.checkbox(
        "Procesamiento paralelo",
        value=True,
        help="Procesa múltiples PDFs simultáneamente (más rápido)"
    )
    
    max_workers = st.slider(
        "Número de hilos (si paralelo)",
        min_value=1,
        max_value=10,
        value=4,
        help="Más hilos = más rápido, pero más uso de CPU"
    )
    
    st.divider()
    
    st.header("ℹ️ Información")
    st.info("""
    **Características:**
    - ✅ Múltiples PDFs simultáneos
    - ✅ PDFs de cualquier tamaño
    - ✅ 3 métodos de extracción
    - ✅ Descarga individual o ZIP
    - ✅ Procesamiento paralelo
    """)
    
    st.divider()
    
    with st.expander("🔧 Métodos de extracción"):
        st.markdown("""
        **PyPDF2**: Rápido y ligero, ideal para PDFs simples con texto estándar.
        
        **pdfplumber**: Excelente para PDFs con tablas, columnas múltiples y layouts complejos.
        
        **PyMuPDF (fitz)**: El más robusto, maneja PDFs grandes y complejos, extrae texto de imágenes vectoriales.
        
        **Auto**: Prueba automáticamente los 3 métodos hasta encontrar uno que funcione.
        """)

# Área principal
col1, col2 = st.columns([2, 1])

with col1:
    st.header("📤 Subir archivos PDF")
    uploaded_files = st.file_uploader(
        "Selecciona uno o varios PDFs",
        type=['pdf'],
        accept_multiple_files=True,
        help="Puedes seleccionar múltiples archivos manteniendo Ctrl (Windows) o Cmd (Mac)"
    )

with col2:
    if uploaded_files:
        st.metric("📁 Archivos cargados", len(uploaded_files))
        total_size = sum(file.size for file in uploaded_files)
        st.metric("💾 Tamaño total", f"{total_size / 1024 / 1024:.2f} MB")

# Procesamiento
if uploaded_files:
    st.divider()
    
    if st.button("🚀 Convertir todos los PDFs", type="primary", use_container_width=True):
        progress_bar = st.progress(0)
        status_text = st.empty()
        results_container = st.empty()
        
        results = []
        
        with st.spinner("Procesando PDFs..."):
            if parallel_processing and len(uploaded_files) > 1:
                # Procesamiento paralelo
                with ThreadPoolExecutor(max_workers=max_workers) as executor:
                    futures = {
                        executor.submit(
                            process_single_pdf,
                            file,
                            file.name,
                            conversion_method
                        ): file for file in uploaded_files
                    }
                    
                    completed = 0
                    for future in as_completed(futures):
                        result = future.result()
                        results.append(result)
                        completed += 1
                        
                        progress = completed / len(uploaded_files)
                        progress_bar.progress(progress)
                        status_text.text(f"Procesando: {completed}/{len(uploaded_files)} archivos")
            else:
                # Procesamiento secuencial
                for idx, file in enumerate(uploaded_files):
                    status_text.text(f"Procesando: {file.name}")
                    result = process_single_pdf(file, file.name, conversion_method)
                    results.append(result)
                    
                    progress = (idx + 1) / len(uploaded_files)
                    progress_bar.progress(progress)
        
        # Mostrar resultados
        status_text.empty()
        progress_bar.empty()
        
        successful = [r for r in results if r['success']]
        failed = [r for r in results if not r['success']]
        
        # Resumen
        col1, col2, col3 = st.columns(3)
        with col1:
            st.metric("✅ Exitosos", len(successful))
        with col2:
            st.metric("❌ Fallidos", len(failed))
        with col3:
            total_chars = sum(r['size'] for r in successful)
            st.metric("📝 Caracteres totales", f"{total_chars:,}")
        
        st.divider()
        
        # Tabs para resultados
        tab1, tab2, tab3 = st.tabs(["📋 Resultados", "📥 Descargas", "❌ Errores"])
        
        with tab1:
            if successful:
                st.success(f"✅ {len(successful)} archivo(s) convertido(s) exitosamente")
                
                for result in successful:
                    with st.expander(f"📄 {result['filename']} - {result['size']:,} caracteres"):
                        st.text_area(
                            "Vista previa (primeros 1000 caracteres)",
                            result['text'][:1000] + "..." if len(result['text']) > 1000 else result['text'],
                            height=200,
                            key=f"preview_{result['filename']}"
                        )
        
        with tab2:
            if successful:
                st.subheader("Descargar archivos")
                
                # Descarga individual
                st.write("**Descarga individual:**")
                cols = st.columns(3)
                for idx, result in enumerate(successful):
                    with cols[idx % 3]:
                        txt_filename = Path(result['filename']).stem + '.txt'
                        st.download_button(
                            label=f"⬇️ {txt_filename}",
                            data=result['text'],
                            file_name=txt_filename,
                            mime="text/plain",
                            key=f"download_{idx}"
                        )
                
                st.divider()
                
                # Descarga en ZIP
                st.write("**Descarga todos en ZIP:**")
                zip_data = create_zip_file(successful)
                st.download_button(
                    label="📦 Descargar todos como ZIP",
                    data=zip_data,
                    file_name="converted_pdfs.zip",
                    mime="application/zip",
                    type="primary",
                    use_container_width=True
                )
        
        with tab3:
            if failed:
                st.error(f"❌ {len(failed)} archivo(s) fallaron")
                
                for result in failed:
                    with st.expander(f"❌ {result['filename']}"):
                        st.error(result['text'])
                        st.info("💡 Intenta con un método de extracción diferente")
            else:
                st.success("✅ No hubo errores en la conversión")
        
        # Guardar resultados en session state
        st.session_state['last_results'] = results

# Footer
st.divider()
st.markdown("""
    <div style='text-align: center; color: #666; padding: 2rem;'>
        <p><strong>PDF to TXT Converter</strong> | Desarrollado con ❤️ usando Streamlit</p>
        <p>Soporta PDFs de cualquier tamaño | Procesamiento paralelo optimizado</p>
    </div>
""", unsafe_allow_html=True)
