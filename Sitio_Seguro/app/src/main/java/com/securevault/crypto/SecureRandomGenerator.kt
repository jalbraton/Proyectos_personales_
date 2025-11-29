package com.securevault.crypto

import com.securevault.utils.Logger
import java.security.SecureRandom

/**
 * Utilidades para generación de números aleatorios criptográficamente seguros
 */
object SecureRandomGenerator {
    
    private val secureRandom = SecureRandom()
    
    init {
        // Pre-seed el generador para mejor rendimiento
        secureRandom.nextBytes(ByteArray(32))
        Logger.crypto("SecureRandom initialized")
    }
    
    /**
     * Genera bytes aleatorios criptográficamente seguros
     */
    fun generateBytes(size: Int): ByteArray {
        val bytes = ByteArray(size)
        secureRandom.nextBytes(bytes)
        return bytes
    }
    
    /**
     * Genera un entero aleatorio en un rango
     */
    fun generateInt(min: Int = Int.MIN_VALUE, max: Int = Int.MAX_VALUE): Int {
        val range = (max - min).toLong() + 1
        val random = (secureRandom.nextLong() and Long.MAX_VALUE) % range
        return (min + random).toInt()
    }
    
    /**
     * Genera un long aleatorio en un rango
     */
    fun generateLong(min: Long = Long.MIN_VALUE, max: Long = Long.MAX_VALUE): Long {
        return min + (secureRandom.nextDouble() * (max - min)).toLong()
    }
    
    /**
     * Genera una contraseña aleatoria segura
     */
    fun generatePassword(length: Int = 16): String {
        val uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val lowercase = "abcdefghijklmnopqrstuvwxyz"
        val digits = "0123456789"
        val special = "!@#$%^&*()-_=+[]{}|;:,.<>?"
        val all = uppercase + lowercase + digits + special
        
        val password = StringBuilder()
        
        // Asegura al menos un carácter de cada tipo
        password.append(uppercase[generateInt(0, uppercase.length - 1)])
        password.append(lowercase[generateInt(0, lowercase.length - 1)])
        password.append(digits[generateInt(0, digits.length - 1)])
        password.append(special[generateInt(0, special.length - 1)])
        
        // Rellena el resto
        for (i in 4 until length) {
            password.append(all[generateInt(0, all.length - 1)])
        }
        
        // Mezcla los caracteres
        val chars = password.toString().toCharArray()
        for (i in chars.indices) {
            val j = generateInt(0, chars.size - 1)
            val temp = chars[i]
            chars[i] = chars[j]
            chars[j] = temp
        }
        
        return String(chars)
    }
}
