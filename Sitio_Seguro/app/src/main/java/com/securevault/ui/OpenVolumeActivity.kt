package com.securevault.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.securevault.R
import com.securevault.security.SessionManager
import com.securevault.utils.Logger
import com.securevault.volume.VolumeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Actividad para abrir volúmenes existentes
 */
class OpenVolumeActivity : AppCompatActivity() {
    
    private lateinit var tilPassword: TextInputLayout
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnSelectVolume: MaterialButton
    private lateinit var btnOpen: MaterialButton
    private lateinit var layoutProgress: LinearLayout
    
    private lateinit var sessionManager: SessionManager
    private var selectedVolumePath: String? = null
    
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                // En una implementación completa, deberías copiar el archivo seleccionado
                // Para simplicidad, asumimos que el usuario selecciona desde el directorio de la app
                val path = uri.path ?: return@let
                selectedVolumePath = path
                Toast.makeText(this, "Volumen seleccionado", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_volume) // ✅ FIX: Layout propio
        
        sessionManager = SessionManager.getInstance()
        
        if (sessionManager.shouldProtectScreen()) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
        
        initViews()
        setupUI()
    }
    
    private fun initViews() {
        tilPassword = findViewById(R.id.tilPassword)
        etPassword = findViewById(R.id.etPassword)
        btnOpen = findViewById(R.id.btnOpen)
        layoutProgress = findViewById(R.id.layoutProgress)
    }
    
    private fun setupUI() {
        // No necesita configuración adicional - el botón ya tiene el ID correcto
        btnOpen.setOnClickListener {
            openVolume()
        }
    }
    
    private fun openVolume() {
        // Para simplificar, buscaremos volúmenes en el directorio de la app
        val volumesDir = getExternalFilesDir("volumes")
        val volumes = volumesDir?.listFiles()?.filter { it.extension == "svlt" }
        
        if (volumes.isNullOrEmpty()) {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.error)
                .setMessage("No se encontraron volúmenes. Crea uno primero.")
                .setPositiveButton(R.string.ok, null)
                .show()
            return
        }
        
        // Mostrar lista de volúmenes
        val volumeNames = volumes.map { it.name }.toTypedArray()
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.select_volume)
            .setItems(volumeNames) { _, which ->
                selectedVolumePath = volumes[which].absolutePath
                proceedToOpen()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    private fun proceedToOpen() {
        val volumePath = selectedVolumePath
        if (volumePath == null) {
            Toast.makeText(this, "Selecciona un volumen primero", Toast.LENGTH_SHORT).show()
            return
        }
        
        val password = etPassword.text?.toString() ?: ""
        if (password.isEmpty()) {
            tilPassword.error = getString(R.string.error_empty_field)
            return
        }
        tilPassword.error = null
        
        btnOpen.isEnabled = false
        layoutProgress.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    VolumeManager.openVolume(volumePath, password.toCharArray())
                }
                
                if (result.isSuccess) {
                    // Navegar al explorador de volumen
                    val intent = Intent(this@OpenVolumeActivity, VolumeExplorerActivity::class.java)
                    intent.putExtra("volume_path", volumePath)
                    startActivity(intent)
                    finish()
                } else {
                    showError(result.exceptionOrNull()?.message ?: getString(R.string.error_open_volume))
                }
            } catch (e: Exception) {
                Logger.e("Failed to open volume", e)
                showError(e.message ?: getString(R.string.error_open_volume))
            } finally {
                btnOpen.isEnabled = true
                layoutProgress.visibility = View.GONE
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
}
