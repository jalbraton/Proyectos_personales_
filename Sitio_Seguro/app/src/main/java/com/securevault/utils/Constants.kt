package com.securevault.utils

/**
 * Constantes globales de la aplicación
 */
object Constants {
    // Criptografía
    const val AES_ALGORITHM = "AES"
    const val AES_MODE = "AES/XTS/NoPadding"
    const val AES_KEY_SIZE = 256 // bits
    const val AES_BLOCK_SIZE = 16 // bytes
    
    // Derivación de clave
    const val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA512"
    const val PBKDF2_ITERATIONS = 100_000
    const val PBKDF2_KEY_LENGTH = 512 // bits (dos claves de 256 bits para XTS)
    const val SALT_SIZE = 32 // bytes
    
    // Header del volumen
    const val VOLUME_HEADER_SIZE = 512 // bytes
    const val VOLUME_MAGIC = "SVLT" // SecureVault
    const val VOLUME_VERSION = 1
    
    // Tamaños de volumen
    const val MIN_VOLUME_SIZE = 1024L * 1024 // 1 MB
    const val MAX_VOLUME_SIZE = 10L * 1024 * 1024 * 1024 // 10 GB
    const val DEFAULT_VOLUME_SIZE = 100L * 1024 * 1024 // 100 MB
    
    // Seguridad
    const val MIN_PASSWORD_LENGTH = 12
    const val SESSION_TIMEOUT_MS = 5 * 60 * 1000L // 5 minutos
    const val MEMORY_CLEAR_DELAY_MS = 100L
    
    // Archivos
    const val VOLUME_EXTENSION = ".svlt"
    const val TEMP_PREFIX = "svlt_temp_"
    const val BUFFER_SIZE = 8192 // bytes
    
    // UI
    const val ANIMATION_DURATION = 300L
    const val TOAST_DURATION_SHORT = 2000L
    
    // SharedPreferences
    const val PREFS_NAME = "SecureVaultPrefs"
    const val PREF_SESSION_TIMEOUT = "session_timeout"
    const val PREF_PROTECT_SCREEN = "protect_screen"
    const val PREF_AUTO_LOCK = "auto_lock"
    
    // Request codes
    const val REQUEST_PICK_FILE = 1001
    const val REQUEST_CREATE_VOLUME = 1002
    const val REQUEST_STORAGE_PERMISSION = 1003
}
