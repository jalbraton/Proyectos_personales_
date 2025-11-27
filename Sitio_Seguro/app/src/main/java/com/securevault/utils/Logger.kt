package com.securevault.utils

import android.util.Log

/**
 * Logger centralizado para la aplicación
 */
object Logger {
    private const val TAG = "SecureVault"
    private var isDebugEnabled = true
    
    fun setDebugEnabled(enabled: Boolean) {
        isDebugEnabled = enabled
    }
    
    fun d(message: String, tag: String = TAG) {
        if (isDebugEnabled) {
            Log.d(tag, message)
        }
    }
    
    fun i(message: String, tag: String = TAG) {
        Log.i(tag, message)
    }
    
    fun w(message: String, throwable: Throwable? = null, tag: String = TAG) {
        if (throwable != null) {
            Log.w(tag, message, throwable)
        } else {
            Log.w(tag, message)
        }
    }
    
    fun e(message: String, throwable: Throwable? = null, tag: String = TAG) {
        if (throwable != null) {
            Log.e(tag, message, throwable)
        } else {
            Log.e(tag, message)
        }
    }
    
    fun security(message: String) {
        Log.i("$TAG-Security", message)
    }
    
    fun crypto(message: String) {
        if (isDebugEnabled) {
            Log.d("$TAG-Crypto", message)
        }
    }
}
