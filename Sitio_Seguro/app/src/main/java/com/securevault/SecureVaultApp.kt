package com.securevault

import android.app.Application
import com.securevault.security.SessionManager
import com.securevault.utils.Logger
import com.securevault.volume.VolumeManager

/**
 * Clase de aplicación principal
 */
class SecureVaultApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        Logger.i("SecureVault starting...")
        
        // Inicializa el gestor de sesiones
        SessionManager.initialize(this)
        
        Logger.i("SecureVault initialized")
    }
    
    override fun onTerminate() {
        super.onTerminate()
        
        // Cierra todos los volúmenes al terminar
        VolumeManager.closeAllVolumes()
        
        Logger.i("SecureVault terminated")
    }
    
    override fun onLowMemory() {
        super.onLowMemory()
        Logger.w("Low memory warning")
    }
}
