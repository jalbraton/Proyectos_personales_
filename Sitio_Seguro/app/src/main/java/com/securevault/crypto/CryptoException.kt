package com.securevault.crypto

/**
 * Excepción personalizada para errores de criptografía
 */
class CryptoException(message: String, cause: Throwable? = null) : Exception(message, cause)
