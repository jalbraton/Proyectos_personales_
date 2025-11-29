package com.securevault.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.securevault.R
import com.securevault.security.SessionManager
import com.securevault.storage.FileEntry
import com.securevault.storage.VolumeFileSystem
import com.securevault.utils.Logger
import com.securevault.utils.formatAsFileSize
import com.securevault.volume.VolumeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Actividad para explorar y gestionar archivos dentro del volumen
 */
class VolumeExplorerActivity : AppCompatActivity() {
    
    private lateinit var toolbar: MaterialToolbar
    private lateinit var recyclerFiles: RecyclerView
    private lateinit var layoutEmpty: View
    private lateinit var tvFileCount: TextView
    private lateinit var tvSpaceInfo: TextView
    private lateinit var progressSpaceUsed: ProgressBar
    private lateinit var btnAddFile: MaterialButton
    private lateinit var btnCloseVolume: MaterialButton
    
    private lateinit var sessionManager: SessionManager
    private lateinit var filesAdapter: FilesAdapter
    private var volumePath: String? = null
    private var fileSystem: VolumeFileSystem? = null
    
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                addFileToVolume(uri)
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volume_explorer)
        
        sessionManager = SessionManager.getInstance()
        
        if (sessionManager.shouldProtectScreen()) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
        
        volumePath = intent.getStringExtra("volume_path")
        if (volumePath == null) {
            finish()
            return
        }
        
        initViews()
        setupUI()
        loadVolume()
    }
    
    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        recyclerFiles = findViewById(R.id.recyclerFiles)
        layoutEmpty = findViewById(R.id.layoutEmpty)
        tvFileCount = findViewById(R.id.tvFileCount)
        tvSpaceInfo = findViewById(R.id.tvSpaceInfo)
        progressSpaceUsed = findViewById(R.id.progressSpaceUsed)
        btnAddFile = findViewById(R.id.btnAddFile)
        btnCloseVolume = findViewById(R.id.btnCloseVolume)
    }
    
    private fun setupUI() {
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        
        filesAdapter = FilesAdapter(
            onExtractClick = { file -> extractFile(file) },
            onDeleteClick = { file -> confirmDeleteFile(file) }
        )
        
        recyclerFiles.layoutManager = LinearLayoutManager(this)
        recyclerFiles.adapter = filesAdapter
        
        btnAddFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            filePickerLauncher.launch(intent)
        }
        
        btnCloseVolume.setOnClickListener {
            confirmCloseVolume()
        }
    }
    
    private fun loadVolume() {
        lifecycleScope.launch {
            try {
                val volume = withContext(Dispatchers.IO) {
                    VolumeManager.getVolume(volumePath!!)
                }
                
                if (volume != null) {
                    fileSystem = VolumeFileSystem(volume)
                    refreshFileList()
                } else {
                    showError("No se pudo cargar el volumen")
                    finish()
                }
            } catch (e: Exception) {
                Logger.e("Failed to load volume", e)
                showError(e.message ?: "Error al cargar volumen")
                finish()
            }
        }
    }
    
    private fun refreshFileList() {
        val files = fileSystem?.listFiles() ?: emptyList()
        
        if (files.isEmpty()) {
            layoutEmpty.visibility = View.VISIBLE
            recyclerFiles.visibility = View.GONE
        } else {
            layoutEmpty.visibility = View.GONE
            recyclerFiles.visibility = View.VISIBLE
            filesAdapter.submitList(files)
        }
        
        tvFileCount.text = getString(R.string.file_count, files.size)
        updateSpaceInfo()
    }
    
    private fun updateSpaceInfo() {
        fileSystem?.let { fs ->
            val used = fs.getUsedSpace()
            val total = fs.getUsedSpace() + fs.getFreeSpace()
            val percentage = if (total > 0) ((used * 100) / total).toInt() else 0
            
            progressSpaceUsed.progress = percentage
            tvSpaceInfo.text = "Usado: ${used.formatAsFileSize()} / ${total.formatAsFileSize()}"
        }
    }
    
    private fun addFileToVolume(uri: Uri) {
        lifecycleScope.launch {
            try {
                // Obtener nombre del archivo
                var fileName = "archivo"
                contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (cursor.moveToFirst() && nameIndex >= 0) {
                        fileName = cursor.getString(nameIndex)
                    }
                }
                
                // Crear archivo temporal
                val tempFile = File(cacheDir, fileName)
                contentResolver.openInputStream(uri)?.use { input ->
                    tempFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                
                // Agregar al volumen
                val result = withContext(Dispatchers.IO) {
                    fileSystem?.addFile(tempFile.absolutePath, fileName)
                }
                
                // Limpiar archivo temporal
                tempFile.delete()
                
                if (result?.isSuccess == true) {
                    refreshFileList()
                    MaterialAlertDialogBuilder(this@VolumeExplorerActivity)
                        .setTitle(R.string.success)
                        .setMessage("Archivo agregado: $fileName")
                        .setPositiveButton(R.string.ok, null)
                        .show()
                } else {
                    showError(getString(R.string.error_add_file))
                }
            } catch (e: Exception) {
                Logger.e("Failed to add file", e)
                showError(e.message ?: getString(R.string.error_add_file))
            }
        }
    }
    
    private fun extractFile(file: FileEntry) {
        val destDir = File(getExternalFilesDir(null), "extracted")
        destDir.mkdirs()
        val destPath = File(destDir, file.name).absolutePath
        
        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    fileSystem?.extractFile(file.name, destPath)
                }
                
                if (result?.isSuccess == true) {
                    MaterialAlertDialogBuilder(this@VolumeExplorerActivity)
                        .setTitle(R.string.success)
                        .setMessage("Archivo extraído a: $destPath")
                        .setPositiveButton(R.string.ok, null)
                        .show()
                } else {
                    showError(getString(R.string.error_extract_file))
                }
            } catch (e: Exception) {
                Logger.e("Failed to extract file", e)
                showError(e.message ?: getString(R.string.error_extract_file))
            }
        }
    }
    
    private fun confirmDeleteFile(file: FileEntry) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.confirm)
            .setMessage(getString(R.string.delete_confirmation))
            .setPositiveButton(R.string.yes) { _, _ ->
                deleteFile(file)
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }
    
    private fun deleteFile(file: FileEntry) {
        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    fileSystem?.deleteFile(file.name)
                }
                
                if (result?.isSuccess == true) {
                    refreshFileList()
                } else {
                    showError(getString(R.string.error_delete_file))
                }
            } catch (e: Exception) {
                Logger.e("Failed to delete file", e)
                showError(e.message ?: getString(R.string.error_delete_file))
            }
        }
    }
    
    private fun confirmCloseVolume() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.confirm)
            .setMessage(getString(R.string.close_volume_confirmation))
            .setPositiveButton(R.string.yes) { _, _ ->
                closeVolume()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }
    
    private fun closeVolume() {
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    VolumeManager.closeVolume(volumePath!!)
                }
                finish()
            } catch (e: Exception) {
                Logger.e("Failed to close volume", e)
                showError(e.message ?: "Error al cerrar volumen")
            }
        }
    }
    
    private fun showError(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.error)
            .setMessage(message)
            .setPositiveButton(R.string.ok, null)
            .show()
    }
    
    override fun onResume() {
        super.onResume()
        sessionManager.updateActivity()
    }
    
    override fun onBackPressed() {
        confirmCloseVolume()
    }
}
