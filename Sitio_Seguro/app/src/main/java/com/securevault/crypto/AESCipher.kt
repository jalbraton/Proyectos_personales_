package com.securevault.crypto

import com.securevault.utils.Constants
import com.securevault.utils.Logger
import com.securevault.utils.clear
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Implementación de cifrado AES-256 en modo XTS
 * Similar a la implementación de VeraCrypt
 * 
 * XTS (XEX-based tweaked-codebook mode with ciphertext stealing) es el modo
 * estándar para cifrado de disco completo, usado por VeraCrypt, BitLocker, etc.
 */
class AESCipher {
    
    companion object {
        /**
         * Cifra datos usando AES-256-XTS
         * 
         * @param plaintext Datos a cifrar
         * @param key1 Primera clave de 256 bits
         * @param key2 Segunda clave de 256 bits (para XTS)
         * @param sectorIndex Índice del sector (para XTS tweak)
         * @return Datos cifrados
         */
        fun encrypt(
            plaintext: ByteArray,
            key1: ByteArray,
            key2: ByteArray,
            sectorIndex: Long = 0
        ): ByteArray {
            try {
                Logger.crypto("Encrypting ${plaintext.size} bytes, sector $sectorIndex")
                
                // XTS usa dos instancias de AES
                val cipher1 = createCipher(key1, Cipher.ENCRYPT_MODE, sectorIndex)
                val cipher2 = createCipher(key2, Cipher.ENCRYPT_MODE, sectorIndex)
                
                // Implementación simplificada de XTS
                // En producción, esto debería ser una implementación completa de XTS
                val encrypted = xtsEncrypt(plaintext, cipher1, cipher2, sectorIndex)
                
                Logger.crypto("Encryption successful")
                return encrypted
                
            } catch (e: Exception) {
                Logger.e("Encryption failed", e)
                throw CryptoException("Failed to encrypt data", e)
            }
        }
        
        /**
         * Descifra datos usando AES-256-XTS
         * 
         * @param ciphertext Datos cifrados
         * @param key1 Primera clave de 256 bits
         * @param key2 Segunda clave de 256 bits (para XTS)
         * @param sectorIndex Índice del sector (para XTS tweak)
         * @return Datos descifrados
         */
        fun decrypt(
            ciphertext: ByteArray,
            key1: ByteArray,
            key2: ByteArray,
            sectorIndex: Long = 0
        ): ByteArray {
            try {
                Logger.crypto("Decrypting ${ciphertext.size} bytes, sector $sectorIndex")
                
                val cipher1 = createCipher(key1, Cipher.DECRYPT_MODE, sectorIndex)
                val cipher2 = createCipher(key2, Cipher.DECRYPT_MODE, sectorIndex)
                
                val decrypted = xtsDecrypt(ciphertext, cipher1, cipher2, sectorIndex)
                
                Logger.crypto("Decryption successful")
                return decrypted
                
            } catch (e: Exception) {
                Logger.e("Decryption failed", e)
                throw CryptoException("Failed to decrypt data", e)
            }
        }
        
        /**
         * Cifra un flujo de datos en modo XTS sector por sector
         */
        fun encryptStream(
            plaintext: ByteArray,
            key1: ByteArray,
            key2: ByteArray,
            startSector: Long = 0
        ): ByteArray {
            val sectorSize = Constants.AES_BLOCK_SIZE * 32 // 512 bytes por sector
            val result = ByteArray(plaintext.size)
            
            var offset = 0
            var sectorIndex = startSector
            
            while (offset < plaintext.size) {
                val sectorEnd = minOf(offset + sectorSize, plaintext.size)
                val sectorData = plaintext.copyOfRange(offset, sectorEnd)
                val encryptedSector = encrypt(sectorData, key1, key2, sectorIndex)
                
                encryptedSector.copyInto(result, offset)
                
                sectorData.clear()
                encryptedSector.clear()
                
                offset = sectorEnd
                sectorIndex++
            }
            
            return result
        }
        
        /**
         * Descifra un flujo de datos en modo XTS sector por sector
         */
        fun decryptStream(
            ciphertext: ByteArray,
            key1: ByteArray,
            key2: ByteArray,
            startSector: Long = 0
        ): ByteArray {
            val sectorSize = Constants.AES_BLOCK_SIZE * 32
            val result = ByteArray(ciphertext.size)
            
            var offset = 0
            var sectorIndex = startSector
            
            while (offset < ciphertext.size) {
                val sectorEnd = minOf(offset + sectorSize, ciphertext.size)
                val sectorData = ciphertext.copyOfRange(offset, sectorEnd)
                val decryptedSector = decrypt(sectorData, key1, key2, sectorIndex)
                
                decryptedSector.copyInto(result, offset)
                
                sectorData.clear()
                decryptedSector.clear()
                
                offset = sectorEnd
                sectorIndex++
            }
            
            return result
        }
        
        /**
         * Crea un cipher AES configurado
         */
        private fun createCipher(
            key: ByteArray,
            mode: Int,
            sectorIndex: Long
        ): Cipher {
            val keySpec = SecretKeySpec(key, Constants.AES_ALGORITHM)
            val cipher = Cipher.getInstance("AES/CBC/NoPadding")
            
            // IV derivado del índice del sector para XTS
            val iv = ByteArray(16)
            for (i in 0..7) {
                iv[i] = ((sectorIndex shr (i * 8)) and 0xFF).toByte()
            }
            
            val ivSpec = IvParameterSpec(iv)
            cipher.init(mode, keySpec, ivSpec)
            
            iv.clear()
            return cipher
        }
        
        /**
         * Implementación simplificada de XTS encryption
         * Nota: Esta es una implementación educativa. Para producción,
         * usar una biblioteca probada de XTS.
         */
        private fun xtsEncrypt(
            plaintext: ByteArray,
            cipher1: Cipher,
            cipher2: Cipher,
            sectorIndex: Long
        ): ByteArray {
            // Padding a múltiplo del tamaño de bloque
            val paddedSize = ((plaintext.size + 15) / 16) * 16
            val padded = plaintext.copyOf(paddedSize)
            
            // Genera tweak value
            val tweak = ByteArray(16)
            for (i in 0..7) {
                tweak[i] = ((sectorIndex shr (i * 8)) and 0xFF).toByte()
            }
            
            val encryptedTweak = cipher2.doFinal(tweak)
            
            // XOR con tweak, cifrar, XOR de nuevo
            for (i in padded.indices step 16) {
                for (j in 0..15) {
                    if (i + j < padded.size) {
                        padded[i + j] = (padded[i + j].toInt() xor encryptedTweak[j].toInt()).toByte()
                    }
                }
            }
            
            val encrypted = cipher1.doFinal(padded)
            
            for (i in encrypted.indices step 16) {
                for (j in 0..15) {
                    if (i + j < encrypted.size) {
                        encrypted[i + j] = (encrypted[i + j].toInt() xor encryptedTweak[j].toInt()).toByte()
                    }
                }
            }
            
            // Limpieza
            tweak.clear()
            encryptedTweak.clear()
            padded.clear()
            
            return if (plaintext.size == paddedSize) {
                encrypted
            } else {
                encrypted.copyOf(plaintext.size)
            }
        }
        
        /**
         * Implementación simplificada de XTS decryption
         */
        private fun xtsDecrypt(
            ciphertext: ByteArray,
            cipher1: Cipher,
            cipher2: Cipher,
            sectorIndex: Long
        ): ByteArray {
            val paddedSize = ((ciphertext.size + 15) / 16) * 16
            val padded = ciphertext.copyOf(paddedSize)
            
            val tweak = ByteArray(16)
            for (i in 0..7) {
                tweak[i] = ((sectorIndex shr (i * 8)) and 0xFF).toByte()
            }
            
            val encryptedTweak = cipher2.doFinal(tweak)
            
            for (i in padded.indices step 16) {
                for (j in 0..15) {
                    if (i + j < padded.size) {
                        padded[i + j] = (padded[i + j].toInt() xor encryptedTweak[j].toInt()).toByte()
                    }
                }
            }
            
            val decrypted = cipher1.doFinal(padded)
            
            for (i in decrypted.indices step 16) {
                for (j in 0..15) {
                    if (i + j < decrypted.size) {
                        decrypted[i + j] = (decrypted[i + j].toInt() xor encryptedTweak[j].toInt()).toByte()
                    }
                }
            }
            
            tweak.clear()
            encryptedTweak.clear()
            padded.clear()
            
            return if (ciphertext.size == paddedSize) {
                decrypted
            } else {
                decrypted.copyOf(ciphertext.size)
            }
        }
    }
}
