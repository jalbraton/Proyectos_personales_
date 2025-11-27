package com.securevault.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.securevault.R
import com.securevault.security.SessionManager
import com.securevault.utils.Constants
import com.securevault.utils.Logger

/**
 * Pantalla principal de la aplicación
 * Interfaz limpia estilo Mint Linux
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var sessionManager: SessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        sessionManager = SessionManager.getInstance()
        
        // Protección de pantalla contra screenshots
        if (sessionManager.shouldProtectScreen()) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
        
        checkPermissions()
        setupUI()
    }
    
    private fun setupUI() {
        findViewById<MaterialButton>(R.id.btnEncrypt).setOnClickListener {
            // TODO: Implement encryption functionality
            Logger.d("Encrypt button clicked")
        }
        
        findViewById<MaterialButton>(R.id.btnDecrypt).setOnClickListener {
            // TODO: Implement decryption functionality
            Logger.d("Decrypt button clicked")
        }
    }
    
    private fun checkPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO
            )
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        
        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        
        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                missingPermissions.toTypedArray(),
                Constants.REQUEST_STORAGE_PERMISSION
            )
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == Constants.REQUEST_STORAGE_PERMISSION) {
            if (grantResults.any { it != PackageManager.PERMISSION_GRANTED }) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.permission_required)
                    .setMessage(R.string.permission_storage)
                    .setPositiveButton(R.string.ok, null)
                    .show()
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        sessionManager.updateActivity()
        
        if (sessionManager.checkSessionTimeout()) {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.session_expired)
                .setMessage(R.string.session_expired_message)
                .setPositiveButton(R.string.ok, null)
                .show()
        }
    }
}
