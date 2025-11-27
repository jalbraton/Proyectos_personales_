package com.securevault.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.securevault.R
import com.securevault.security.SessionManager
import com.securevault.utils.Constants
import com.securevault.utils.Logger
import com.securevault.utils.formatAsFileSize
import com.securevault.utils.isValidPassword
import com.securevault.utils.passwordStrength
import com.securevault.volume.VolumeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Actividad para crear nuevos volúmenes encriptados
 */
class CreateVolumeActivity : AppCompatActivity() {
    
    private lateinit var tilVolumeName: TextInputLayout
    private lateinit var etVolumeName: TextInputEditText
    private lateinit var tilVolumeSize: TextInputLayout
    private lateinit var etVolumeSize: TextInputEditText
    private lateinit var tilPassword: TextInputLayout
    private lateinit var etPassword: TextInputEditText
    private lateinit var tilConfirmPassword: TextInputLayout
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var tvPasswordStrength: TextView
    private lateinit var progressPasswordStrength: ProgressBar
    private lateinit var btnCreate: MaterialButton
    private lateinit var layoutProgress: LinearLayout
    
    private lateinit var sessionManager: SessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_volume)
        
        sessionManager = SessionManager.getInstance()
        
        if (sessionManager.shouldProtectScreen()) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
        
        initViews()
        setupPasswordStrengthIndicator()
        setupCreateButton()
    }
    
    private fun initViews() {
        tilVolumeName = findViewById(R.id.tilVolumeName)
        etVolumeName = findViewById(R.id.etVolumeName)
        tilVolumeSize = findViewById(R.id.tilVolumeSize)
        etVolumeSize = findViewById(R.id.etVolumeSize)
        tilPassword = findViewById(R.id.tilPassword)
        etPassword = findViewById(R.id.etPassword)
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        tvPasswordStrength = findViewById(R.id.tvPasswordStrength)
        progressPasswordStrength = findViewById(R.id.progressPasswordStrength)
        btnCreate = findViewById(R.id.btnCreate)
        layoutProgress = findViewById(R.id.layoutProgress)
    }
    
    private fun setupPasswordStrengthIndicator() {
        etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                val password = s?.toString() ?: ""
                updatePasswordStrength(password)
            }
        })
    }
    
    private fun updatePasswordStrength(password: String) {
        val strength = password.passwordStrength()
        progressPasswordStrength.progress = strength
        
        val (text, color) = when {
            strength < 25 -> Pair(getString(R.string.strength_weak), R.color.strength_weak)
            strength < 50 -> Pair(getString(R.string.strength_medium), R.color.strength_medium)
            strength < 75 -> Pair(getString(R.string.strength_strong), R.color.strength_strong)
            else -> Pair(getString(R.string.strength_very_strong), R.color.strength_very_strong)
        }
        
        tvPasswordStrength.text = text
        tvPasswordStrength.setTextColor(getColor(color))
        progressPasswordStrength.progressTintList = getColorStateList(color)
    }
    
    private fun setupCreateButton() {
        btnCreate.setOnClickListener {
            createVolume()
        }
    }
    
    private fun createVolume() {
        // Validaciones
        val volumeName = etVolumeName.text?.toString()?.trim() ?: ""
        if (volumeName.isEmpty()) {
            tilVolumeName.error = getString(R.string.error_empty_field)
            return
        }
        tilVolumeName.error = null
        
        val sizeStr = etVolumeSize.text?.toString() ?: ""
        val sizeMB = sizeStr.toLongOrNull()
        if (sizeMB == null || sizeMB < 1 || sizeMB > 10240) {
            tilVolumeSize.error = getString(R.string.error_invalid_size)
            return
        }
        tilVolumeSize.error = null
        
        val password = etPassword.text?.toString() ?: ""
        if (!password.isValidPassword()) {
            tilPassword.error = getString(R.string.error_password_weak)
            return
        }
        tilPassword.error = null
        
        val confirmPassword = etConfirmPassword.text?.toString() ?: ""
        if (password != confirmPassword) {
            tilConfirmPassword.error = getString(R.string.error_password_mismatch)
            return
        }
        tilConfirmPassword.error = null
        
        // Crear volumen
        val volumePath = getVolumeFilePath(volumeName)
        val volumeSize = sizeMB * 1024 * 1024 // Convertir a bytes
        
        btnCreate.isEnabled = false
        layoutProgress.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    VolumeManager.createVolume(
                        volumePath,
                        password.toCharArray(),
                        volumeSize
                    )
                }
                
                if (result.isSuccess) {
                    MaterialAlertDialogBuilder(this@CreateVolumeActivity)
                        .setTitle(R.string.success)
                        .setMessage(getString(R.string.volume_created))
                        .setPositiveButton(R.string.ok) { _, _ ->
                            finish()
                        }
                        .show()
                } else {
                    showError(result.exceptionOrNull()?.message ?: getString(R.string.error_create_volume))
                }
            } catch (e: Exception) {
                Logger.e("Failed to create volume", e)
                showError(e.message ?: getString(R.string.error_create_volume))
            } finally {
                btnCreate.isEnabled = true
                layoutProgress.visibility = View.GONE
            }
        }
    }
    
    private fun getVolumeFilePath(volumeName: String): String {
        val fileName = if (volumeName.endsWith(Constants.VOLUME_EXTENSION)) {
            volumeName
        } else {
            "$volumeName${Constants.VOLUME_EXTENSION}"
        }
        
        val volumesDir = File(getExternalFilesDir(null), "volumes")
        volumesDir.mkdirs()
        
        return File(volumesDir, fileName).absolutePath
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
