package com.securevault.crypto

import com.securevault.utils.Constants
import com.securevault.utils.Logger
import java.security.MessageDigest

/**
 * Utilidades criptográficas generales
 */
object CryptoUtils {
    
    /**
     * Calcula el hash SHA-256 de datos
     */
    fun sha256(data: ByteArray): ByteArray {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            digest.digest(data)
        } catch (e: Exception) {
            Logger.e("SHA-256 hash failed", e)
            throw CryptoException("Failed to compute SHA-256", e)
        }
    }
    
    /**
     * Calcula el hash SHA-512 de datos
     */
    fun sha512(data: ByteArray): ByteArray {
        return try {
            val digest = MessageDigest.getInstance("SHA-512")
            digest.digest(data)
        } catch (e: Exception) {
            Logger.e("SHA-512 hash failed", e)
            throw CryptoException("Failed to compute SHA-512", e)
        }
    }
    
    /**
     * Compara dos arrays de bytes de forma segura (constant-time)
     * Previene timing attacks
     */
    fun constantTimeEquals(a: ByteArray, b: ByteArray): Boolean {
        if (a.size != b.size) return false
        
        var result = 0
        for (i in a.indices) {
            result = result or (a[i].toInt() xor b[i].toInt())
        }
        
        return result == 0
    }
    
    /**
     * Aplica padding PKCS7 a datos
     */
    fun applyPadding(data: ByteArray, blockSize: Int = Constants.AES_BLOCK_SIZE): ByteArray {
        val paddingSize = blockSize - (data.size % blockSize)
        val padded = ByteArray(data.size + paddingSize)
        
        data.copyInto(padded)
        
        // PKCS7: cada byte de padding contiene el tamaño del padding
        for (i in data.size until padded.size) {
            padded[i] = paddingSize.toByte()
        }
        
        return padded
    }
    
    /**
     * Remueve padding PKCS7 de datos
     */
    fun removePadding(data: ByteArray, blockSize: Int = Constants.AES_BLOCK_SIZE): ByteArray {
        if (data.isEmpty()) return data
        
        val paddingSize = data.last().toInt()
        
        // Valida el padding
        if (paddingSize < 1 || paddingSize > blockSize) {
            throw CryptoException("Invalid padding")
        }
        
        for (i in data.size - paddingSize until data.size) {
            if (data[i].toInt() != paddingSize) {
                throw CryptoException("Invalid padding bytes")
            }
        }
        
        return data.copyOfRange(0, data.size - paddingSize)
    }
    
    /**
     * Convierte ByteArray a string hexadecimal
     */
    fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        for (i in bytes.indices) {
            val v = bytes[i].toInt() and 0xFF
            hexChars[i * 2] = "0123456789ABCDEF"[v ushr 4]
            hexChars[i * 2 + 1] = "0123456789ABCDEF"[v and 0x0F]
        }
        return String(hexChars)
    }
    
    /**
     * Convierte string hexadecimal a ByteArray
     */
    fun hexToBytes(hex: String): ByteArray {
        val cleanHex = hex.replace(" ", "").replace("-", "")
        if (cleanHex.length % 2 != 0) {
            throw IllegalArgumentException("Invalid hex string length")
        }
        
        val bytes = ByteArray(cleanHex.length / 2)
        for (i in bytes.indices) {
            val index = i * 2
            bytes[i] = cleanHex.substring(index, index + 2).toInt(16).toByte()
        }
        
        return bytes
    }
    
    /**
     * Verifica la integridad de un archivo usando checksum
     */
    fun verifyChecksum(data: ByteArray, expectedChecksum: ByteArray): Boolean {
        val actualChecksum = sha256(data)
        return constantTimeEquals(actualChecksum, expectedChecksum)
    }
    
    /**
     * Genera un identificador único basado en timestamp y random
     */
    fun generateUniqueId(): String {
        val timestamp = System.currentTimeMillis()
        val random = SecureRandomGenerator.generateBytes(8)
        val combined = "$timestamp-${bytesToHex(random)}"
        return bytesToHex(sha256(combined.toByteArray()))
    }
}
