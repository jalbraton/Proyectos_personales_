package com.securevault.crypto

import com.securevault.utils.Constants
import com.securevault.utils.Logger
import com.securevault.utils.clear
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import java.security.SecureRandom

/**
 * Gestiona la derivación de claves criptográficas desde contraseñas
 * usando PBKDF2 con HMAC-SHA512
 */
class KeyDerivation {
    
    companion object {
        /**
         * Genera un salt aleatorio criptográficamente seguro
         */
        fun generateSalt(): ByteArray {
            val salt = ByteArray(Constants.SALT_SIZE)
            SecureRandom().nextBytes(salt)
            Logger.crypto("Generated salt of ${salt.size} bytes")
            return salt
        }
        
        /**
         * Deriva una clave criptográfica desde una contraseña usando PBKDF2
         * 
         * @param password Contraseña del usuario
         * @param salt Salt único para esta derivación
         * @param iterations Número de iteraciones (default: 100,000)
         * @return Par de claves de 256 bits cada una (para XTS mode)
         */
        fun deriveKey(
            password: CharArray,
            salt: ByteArray,
            iterations: Int = Constants.PBKDF2_ITERATIONS
        ): Pair<ByteArray, ByteArray> {
            try {
                Logger.crypto("Deriving key with $iterations iterations")
                
                val spec = PBEKeySpec(
                    password,
                    salt,
                    iterations,
                    Constants.PBKDF2_KEY_LENGTH
                )
                
                val factory = SecretKeyFactory.getInstance(Constants.PBKDF2_ALGORITHM)
                val keyBytes = factory.generateSecret(spec).encoded
                
                // Limpia la spec
                spec.clearPassword()
                
                // XTS requiere dos claves independientes
                val key1 = keyBytes.copyOfRange(0, 32) // Primera clave de 256 bits
                val key2 = keyBytes.copyOfRange(32, 64) // Segunda clave de 256 bits
                
                // Limpia el array completo
                keyBytes.clear()
                
                Logger.crypto("Key derivation successful")
                return Pair(key1, key2)
                
            } catch (e: Exception) {
                Logger.e("Key derivation failed", e)
                throw CryptoException("Failed to derive key", e)
            }
        }
        
        /**
         * Verifica una contraseña contra un hash almacenado
         * 
         * @param password Contraseña a verificar
         * @param salt Salt usado en la derivación original
         * @param storedKey1 Primera parte de la clave almacenada
         * @param storedKey2 Segunda parte de la clave almacenada
         * @param iterations Número de iteraciones usado
         * @return true si la contraseña es correcta
         */
        fun verifyPassword(
            password: CharArray,
            salt: ByteArray,
            storedKey1: ByteArray,
            storedKey2: ByteArray,
            iterations: Int = Constants.PBKDF2_ITERATIONS
        ): Boolean {
            return try {
                val (derivedKey1, derivedKey2) = deriveKey(password, salt, iterations)
                
                val matches = derivedKey1.contentEquals(storedKey1) && 
                             derivedKey2.contentEquals(storedKey2)
                
                // Limpia las claves derivadas
                derivedKey1.clear()
                derivedKey2.clear()
                
                matches
            } catch (e: Exception) {
                Logger.e("Password verification failed", e)
                false
            }
        }
        
        /**
         * Estima el tiempo de derivación de clave
         * Útil para ajustar el número de iteraciones
         */
        fun benchmarkDerivation(iterations: Int = 10_000): Long {
            val testPassword = "TestPassword123!".toCharArray()
            val testSalt = generateSalt()
            
            val startTime = System.currentTimeMillis()
            deriveKey(testPassword, testSalt, iterations)
            val endTime = System.currentTimeMillis()
            
            testPassword.clear()
            testSalt.clear()
            
            return endTime - startTime
        }
    }
}
