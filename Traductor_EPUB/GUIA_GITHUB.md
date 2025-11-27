# 🚀 Guía para Subir el Proyecto a GitHub

Este documento te guiará para subir el Traductor de EPUB a GitHub.

## 📋 Prerequisitos

1. **Cuenta de GitHub**: Crea una cuenta en [github.com](https://github.com) si no tienes una
2. **Git instalado**: Descarga desde [git-scm.com](https://git-scm.com/)

## 🎯 Pasos para Subir a GitHub

### Opción 1: Usando GitHub Desktop (Más Fácil)

1. **Descarga GitHub Desktop**
   - [desktop.github.com](https://desktop.github.com/)

2. **Crea un nuevo repositorio**
   - File → New Repository
   - Name: `traductor-epub`
   - Description: `Traductor de libros EPUB a múltiples idiomas`
   - Local path: Selecciona la carpeta `traductor-epub-github`

3. **Publica en GitHub**
   - Publish repository
   - Mantén "Keep this code private" desmarcado para hacerlo público
   - Publish!

### Opción 2: Usando Línea de Comandos

```bash
# 1. Abre terminal en la carpeta del proyecto
cd traductor-epub-github

# 2. Inicializa el repositorio Git
git init

# 3. Agrega todos los archivos
git add .

# 4. Primer commit
git commit -m "Primera versión del traductor de EPUB"

# 5. Crea el repositorio en GitHub.com
# Ve a github.com → New Repository
# Nombre: traductor-epub
# NO inicialices con README (ya lo tenemos)

# 6. Conecta con GitHub (reemplaza TU-USUARIO)
git remote add origin https://github.com/TU-USUARIO/traductor-epub.git

# 7. Sube el código
git branch -M main
git push -u origin main
```

## ✅ Verificación

Después de subir, verifica que aparezcan estos archivos en GitHub:

```
✓ README.md (con badges y formato profesional)
✓ requirements.txt
✓ traductor_epub.py
✓ LICENSE
✓ .gitignore
✓ CHANGELOG.md
✓ CONTRIBUTING.md
✓ QUICKSTART.md
✓ instalar.bat / instalar.sh
✓ ejecutar.bat / ejecutar.sh
✓ ejemplos/crear_epub_prueba.py
```

## 🎨 Personalización del README

Antes de subir, edita `README.md` y reemplaza:

- `tu-usuario` con tu nombre de usuario de GitHub
- Agrega tu información de contacto si deseas
- Personaliza la sección de licencia si es necesario

## 📝 Después de Subir

### 1. Configura el Repositorio

En GitHub, ve a Settings y:
- Agrega una descripción corta
- Agrega topics: `python`, `epub`, `translator`, `ebook`, `translation`
- Activa Issues y Discussions si quieres colaboración

### 2. Crea un Release

1. Ve a Releases → Create a new release
2. Tag: `v1.1.0`
3. Title: `Versión 1.1.0 - Mejoras en Manejo de Errores`
4. Descripción: Copia el contenido de CHANGELOG.md
5. Publish release

### 3. Comparte tu Proyecto

- Comparte el link en redes sociales
- Publica en Reddit (r/Python, r/learnpython)
- Comparte en foros de programación

## 🔄 Actualizaciones Futuras

Para subir cambios futuros:

```bash
# 1. Agrega los cambios
git add .

# 2. Commit con mensaje descriptivo
git commit -m "Descripción de los cambios"

# 3. Sube a GitHub
git push
```

## 📊 Estadísticas

Una vez en GitHub, tu proyecto mostrará:
- ⭐ Estrellas (usuarios que les gusta tu proyecto)
- 🍴 Forks (personas que han copiado tu proyecto)
- 👀 Watchers (personas siguiendo las actualizaciones)

## 🎯 Tips para Conseguir Más Visibilidad

1. **README atractivo**: Ya lo tienes con badges y emojis ✅
2. **Documentación clara**: QUICKSTART.md y ejemplos ✅
3. **Licencia clara**: MIT License ✅
4. **Contribuciones**: CONTRIBUTING.md ✅
5. **Tags descriptivos**: epub, translator, python, ebook
6. **Screenshots**: Considera agregar capturas de pantalla
7. **Demo**: Crea un video corto mostrando cómo funciona

## ❓ Problemas Comunes

### "Permission denied (publickey)"
```bash
# Usa HTTPS en lugar de SSH
git remote set-url origin https://github.com/TU-USUARIO/traductor-epub.git
```

### "Updates were rejected"
```bash
# Primero descarga los cambios
git pull origin main --rebase
git push
```

### Archivo muy grande
Los archivos EPUB de prueba no deberían subirse (están en .gitignore)

## 🎉 ¡Listo!

Tu proyecto ahora está en GitHub y disponible para el mundo.

**URL de ejemplo:** `https://github.com/TU-USUARIO/traductor-epub`

---

¿Necesitas ayuda? Consulta la [documentación de GitHub](https://docs.github.com/)
