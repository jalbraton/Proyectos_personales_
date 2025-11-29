package com.securevault.security

import android.content.Context
import android.content.SharedPreferences
import com.securevault.utils.Constants
import com.securevault.utils.Logger
import com.securevault.volume.VolumeManager

/**
 * Gestiona las sesiones de usuario y el timeout automático
 */
class SessionManager private constructor(private val context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        Constants.PREFS_NAME,
        Context.MODE_PRIVATE
    )
    
    private var lastActivityTime: Long = 0
    private var isSessionActive = false
    
    companion object {
        @Volatile
        private var instance: SessionManager? = null
        
        fun initialize(context: Context) {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = SessionManager(context.applicationContext)
                    }
                }
            }
        }
        
        fun getInstance(): SessionManager {
            return instance ?: throw IllegalStateException("SessionManager not initialized")
        }
    }
    
    /**
     * Inicia una nueva sesión
     */
    fun startSession() {
        isSessionActive = true
        updateActivity()
        Logger.security("Session started")
    }
    
    /**
     * Finaliza la sesión actual
     */
    fun endSession() {
        if (!isSessionActive) return
        
        Logger.security("Ending session")
        
        // Cierra todos los volúmenes
        VolumeManager.closeAllVolumes()
        
        isSessionActive = false
        lastActivityTime = 0
        
        Logger.security("Session ended")
    }
    
    /**
     * Actualiza el tiempo de última actividad
     */
    fun updateActivity() {
        lastActivityTime = System.currentTimeMillis()
    }
    
    /**
     * Verifica si la sesión ha expirado
     */
    fun checkSessionTimeout(): Boolean {
        if (!isSessionActive) return false
        
        val timeout = getSessionTimeout()
        val elapsed = System.currentTimeMillis() - lastActivityTime
        
        if (elapsed > timeout) {
            Logger.security("Session timeout")
            endSession()
            return true
        }
        
        return false
    }
    
    /**
     * Obtiene el timeout de sesión configurado
     */
    fun getSessionTimeout(): Long {
        return prefs.getLong(Constants.PREF_SESSION_TIMEOUT, Constants.SESSION_TIMEOUT_MS)
    }
    
    /**
     * Establece el timeout de sesión
     */
    fun setSessionTimeout(timeoutMs: Long) {
        prefs.edit().putLong(Constants.PREF_SESSION_TIMEOUT, timeoutMs).apply()
    }
    
    /**
     * Verifica si debe proteger la pantalla contra screenshots
     */
    fun shouldProtectScreen(): Boolean {
        return prefs.getBoolean(Constants.PREF_PROTECT_SCREEN, true)
    }
    
    /**
     * Establece la protección de pantalla
     */
    fun setProtectScreen(protect: Boolean) {
        prefs.edit().putBoolean(Constants.PREF_PROTECT_SCREEN, protect).apply()
    }
    
    /**
     * Verifica si está activo el auto-lock
     */
    fun isAutoLockEnabled(): Boolean {
        return prefs.getBoolean(Constants.PREF_AUTO_LOCK, true)
    }
    
    /**
     * Establece el auto-lock
     */
    fun setAutoLock(enabled: Boolean) {
        prefs.edit().putBoolean(Constants.PREF_AUTO_LOCK, enabled).apply()
    }
    
    fun isActive(): Boolean = isSessionActive
}
