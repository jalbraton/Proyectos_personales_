package com.securevault.security

import com.securevault.utils.Constants
import com.securevault.utils.Logger
import com.securevault.utils.clear
import kotlinx.coroutines.*

/**
 * Gestiona la limpieza segura de datos sensibles en memoria
 */
object SecureMemory {
    
    private val cleanupScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    /**
     * Limpia un ByteArray de forma segura después de un delay
     */
    fun clearDelayed(data: ByteArray, delayMs: Long = Constants.MEMORY_CLEAR_DELAY_MS) {
        cleanupScope.launch {
            delay(delayMs)
            data.clear()
            Logger.crypto("Memory cleared (ByteArray)")
        }
    }
    
    /**
     * Limpia un CharArray de forma segura después de un delay
     */
    fun clearDelayed(data: CharArray, delayMs: Long = Constants.MEMORY_CLEAR_DELAY_MS) {
        cleanupScope.launch {
            delay(delayMs)
            data.clear()
            Logger.crypto("Memory cleared (CharArray)")
        }
    }
    
    /**
     * Limpia inmediatamente un ByteArray
     */
    fun clearNow(data: ByteArray) {
        data.clear()
        Logger.crypto("Memory cleared immediately (ByteArray)")
    }
    
    /**
     * Limpia inmediatamente un CharArray
     */
    fun clearNow(data: CharArray) {
        data.clear()
        Logger.crypto("Memory cleared immediately (CharArray)")
    }
    
    /**
     * Limpia múltiples arrays
     */
    fun clearAll(vararg arrays: Any) {
        for (array in arrays) {
            when (array) {
                is ByteArray -> array.clear()
                is CharArray -> array.clear()
                else -> Logger.w("Unsupported type for clearing: ${array::class.simpleName}")
            }
        }
        Logger.crypto("Multiple arrays cleared")
    }
    
    /**
     * Solicita recolección de basura (no garantizado)
     */
    fun requestGarbageCollection() {
        System.gc()
        Logger.crypto("Garbage collection requested")
    }
}
