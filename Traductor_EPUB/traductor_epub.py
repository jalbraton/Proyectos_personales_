#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Traductor de Libros EPUB
=========================
Programa para traducir libros en formato EPUB a diferentes idiomas.
Utiliza la API gratuita de Google Translate mediante la librería googletrans.
"""

import os
import sys
from pathlib import Path
from ebooklib import epub
from bs4 import BeautifulSoup

import time

# Configurar codificación UTF-8 para Windows
if sys.platform == 'win32':
    import codecs
    if sys.stdout.encoding != 'utf-8':
        sys.stdout = codecs.getwriter('utf-8')(sys.stdout.buffer, 'strict')
    if sys.stderr.encoding != 'utf-8':
        sys.stderr = codecs.getwriter('utf-8')(sys.stderr.buffer, 'strict')


class TraductorEPUB:
    """Clase para traducir archivos EPUB."""
    
    def __init__(self, motor_traduccion='googletrans'):
        """
        Inicializa el traductor.
        
        Args:
            motor_traduccion (str): 'googletrans' o 'deep-translator'
        """
        self.motor = motor_traduccion
        
        if motor_traduccion == 'googletrans':
            try:
                from googletrans import Translator, LANGUAGES
                self.translator = Translator()
                self.idiomas_disponibles = LANGUAGES
                print(f"✓ Motor de traducción: Google Translate (googletrans)")
            except ImportError:
                print("✗ Error: googletrans no está instalado.")
                print("  Instala con: pip install googletrans==4.0.0rc1")
                sys.exit(1)
        elif motor_traduccion == 'deep-translator':
            try:
                from deep_translator import GoogleTranslator
                self.translator = GoogleTranslator()
                # Deep translator usa códigos ISO 639-1 estándar
                # Obtener lista de idiomas soportados
                idiomas_lista = GoogleTranslator().get_supported_languages(as_dict=True)
                # Convertir a formato similar a googletrans
                self.idiomas_disponibles = {k: v for k, v in idiomas_lista.items()}
                print(f"✓ Motor de traducción: Deep Translator")
            except ImportError:
                print("✗ Error: deep-translator no está instalado.")
                print("  Instala con: pip install deep-translator")
                sys.exit(1)
            except Exception as e:
                print(f"⚠ Advertencia al cargar idiomas: {e}")
                # Lista básica de idiomas si falla
                self.idiomas_disponibles = {
                    'english': 'en', 'spanish': 'es', 'french': 'fr', 'german': 'de',
                    'italian': 'it', 'portuguese': 'pt', 'russian': 'ru', 'japanese': 'ja',
                    'chinese (simplified)': 'zh-CN', 'korean': 'ko', 'arabic': 'ar'
                }
                print(f"✓ Motor de traducción: Deep Translator (lista básica de idiomas)")
        else:
            print(f"✗ Error: Motor de traducción no válido: {motor_traduccion}")
            print("  Usa 'googletrans' o 'deep-translator'")
            sys.exit(1)
    
    def mostrar_idiomas_disponibles(self):
        """Muestra los idiomas disponibles para traducción."""
        print("\n" + "="*60)
        print("IDIOMAS DISPONIBLES PARA TRADUCCIÓN")
        print("="*60)
        
        if self.motor == 'googletrans':
            print("\nCódigos de idioma más comunes:")
            idiomas_comunes = {
                'es': 'Español',
                'en': 'Inglés',
                'fr': 'Francés',
                'de': 'Alemán',
                'it': 'Italiano',
                'pt': 'Portugués',
                'ru': 'Ruso',
                'ja': 'Japonés',
                'zh-cn': 'Chino (Simplificado)',
                'ko': 'Coreano',
                'ar': 'Árabe'
            }
            
            for codigo, nombre in idiomas_comunes.items():
                print(f"  {codigo:8} - {nombre}")
            
            print("\nPara ver todos los idiomas, visita: https://py-googletrans.readthedocs.io/en/latest/#googletrans-languages")
        
        elif self.motor == 'deep-translator':
            print("\nIdiomas más comunes (Deep Translator):")
            idiomas_comunes = [
                ('spanish', 'Español'),
                ('english', 'Inglés'),
                ('french', 'Francés'),
                ('german', 'Alemán'),
                ('italian', 'Italiano'),
                ('portuguese', 'Portugués'),
                ('russian', 'Ruso'),
                ('japanese', 'Japonés'),
                ('chinese (simplified)', 'Chino Simplificado'),
                ('korean', 'Coreano'),
                ('arabic', 'Árabe')
            ]
            
            for codigo, nombre in idiomas_comunes:
                print(f"  {codigo:25} - {nombre}")
            
            print("\nNota: Deep Translator usa nombres de idiomas completos en inglés")
        
        print("="*60 + "\n")
    
    def normalizar_codigo_idioma(self, codigo):
        """
        Normaliza el código de idioma según el motor usado.
        
        Args:
            codigo (str): Código ingresado por el usuario
            
        Returns:
            str: Código normalizado o None si no es válido
        """
        codigo = codigo.lower().strip()
        
        if self.motor == 'googletrans':
            # googletrans usa códigos ISO (es, en, fr, etc.)
            return codigo if codigo in self.idiomas_disponibles else None
        
        elif self.motor == 'deep-translator':
            # deep-translator puede usar nombres completos o códigos
            # Mapeo de códigos comunes a nombres completos
            mapeo_codigos = {
                'es': 'spanish',
                'en': 'english',
                'fr': 'french',
                'de': 'german',
                'it': 'italian',
                'pt': 'portuguese',
                'ru': 'russian',
                'ja': 'japanese',
                'zh-cn': 'chinese (simplified)',
                'zh': 'chinese (simplified)',
                'ko': 'korean',
                'ar': 'arabic'
            }
            
            # Si el usuario ingresó un código corto, convertirlo
            if codigo in mapeo_codigos:
                return mapeo_codigos[codigo]
            
            # Si ingresó el nombre completo, verificar que exista
            if codigo in self.idiomas_disponibles or codigo in self.idiomas_disponibles.values():
                return codigo
            
            return None
        
        return None
    
    def leer_epub(self, ruta_epub):
        """
        Lee un archivo EPUB y extrae su contenido.
        
        Args:
            ruta_epub (str): Ruta al archivo EPUB
            
        Returns:
            epub.EpubBook: Objeto libro de epub
        """
        try:
            libro = epub.read_epub(ruta_epub)
            print(f"✓ Archivo EPUB leído correctamente: {Path(ruta_epub).name}")
            
            # Obtener información del libro
            titulo = libro.get_metadata('DC', 'title')
            autor = libro.get_metadata('DC', 'creator')
            
            if titulo:
                print(f"  Título: {titulo[0][0]}")
            if autor:
                print(f"  Autor: {autor[0][0]}")
                
            return libro
        except Exception as e:
            print(f"✗ Error al leer el archivo EPUB: {e}")
            return None
    
    def traducir_texto(self, texto, idioma_destino, idioma_origen='auto'):
        """
        Traduce un texto al idioma especificado.
        
        Args:
            texto (str): Texto a traducir
            idioma_destino (str): Código del idioma destino
            idioma_origen (str): Código del idioma origen (auto-detectado por defecto)
            
        Returns:
            str: Texto traducido
        """
        try:
            if not texto or len(texto.strip()) == 0:
                return texto
            
            if self.motor == 'googletrans':
                # Google Translate con googletrans
                if len(texto) > 4500:
                    # Dividir en chunks más pequeños
                    partes = [texto[i:i+4500] for i in range(0, len(texto), 4500)]
                    resultado = ""
                    for parte in partes:
                        traduccion = self.translator.translate(parte, dest=idioma_destino, src=idioma_origen)
                        if traduccion and traduccion.text:
                            resultado += traduccion.text
                        time.sleep(0.5)  # Pausa para evitar límite de rate
                    return resultado if resultado else texto
                else:
                    traduccion = self.translator.translate(texto, dest=idioma_destino, src=idioma_origen)
                    return traduccion.text if traduccion and traduccion.text else texto
                    
            elif self.motor == 'deep-translator':
                # Deep Translator
                from deep_translator import GoogleTranslator
                
                if len(texto) > 4500:
                    # Dividir en chunks más pequeños
                    partes = [texto[i:i+4500] for i in range(0, len(texto), 4500)]
                    resultado = ""
                    for parte in partes:
                        traductor = GoogleTranslator(source=idioma_origen, target=idioma_destino)
                        traduccion = traductor.translate(parte)
                        if traduccion:
                            resultado += traduccion
                        time.sleep(0.5)
                    return resultado if resultado else texto
                else:
                    traductor = GoogleTranslator(source=idioma_origen, target=idioma_destino)
                    traduccion = traductor.translate(texto)
                    return traduccion if traduccion else texto
                    
        except Exception as e:
            print(f"  ⚠ Error al traducir fragmento: {e}")
            return texto  # Devolver texto original si falla
    
    def traducir_html(self, contenido_html, idioma_destino):
        """
        Traduce el contenido HTML preservando las etiquetas.
        
        Args:
            contenido_html (str): Contenido HTML a traducir
            idioma_destino (str): Código del idioma destino
            
        Returns:
            str: HTML traducido
        """
        try:
            soup = BeautifulSoup(contenido_html, 'html.parser')
            
            # Encontrar todos los textos en el HTML
            for elemento in soup.find_all(text=True):
                try:
                    if elemento.parent.name not in ['script', 'style']:
                        texto_original = str(elemento)
                        if texto_original.strip():
                            texto_traducido = self.traducir_texto(texto_original, idioma_destino)
                            
                            # Verificar que la traducción no sea None o vacía
                            if texto_traducido is not None and texto_traducido.strip():
                                elemento.replace_with(texto_traducido)
                            # Si es None o vacío, mantener el texto original
                except Exception as e:
                    # Si hay error con este elemento, continuar con el siguiente
                    print(f"    ⚠ Error en elemento HTML: {e}")
                    continue
            
            return str(soup)
        except Exception as e:
            print(f"    ⚠ Error al procesar HTML: {e}")
            # Devolver contenido original si hay error
            return contenido_html
    
    def traducir_epub(self, ruta_epub, idioma_destino, carpeta_destino=None):
        """
        Traduce un archivo EPUB completo.
        
        Args:
            ruta_epub (str): Ruta al archivo EPUB original
            idioma_destino (str): Código del idioma destino
            carpeta_destino (str): Carpeta donde guardar el archivo traducido (opcional)
            
        Returns:
            str: Ruta al archivo traducido
        """
        print(f"\n{'='*60}")
        
        # Mostrar idioma destino según el motor
        if self.motor == 'googletrans':
            nombre_idioma = self.idiomas_disponibles.get(idioma_destino, idioma_destino)
        else:
            nombre_idioma = idioma_destino
        
        print(f"Iniciando traducción a: {nombre_idioma}")
        print(f"{'='*60}\n")
        
        # Leer el EPUB
        libro = self.leer_epub(ruta_epub)
        if not libro:
            return None
        
        # Crear un nuevo libro traducido
        libro_traducido = epub.EpubBook()
        
        # Copiar metadatos
        try:
            identificador = libro.get_metadata('DC', 'identifier')
            if identificador and len(identificador) > 0:
                libro_traducido.set_identifier(identificador[0][0])
            else:
                libro_traducido.set_identifier('id_traducido_' + str(hash(ruta_epub)))
        except:
            libro_traducido.set_identifier('id_traducido_' + str(hash(ruta_epub)))
        
        titulo_original = libro.get_metadata('DC', 'title')
        if titulo_original and len(titulo_original) > 0:
            try:
                titulo_traducido = self.traducir_texto(titulo_original[0][0], idioma_destino)
                libro_traducido.set_title(titulo_traducido + " (Traducido)")
            except:
                libro_traducido.set_title("Libro Traducido")
        else:
            libro_traducido.set_title("Libro Traducido")
        
        # Configurar idioma del libro traducido
        mapeo_idiomas = {
            'spanish': 'es', 'english': 'en', 'french': 'fr', 'german': 'de',
            'italian': 'it', 'portuguese': 'pt', 'russian': 'ru', 'japanese': 'ja',
            'chinese (simplified)': 'zh', 'korean': 'ko', 'arabic': 'ar'
        }
        
        if self.motor == 'googletrans':
            codigo_iso = idioma_destino
            libro_traducido.set_language(idioma_destino)
        else:
            # Para deep-translator, convertir nombre a código ISO
            codigo_iso = mapeo_idiomas.get(idioma_destino, 'en')
            libro_traducido.set_language(codigo_iso)
        
        # Copiar autores
        autores = libro.get_metadata('DC', 'creator')
        if autores:
            for autor in autores:
                libro_traducido.add_author(autor[0])
        
        # Traducir los capítulos
        items = list(libro.get_items())
        # Filtrar solo documentos HTML/XHTML (type 9 en ebooklib)
        total_items = len([item for item in items if isinstance(item, epub.EpubHtml)])
        contador = 0
        
        print(f"Traduciendo {total_items} documentos...\n")
        
        # Copiar imágenes, CSS, etc. (no HTML)
        capitulos_traducidos = []
        for item in items:
            # Verificar si es un documento HTML
            if isinstance(item, epub.EpubHtml):
                # Saltar archivos de navegación especiales
                if item.get_name() in ['nav.xhtml', 'toc.ncx']:
                    continue
                
                contador += 1
                print(f"  [{contador}/{total_items}] Traduciendo: {item.get_name()}")
                
                try:
                    contenido = item.get_content()
                    if contenido:
                        contenido_str = contenido.decode('utf-8')
                        contenido_traducido = self.traducir_html(contenido_str, idioma_destino)
                        
                        # Crear nuevo item con contenido traducido
                        nuevo_item = epub.EpubHtml(
                            title=str(item.title) if item.title else 'Capítulo',
                            file_name=item.file_name,
                            lang=codigo_iso
                        )
                        nuevo_item.content = contenido_traducido
                        
                        libro_traducido.add_item(nuevo_item)
                        capitulos_traducidos.append(nuevo_item)
                    
                    time.sleep(1)  # Pausa para evitar límites de rate
                except Exception as e:
                    print(f"    ⚠ Error al traducir {item.get_name()}: {e}")
                    # Si falla, continuar con el siguiente
            else:
                # Copiar imágenes, CSS, fuentes, etc.
                try:
                    libro_traducido.add_item(item)
                except:
                    pass  # Ignorar si no se puede copiar
        
        # Configurar tabla de contenidos simple
        try:
            libro_traducido.toc = tuple(capitulos_traducidos)
        except:
            libro_traducido.toc = ()
        
        # Agregar archivos de navegación
        libro_traducido.add_item(epub.EpubNcx())
        libro_traducido.add_item(epub.EpubNav())
        
        # Definir spine (orden de lectura)
        libro_traducido.spine = ['nav'] + capitulos_traducidos
        
        # Generar nombre del archivo de salida
        nombre_original = Path(ruta_epub).stem
        idioma_para_nombre = idioma_destino.replace(' ', '_').replace('(', '').replace(')', '')
        nombre_archivo = f"{nombre_original}_traducido_{idioma_para_nombre}.epub"
        
        # Determinar carpeta de destino
        if carpeta_destino:
            # Usar carpeta especificada por el usuario
            carpeta = Path(carpeta_destino)
            if not carpeta.exists():
                print(f"\n⚠ La carpeta '{carpeta_destino}' no existe. Creándola...")
                try:
                    carpeta.mkdir(parents=True, exist_ok=True)
                except Exception as e:
                    print(f"✗ Error al crear carpeta: {e}")
                    print(f"  Usando carpeta original del archivo...")
                    carpeta = Path(ruta_epub).parent
            ruta_salida = carpeta / nombre_archivo
        else:
            # Usar la misma carpeta que el archivo original
            ruta_salida = Path(ruta_epub).parent / nombre_archivo
        
        # Guardar el libro traducido
        try:
            # Asegurarse de que todos los metadatos existen
            if not libro_traducido.title:
                libro_traducido.set_title("Libro Traducido")
            
            ruta_salida_str = str(ruta_salida)
            epub.write_epub(ruta_salida_str, libro_traducido, {})
            print(f"\n✓ Traducción completada exitosamente!")
            print(f"✓ Archivo guardado en: {ruta_salida_str}")
            return ruta_salida_str
        except Exception as e:
            print(f"\n✗ Error al guardar el archivo: {e}")
            import traceback
            traceback.print_exc()
            return None


def main():
    """Función principal del programa."""
    print("\n" + "="*60)
    print(" "*15 + "TRADUCTOR DE EPUB")
    print("="*60)
    
    # Seleccionar motor de traducción
    print("\nSelecciona el motor de traducción:")
    print("  1. Google Translate (googletrans) - Recomendado")
    print("  2. Deep Translator - Alternativa más estable")
    
    opcion = input("\nOpción (1 o 2) [1]: ").strip() or "1"
    
    if opcion == "1":
        motor = "googletrans"
    elif opcion == "2":
        motor = "deep-translator"
    else:
        print("✗ Opción no válida. Usando googletrans por defecto.")
        motor = "googletrans"
    
    traductor = TraductorEPUB(motor_traduccion=motor)
    
    # Solicitar archivo EPUB
    print("\nPor favor, ingresa la ruta del archivo EPUB:")
    print("(Puedes arrastrar el archivo a esta ventana)")
    ruta_epub = input("\nRuta del archivo EPUB: ").strip().strip('"')
    
    # Verificar que el archivo existe
    if not os.path.exists(ruta_epub):
        print(f"\n✗ Error: El archivo no existe: {ruta_epub}")
        return
    
    if not ruta_epub.lower().endswith('.epub'):
        print(f"\n✗ Error: El archivo debe tener extensión .epub")
        return
    
    # Mostrar idiomas disponibles
    traductor.mostrar_idiomas_disponibles()
    
    # Solicitar idioma de destino
    if motor == 'googletrans':
        print("Ingresa el código del idioma al que deseas traducir:")
        print("Ejemplo: 'es' para español, 'en' para inglés, 'fr' para francés")
    else:
        print("Ingresa el código del idioma al que deseas traducir:")
        print("Ejemplo: 'es' o 'spanish' para español, 'en' o 'english' para inglés")
    
    idioma_input = input("\nCódigo de idioma: ").strip().lower()
    
    # Normalizar y validar idioma
    idioma_destino = traductor.normalizar_codigo_idioma(idioma_input)
    
    if not idioma_destino:
        print(f"\n✗ Error: Código de idioma no válido: {idioma_input}")
        print("Por favor, usa un código válido de la lista.")
        return
    
    # Solicitar carpeta de destino
    print("\n" + "="*60)
    print("¿Dónde deseas guardar el archivo traducido?")
    print("="*60)
    print("\nOpciones:")
    print("  1. En la misma carpeta que el archivo original (Por defecto)")
    print("  2. En otra carpeta (especificar ruta)")
    
    opcion_carpeta = input("\nOpción (1 o 2) [1]: ").strip() or "1"
    
    carpeta_destino = None
    if opcion_carpeta == "2":
        print("\nIngresa la ruta de la carpeta donde guardar el archivo:")
        print("(Puedes arrastrar la carpeta a esta ventana)")
        carpeta_destino = input("\nRuta de carpeta destino: ").strip().strip('"')
        
        if carpeta_destino:
            print(f"✓ Se guardará en: {carpeta_destino}")
        else:
            print("⚠ No se especificó carpeta. Usando carpeta original.")
            carpeta_destino = None
    
    # Realizar la traducción
    resultado = traductor.traducir_epub(ruta_epub, idioma_destino, carpeta_destino)
    
    if resultado:
        print(f"\n{'='*60}")
        print("PROCESO COMPLETADO")
        print(f"{'='*60}\n")
    else:
        print(f"\n✗ La traducción no se pudo completar.")


if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print("\n\n✗ Proceso interrumpido por el usuario.")
        sys.exit(0)
    except Exception as e:
        print(f"\n✗ Error inesperado: {e}")
        sys.exit(1)
