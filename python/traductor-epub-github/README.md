<div align="center">

# 📚 Traductor de Libros EPUB 🌍

### Traduce tus libros electrónicos a más de 100 idiomas de forma gratuita

[![Python Version](https://img.shields.io/badge/python-3.7+-blue.svg)](https://www.python.org/downloads/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Platform](https://img.shields.io/badge/platform-Windows%20%7C%20Linux%20%7C%20macOS-lightgrey.svg)](https://github.com)

[Características](#-características) •
[Instalación](#-instalación) •
[Uso](#-uso) •
[Documentación](#-documentación) •
[Contribuir](#-contribuir)

</div>

---

## ✨ Características Principales

- 🌐 **Más de 100 idiomas** soportados
- 🔄 **Dos motores de traducción** (Google Translate y Deep Translator)
- 📖 **Preserva el formato** original del libro (HTML, CSS, imágenes)
- 💾 **Elige dónde guardar** tus traducciones
- 🛡️ **Manejo robusto de errores** - continúa traduciendo aunque falle algún fragmento
- 💻 **Interfaz interactiva** por consola
- 🆓 **Completamente gratuito** - usa APIs gratuitas

## 🆕 Últimas Mejoras (v1.1.0)

- ✅ **Mejor manejo de errores**: Soluciona el problema "cannot insert None into a tag"
- ✅ **Carpeta personalizada**: Elige dónde guardar tus archivos traducidos
- ✅ **Validación robusta**: Verifica traducciones antes de insertarlas
- ✅ **Creación automática de carpetas**: Si no existe, la crea automáticamente

## 📋 Requisitos

- Python 3.7 o superior
- Conexión a Internet (para la traducción)

## 🚀 Instalación Rápida

### Windows

```batch
# Clonar el repositorio
git clone https://github.com/tu-usuario/traductor-epub.git
cd traductor-epub

# Instalar automáticamente
instalar.bat
```

### Linux / macOS

```bash
# Clonar el repositorio
git clone https://github.com/tu-usuario/traductor-epub.git
cd traductor-epub

# Dar permisos y ejecutar
chmod +x instalar.sh
./instalar.sh
```

### Manual

```bash
pip install -r requirements.txt
```

## 💻 Uso

### Ejecución Simple

**Windows:**
```batch
ejecutar.bat
```

**Linux/macOS:**
```bash
chmod +x ejecutar.sh
./ejecutar.sh
```

**O directamente:**
```bash
python traductor_epub.py
```

### Pasos de Uso

1. **Selecciona el motor de traducción**
   - Opción 1: Google Translate (googletrans)
   - Opción 2: Deep Translator ⭐ *Recomendado*

2. **Proporciona tu archivo EPUB**
   - Escribe la ruta o arrastra el archivo

3. **Elige el idioma destino**
   - Códigos: `es`, `en`, `fr`, `de`, etc.
   - O nombres: `spanish`, `english`, `french`, etc.

4. **Selecciona dónde guardar**
   - Misma carpeta del original
   - O una carpeta personalizada

5. **¡Listo!** El programa traducirá todo el libro

## 🌐 Idiomas Soportados

<details>
<summary>Ver lista completa de idiomas</summary>

| Código | Idioma | Código | Idioma |
|--------|--------|--------|--------|
| `es` / `spanish` | Español | `en` / `english` | Inglés |
| `fr` / `french` | Francés | `de` / `german` | Alemán |
| `it` / `italian` | Italiano | `pt` / `portuguese` | Portugués |
| `ru` / `russian` | Ruso | `ja` / `japanese` | Japonés |
| `zh` / `chinese` | Chino | `ko` / `korean` | Coreano |
| `ar` / `arabic` | Árabe | ... | Y más de 90 idiomas adicionales |

</details>

## 📖 Ejemplo de Uso

```
> python traductor_epub.py

Selecciona el motor de traducción:
  1. Google Translate (googletrans)
  2. Deep Translator ⭐

Opción: 2

Ruta del archivo EPUB: mi_libro.epub

Código de idioma: english

¿Dónde guardar?
  1. Misma carpeta
  2. Otra carpeta

Opción: 2
Carpeta destino: C:\Mis Traducciones

Traduciendo...
✓ Traducción completada!
✓ Guardado en: C:\Mis Traducciones\mi_libro_traducido_english.epub
```

## 📚 Documentación

- [**QUICKSTART.md**](QUICKSTART.md) - Guía de inicio rápido
- [**CONTRIBUTING.md**](CONTRIBUTING.md) - Cómo contribuir
- [**CHANGELOG.md**](CHANGELOG.md) - Historial de cambios

## 🔧 Características Técnicas

- **Preservación total** de estructura HTML/XHTML
- **Mantiene CSS** y estilos originales
- **Conserva imágenes** y recursos multimedia
- **División automática** de textos largos
- **Control de velocidad** para evitar límites de API
- **UTF-8** configurado automáticamente en Windows
- **Validación** de traducciones antes de insertar

## ⚙️ Motores de Traducción

### Google Translate (googletrans)
- ✅ Muy rápido
- ✅ Códigos ISO estándar
- ⚠️ Puede tener conflictos con otras librerías

### Deep Translator ⭐ *Recomendado*
- ✅ Más estable
- ✅ Menos conflictos
- ✅ Acepta códigos y nombres completos

## 🐛 Solución de Problemas

### Error: "cannot insert None into a tag"
✅ **SOLUCIONADO** en v1.1.0 - El programa ahora maneja estos errores automáticamente.

### Error al leer EPUB
- Verifica que el archivo tenga extensión `.epub`
- Asegúrate de que no esté corrupto

### Traducción lenta
- Normal para libros grandes (100+ páginas)
- Las APIs gratuitas tienen límites de velocidad

## 🤝 Contribuir

¡Las contribuciones son bienvenidas! Por favor lee [CONTRIBUTING.md](CONTRIBUTING.md) para más detalles.

1. Fork el proyecto
2. Crea tu rama (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📝 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.

## 🌟 Apoya el Proyecto

Si este proyecto te fue útil, considera darle una ⭐ en GitHub!

## 📧 Contacto

¿Preguntas? Abre un [issue](https://github.com/tu-usuario/traductor-epub/issues) en GitHub.

---

<div align="center">

Hecho con ❤️ para la comunidad de lectura

[⬆ Volver arriba](#-traductor-de-libros-epub-)

</div>
