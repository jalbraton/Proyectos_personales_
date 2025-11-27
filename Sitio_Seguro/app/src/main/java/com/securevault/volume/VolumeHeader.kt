package com.securevault.volume

import com.securevault.crypto.AESCipher
import com.securevault.crypto.CryptoUtils
import com.securevault.crypto.SecureRandomGenerator
import com.securevault.utils.Constants
import com.securevault.utils.Logger
import com.securevault.utils.clear
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

/**
 * Representa el header de un volumen encriptado
 * Similar al header de VeraCrypt
 * 
 * Estructura del header (512 bytes):
 * - Magic number (4 bytes): "SVLT"
 * - Version (4 bytes): versión del formato
 * - Salt (32 bytes): salt para derivación de clave
 * - Volume ID (32 bytes): identificador único del volumen
 * - Creation timestamp (8 bytes): fecha de creación
 * - Volume size (8 bytes): tamaño del volumen
 * - Data offset (8 bytes): offset donde empiezan los datos
 * - Cipher identifier (4 bytes): algoritmo de cifrado
 * - Iterations (4 bytes): iteraciones de PBKDF2
 * - Header checksum (32 bytes): SHA-256 del header
 * - Reserved (376 bytes): para uso futuro
 */
class VolumeHeader {
    var magic: String = Constants.VOLUME_MAGIC
    var version: Int = Constants.VOLUME_VERSION
    var salt: ByteArray = ByteArray(Constants.SALT_SIZE)
    var volumeId: ByteArray = ByteArray(32)
    var creationTimestamp: Long = 0
    var volumeSize: Long = 0
    var dataOffset: Long = Constants.VOLUME_HEADER_SIZE.toLong()
    var cipherIdentifier: Int = CIPHER_AES_256_XTS
    var iterations: Int = Constants.PBKDF2_ITERATIONS
    var headerChecksum: ByteArray = ByteArray(32)
    
    companion object {
        const val CIPHER_AES_256_XTS = 1
        const val HEADER_SIZE = Constants.VOLUME_HEADER_SIZE
        
        /**
         * Crea un nuevo header de volumen
         */
        fun create(volumeSize: Long, salt: ByteArray): VolumeHeader {
            val header = VolumeHeader()
            header.salt = salt.copyOf()
            header.volumeId = SecureRandomGenerator.generateBytes(32)
            header.creationTimestamp = System.currentTimeMillis()
            header.volumeSize = volumeSize
            header.dataOffset = HEADER_SIZE.toLong()
            
            Logger.crypto("Created new volume header for ${volumeSize} bytes")
            return header
        }
        
        /**
         * Lee un header desde un array de bytes
         */
        fun fromBytes(bytes: ByteArray): VolumeHeader {
            if (bytes.size < HEADER_SIZE) {
                throw VolumeException("Invalid header size: ${bytes.size}")
            }
            
            val header = VolumeHeader()
            val input = DataInputStream(ByteArrayInputStream(bytes))
            
            try {
                // Lee el magic number
                val magicBytes = ByteArray(4)
                input.readFully(magicBytes)
                header.magic = String(magicBytes, StandardCharsets.UTF_8)
                
                if (header.magic != Constants.VOLUME_MAGIC) {
                    throw VolumeException("Invalid volume magic: ${header.magic}")
                }
                
                // Lee el resto de campos
                header.version = input.readInt()
                input.readFully(header.salt)
                input.readFully(header.volumeId)
                header.creationTimestamp = input.readLong()
                header.volumeSize = input.readLong()
                header.dataOffset = input.readLong()
                header.cipherIdentifier = input.readInt()
                header.iterations = input.readInt()
                input.readFully(header.headerChecksum)
                
                Logger.crypto("Loaded volume header, version: ${header.version}")
                return header
                
            } catch (e: Exception) {
                Logger.e("Failed to parse header", e)
                throw VolumeException("Failed to read volume header", e)
            } finally {
                input.close()
            }
        }
    }
    
    /**
     * Convierte el header a array de bytes
     */
    fun toBytes(): ByteArray {
        val output = ByteArrayOutputStream(HEADER_SIZE)
        val dataOutput = DataOutputStream(output)
        
        try {
            // Escribe todos los campos (sin checksum primero)
            dataOutput.write(magic.toByteArray(StandardCharsets.UTF_8))
            dataOutput.writeInt(version)
            dataOutput.write(salt)
            dataOutput.write(volumeId)
            dataOutput.writeLong(creationTimestamp)
            dataOutput.writeLong(volumeSize)
            dataOutput.writeLong(dataOffset)
            dataOutput.writeInt(cipherIdentifier)
            dataOutput.writeInt(iterations)
            
            // Calcula checksum de los datos hasta ahora
            val checksumData = output.toByteArray()
            headerChecksum = CryptoUtils.sha256(checksumData)
            
            // Escribe el checksum
            dataOutput.write(headerChecksum)
            
            // Rellena el resto con ceros (reserved space)
            val currentSize = output.size()
            val remaining = HEADER_SIZE - currentSize
            if (remaining > 0) {
                dataOutput.write(ByteArray(remaining))
            }
            
            return output.toByteArray()
            
            // Reescribe con el checksum correcto
            output.reset()
            dataOutput.write(magic.toByteArray(StandardCharsets.UTF_8))
            dataOutput.writeInt(version)
            dataOutput.write(salt)
            dataOutput.write(volumeId)
            dataOutput.writeLong(creationTimestamp)
            dataOutput.writeLong(volumeSize)
            dataOutput.writeLong(dataOffset)
            dataOutput.writeInt(cipherIdentifier)
            dataOutput.writeInt(iterations)
            dataOutput.write(headerChecksum)
            
            val finalRemaining = HEADER_SIZE - output.size()
            if (finalRemaining > 0) {
                dataOutput.write(ByteArray(finalRemaining))
            }
            
            return output.toByteArray()
            
        } catch (e: Exception) {
            Logger.e("Failed to serialize header", e)
            throw VolumeException("Failed to write volume header", e)
        } finally {
            dataOutput.close()
        }
    }
    
    /**
     * Encripta el header con las claves proporcionadas
     */
    fun encrypt(key1: ByteArray, key2: ByteArray): ByteArray {
        val plainHeader = toBytes()
        val encrypted = AESCipher.encrypt(plainHeader, key1, key2, 0)
        plainHeader.clear()
        return encrypted
    }
    
    /**
     * Verifica la integridad del header
     */
    fun verifyIntegrity(): Boolean {
        val bytes = toBytes()
        val checksumData = bytes.copyOfRange(0, HEADER_SIZE - 32)
        val calculatedChecksum = CryptoUtils.sha256(checksumData)
        
        val valid = CryptoUtils.constantTimeEquals(calculatedChecksum, headerChecksum)
        
        bytes.clear()
        calculatedChecksum.clear()
        
        return valid
    }
    
    /**
     * Limpia datos sensibles
     */
    fun clear() {
        salt.clear()
        volumeId.clear()
        headerChecksum.clear()
    }
}
