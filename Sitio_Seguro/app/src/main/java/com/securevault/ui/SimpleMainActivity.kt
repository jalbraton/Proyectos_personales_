package com.securevault.ui

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.View
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.securevault.R
import com.securevault.crypto.AESCrypto
import com.securevault.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.OutputStream

/**
 * Actividad principal simplificada
 * - Muestra lista de archivos encriptados
 * - Botón FAB para agregar archivos
 * - Click en archivo para ver/abrir
 */
class SimpleMainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var fabImport: FloatingActionButton
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var layoutEmpty: View
    
    // Carpeta ACCESIBLE desde el explorador de archivos
    private val encryptedDir: File by lazy {
        File(android.os.Environment.getExternalStoragePublicDirectory(
            android.os.Environment.DIRECTORY_DOWNLOADS
        ), "SitioSeguro").apply { mkdirs() }
    }
    
    private val crypto = AESCrypto()
    private var masterPassword: String? = null
    
    private val fileAdapter = EncryptedFilesAdapter(
        onFileClick = { file -> showFileOptions(file) },
        onFileDelete = { file -> deleteEncryptedFile(file) },
        onFileExport = { file -> exportEncryptedFile(file) }
    )
    
    // Selector de archivos para agregar
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                encryptAndAddFile(uri)
            }
        }
    }
    
    // Selector de archivos encriptados para importar
    private val importPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                importEncryptedFile(uri)
            }
        }
    }
    
    // Launcher para pedir permisos
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (!allGranted) {
            MaterialAlertDialogBuilder(this)
                .setTitle("Permisos necesarios")
                .setMessage("SitioSeguro necesita acceso al almacenamiento para:\n\n" +
                    "• Encriptar tus archivos\n" +
                    "• Guardar archivos encriptados\n" +
                    "• Abrir archivos desencriptados\n\n" +
                    "Sin estos permisos, la app no puede funcionar.")
                .setPositiveButton("Solicitar permisos") { _, _ ->
                    checkAndRequestPermissions()
                }
                .setNegativeButton("Salir") { _, _ ->
                    finish()
                }
                .setCancelable(false)
                .show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_main)
        
        initViews()
        setupSecureFolder()
        checkAndRequestPermissions()
        refreshFileList()
    }
    
    /**
     * Configura la carpeta segura con archivo .nomedia
     * Esto previene que aparezcan archivos temporales en la galería
     */
    private fun setupSecureFolder() {
        try {
            encryptedDir.mkdirs()
            
            // Crear archivo .nomedia para que Android no indexe esta carpeta
            val noMediaFile = File(encryptedDir, ".nomedia")
            if (!noMediaFile.exists()) {
                noMediaFile.createNewFile()
            }
            
            Logger.d("Carpeta segura configurada: ${encryptedDir.absolutePath}")
        } catch (e: Exception) {
            Logger.e("Error configurando carpeta segura", e)
        }
    }
    
    /**
     * Verifica y solicita permisos necesarios según la versión de Android
     */
    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf<String>()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ (API 33+)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) 
                != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) 
                != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_MEDIA_VIDEO)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) 
                != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_MEDIA_AUDIO)
            }
        } else {
            // Android 12 y anteriores
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) 
                != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) 
                    != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
        }
        
        if (permissions.isNotEmpty()) {
            permissionLauncher.launch(permissions.toTypedArray())
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Refrescar lista de archivos
        refreshFileList()
    }
    
    override fun onPause() {
        super.onPause()
        // Limpiar cache al salir
        cleanupAllTempFiles()
    }
    
    /**
     * Limpia archivo temporal cuando el usuario vuelve o sale
     * IMPORTANTE: Cierra el acceso al archivo desencriptado
     */
    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerEncryptedFiles)
        fabAdd = findViewById(R.id.fabAddFile)
        fabImport = findViewById(R.id.fabImportFile)
        progressBar = findViewById(R.id.progressBar)
        layoutEmpty = findViewById(R.id.layoutEmpty)
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = fileAdapter
        
        // Configurar toolbar con botón de cerrar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.inflateMenu(R.menu.menu_main)
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_exit -> {
                    closeAppSecurely()
                    true
                }
                else -> false
            }
        }
        
        fabAdd.setOnClickListener {
            pickFile()
        }
        
        fabImport.setOnClickListener {
            importFile()
        }
    }
    
    private fun pickFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        filePickerLauncher.launch(intent)
    }
    
    private fun showPasswordDialog(title: String, onPasswordEntered: (String) -> Unit) {
        val input = android.widget.EditText(this).apply {
            inputType = android.text.InputType.TYPE_CLASS_TEXT or 
                       android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            hint = "Contraseña"
        }
        
        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage("Introduce tu contraseña para continuar")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                val password = input.text.toString()
                if (password.length >= 8) {
                    onPasswordEntered(password)
                } else {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Error")
                        .setMessage("La contraseña debe tener al menos 8 caracteres")
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun encryptAndAddFile(uri: Uri) {
        // Primero pedir contraseña
        showPasswordDialog("Encriptar archivo") { password ->
            lifecycleScope.launch {
                progressBar.visibility = View.VISIBLE
                
                try {
                    // Obtener nombre del archivo
                    var fileName = "archivo_${System.currentTimeMillis()}"
                    contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (cursor.moveToFirst() && nameIndex >= 0) {
                            fileName = cursor.getString(nameIndex)
                        }
                    }
                    
                    // Leer archivo original
                    val originalData = withContext(Dispatchers.IO) {
                        contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    } ?: throw Exception("No se pudo leer el archivo")
                    
                    // Encriptar
                    val encryptedData = withContext(Dispatchers.IO) {
                        crypto.encrypt(originalData, password.toByteArray())
                    }
                    
                    // Guardar archivo encriptado
                    val encryptedFile = File(encryptedDir, "$fileName.enc")
                    withContext(Dispatchers.IO) {
                        encryptedFile.writeBytes(encryptedData)
                    }
                    
                    // Guardar metadata (nombre original)
                    val metaFile = File(encryptedDir, "$fileName.enc.meta")
                    withContext(Dispatchers.IO) {
                        metaFile.writeText(fileName)
                    }
                    
                    refreshFileList()
                    
                    MaterialAlertDialogBuilder(this@SimpleMainActivity)
                        .setTitle("Éxito")
                        .setMessage("Archivo encriptado: $fileName\n\nGuardado en: Downloads/SitioSeguro/")
                        .setPositiveButton("OK", null)
                        .show()
                        
                } catch (e: Exception) {
                    Logger.e("Error encriptando archivo", e)
                    MaterialAlertDialogBuilder(this@SimpleMainActivity)
                        .setTitle("Error")
                        .setMessage("No se pudo encriptar: ${e.message}")
                        .setPositiveButton("OK", null)
                        .show()
                } finally {
                    progressBar.visibility = View.GONE
                }
            }
        }
    }
    
    private fun deleteEncryptedFile(file: File) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Eliminar archivo")
            .setMessage("¿Eliminar ${file.name}? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                file.delete()
                File(encryptedDir, "${file.name}.meta").delete()
                refreshFileList()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun refreshFileList() {
        val files = encryptedDir.listFiles()?.filter { it.extension == "enc" } ?: emptyList()
        
        if (files.isEmpty()) {
            layoutEmpty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            layoutEmpty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            fileAdapter.submitList(files)
        }
    }
    
    // ========== NUEVAS FUNCIONES ==========
    
    private fun showFileOptions(file: File) {
        val options = arrayOf(
            "Desencriptar",
            "Exportar",
            "Eliminar"
        )
        
        MaterialAlertDialogBuilder(this)
            .setTitle(file.name.removeSuffix(".enc"))
            .setItems(options) { _, which ->
                when (which) {
                    0 -> decryptPermanently(file)
                    1 -> exportEncryptedFile(file)
                    2 -> deleteEncryptedFile(file)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun importFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            // Filtrar solo archivos .enc
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/octet-stream"))
        }
        
        // Mostrar mensaje de ayuda
        MaterialAlertDialogBuilder(this)
            .setTitle("Importar archivo encriptado")
            .setMessage("Selecciona un archivo con extensión .enc\n\n" +
                "Estos archivos fueron encriptados con SitioSeguro u otra herramienta compatible.")
            .setPositiveButton("Seleccionar") { _, _ ->
                importPickerLauncher.launch(intent)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun importEncryptedFile(uri: Uri) {
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            
            try {
                // Obtener nombre del archivo
                var fileName = "archivo_${System.currentTimeMillis()}.enc"
                contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (cursor.moveToFirst() && nameIndex >= 0) {
                        fileName = cursor.getString(nameIndex)
                    }
                }
                
                // Validar que sea un archivo .enc
                if (!fileName.endsWith(".enc", ignoreCase = true)) {
                    MaterialAlertDialogBuilder(this@SimpleMainActivity)
                        .setTitle("Archivo no válido")
                        .setMessage("Solo se pueden importar archivos con extensión .enc\n\n" +
                            "Archivo seleccionado: $fileName")
                        .setPositiveButton("OK", null)
                        .show()
                    progressBar.visibility = View.GONE
                    return@launch
                }
                
                // Copiar archivo encriptado
                val destFile = File(encryptedDir, fileName)
                withContext(Dispatchers.IO) {
                    contentResolver.openInputStream(uri)?.use { input ->
                        destFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }
                
                refreshFileList()
                
                MaterialAlertDialogBuilder(this@SimpleMainActivity)
                    .setTitle("Archivo importado")
                    .setMessage("Archivo importado: $fileName\n\nUsa tu contraseña para abrirlo.")
                    .setPositiveButton("OK", null)
                    .show()
                    
            } catch (e: Exception) {
                Logger.e("Error importando archivo", e)
                MaterialAlertDialogBuilder(this@SimpleMainActivity)
                    .setTitle("Error")
                    .setMessage("No se pudo importar: ${e.message}")
                    .setPositiveButton("OK", null)
                    .show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
    
    private fun exportEncryptedFile(file: File) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/octet-stream"
            val uri = androidx.core.content.FileProvider.getUriForFile(
                this@SimpleMainActivity,
                "${packageName}.fileprovider",
                file
            )
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        startActivity(Intent.createChooser(shareIntent, "Enviar archivo encriptado"))
    }
    
    /**
     * Desencripta un archivo permanentemente
     * Permite elegir la carpeta de destino
     */
    private fun decryptPermanently(file: File) {
        // Primero pedir contraseña
        showPasswordDialog("Desencriptar archivo") { password ->
            // Luego mostrar diálogo para elegir carpeta
            showFolderSelectionDialog(file, password)
        }
    }
    
    /**
     * Muestra diálogo para seleccionar carpeta de destino
     */
    private fun showFolderSelectionDialog(encFile: File, password: String) {
        val folders = arrayOf(
            "Descargas",
            "Documentos",
            "Imágenes",
            "Música",
            "Videos"
        )
        
        MaterialAlertDialogBuilder(this)
            .setTitle("¿Dónde guardar el archivo?")
            .setItems(folders) { _, which ->
                val destinationFolder = when (which) {
                    0 -> Environment.DIRECTORY_DOWNLOADS
                    1 -> Environment.DIRECTORY_DOCUMENTS
                    2 -> Environment.DIRECTORY_PICTURES
                    3 -> Environment.DIRECTORY_MUSIC
                    4 -> Environment.DIRECTORY_MOVIES
                    else -> Environment.DIRECTORY_DOWNLOADS
                }
                performDecryptToFolder(encFile, password, destinationFolder)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    /**
     * Realiza la desencriptación con MediaStore (compatible Android 10+)
     * Muestra barra de progreso
     */
    private fun performDecryptToFolder(encFile: File, password: String, destinationFolder: String) {
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            
            try {
                // Obtener nombre original
                val metaFile = File(encryptedDir, "${encFile.name}.meta")
                val originalName = if (metaFile.exists()) {
                    metaFile.readText()
                } else {
                    encFile.name.removeSuffix(".enc")
                }
                
                Logger.d("Desencriptando: $originalName a $destinationFolder")
                
                // Leer y desencriptar
                val encryptedData = withContext(Dispatchers.IO) {
                    encFile.readBytes()
                }
                
                val decryptedData = withContext(Dispatchers.IO) {
                    crypto.decrypt(encryptedData, password.toByteArray())
                }
                
                // Guardar usando MediaStore (compatible Android 10+)
                val mimeType = getMimeType(originalName) ?: "application/octet-stream"
                
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, originalName)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.MediaColumns.RELATIVE_PATH, destinationFolder)
                        put(MediaStore.MediaColumns.IS_PENDING, 1)
                    }
                }
                
                val collection = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                        when (destinationFolder) {
                            Environment.DIRECTORY_PICTURES -> MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                            Environment.DIRECTORY_MUSIC -> MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                            Environment.DIRECTORY_MOVIES -> MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                            Environment.DIRECTORY_DOWNLOADS -> MediaStore.Downloads.EXTERNAL_CONTENT_URI
                            else -> MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                        }
                    }
                    else -> {
                        // Android 9 y anteriores - usar Downloads
                        MediaStore.Downloads.EXTERNAL_CONTENT_URI
                    }
                }
                
                val uri = contentResolver.insert(collection, contentValues)
                    ?: throw Exception("No se pudo crear el archivo en $destinationFolder")
                
                // Escribir datos
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    withContext(Dispatchers.IO) {
                        outputStream.write(decryptedData)
                        outputStream.flush()
                    }
                }
                
                // Marcar como completado (Android 10+)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    contentResolver.update(uri, contentValues, null, null)
                }
                
                Logger.d("Archivo guardado exitosamente: $uri")
                
                // Mostrar confirmación
                MaterialAlertDialogBuilder(this@SimpleMainActivity)
                    .setTitle("Desencriptado exitosamente")
                    .setMessage("Archivo guardado en:\n$destinationFolder/$originalName\n\n¿Eliminar el archivo encriptado?")
                    .setPositiveButton("Sí, eliminar") { _, _ ->
                        deleteEncryptedFile(encFile)
                    }
                    .setNegativeButton("No, mantener", null)
                    .show()
                
            } catch (e: Exception) {
                Logger.e("Error desencriptando", e)
                MaterialAlertDialogBuilder(this@SimpleMainActivity)
                    .setTitle("Error al desencriptar")
                    .setMessage("No se pudo desencriptar:\n${e.message}\n\n¿Contraseña incorrecta?")
                    .setPositiveButton("OK", null)
                    .show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
    
    private fun closeAppSecurely() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Cerrar SitioSeguro")
            .setMessage("¿Cerrar la aplicación de forma segura?")
            .setPositiveButton("Cerrar") { _, _ ->
                // Limpiar contraseña de memoria
                masterPassword = null
                
                // Limpiar archivos temporales
                cleanupAllTempFiles()
                
                // Cerrar app
                finishAffinity()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    /**
     * Limpia TODOS los archivos temporales del cache
     */
    private fun cleanupAllTempFiles() {
        try {
            cacheDir.listFiles()?.forEach { file ->
                file.delete()
                Logger.d("Archivo temporal eliminado: ${file.name}")
            }
        } catch (e: Exception) {
            Logger.e("Error limpiando archivos temporales", e)
        }
    }
    
    /**
     * Detecta el tipo MIME correcto según la extensión del archivo
     */
    private fun getMimeType(fileName: String): String? {
        val extension = fileName.substringAfterLast('.', "").lowercase()
        
        // Tipos comunes de imágenes
        return when (extension) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "webp" -> "image/webp"
            "bmp" -> "image/bmp"
            
            // Videos
            "mp4" -> "video/mp4"
            "mkv" -> "video/x-matroska"
            "avi" -> "video/x-msvideo"
            "mov" -> "video/quicktime"
            "webm" -> "video/webm"
            
            // Audio
            "mp3" -> "audio/mpeg"
            "wav" -> "audio/wav"
            "ogg" -> "audio/ogg"
            "m4a" -> "audio/mp4"
            "flac" -> "audio/flac"
            
            // Documentos
            "pdf" -> "application/pdf"
            "doc" -> "application/msword"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "xls" -> "application/vnd.ms-excel"
            "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            "ppt" -> "application/vnd.ms-powerpoint"
            "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            "txt" -> "text/plain"
            
            // Archivos comprimidos
            "zip" -> "application/zip"
            "rar" -> "application/x-rar-compressed"
            "7z" -> "application/x-7z-compressed"
            
            else -> {
                // Usar MimeTypeMap de Android como fallback
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                    ?: "application/octet-stream"
            }
        }
    }
}