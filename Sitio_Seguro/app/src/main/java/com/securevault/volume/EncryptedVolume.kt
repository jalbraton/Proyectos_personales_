package com.securevault.volume

import com.securevault.crypto.AESCipher
import com.securevault.crypto.KeyDerivation
import com.securevault.utils.Constants
import com.securevault.utils.Logger
import com.securevault.utils.clear
import java.io.File
import java.io.RandomAccessFile

/**
 * Representa un volumen encriptado abierto y montado
 * Gestiona el acceso a los datos encriptados
 */
class EncryptedVolume private constructor(
    val volumeFile: File,
    val header: VolumeHeader,
    private val key1: ByteArray,
    private val key2: ByteArray
) {
    
    private var isClosed = false
    private val randomAccessFile: RandomAccessFile = RandomAccessFile(volumeFile, "rw")
    
    companion object {
        /**
         * Abre un volumen existente con una contraseña
         */
        fun open(volumeFile: File, password: CharArray): EncryptedVolume {
            if (!volumeFile.exists() || !volumeFile.canRead()) {
                throw VolumeException("Volume file not accessible: ${volumeFile.path}")
            }
            
            Logger.i("Opening volume: ${volumeFile.name}")
            
            val randomAccess = RandomAccessFile(volumeFile, "rw")
            
            try {
                // Lee el header encriptado
                val encryptedHeader = ByteArray(VolumeHeader.HEADER_SIZE)
                randomAccess.seek(0)
                randomAccess.readFully(encryptedHeader)
                randomAccess.close() // ✅ FIX: Cerrar antes de crear el volumen
                
                // Extrae el salt del header (no está encriptado)
                val tempHeader = VolumeHeader.fromBytes(encryptedHeader)
                val salt = tempHeader.salt
                
                // Deriva las claves desde la contraseña
                val (key1, key2) = KeyDerivation.deriveKey(password, salt, tempHeader.iterations)
                
                // Intenta descifrar el header
                val decryptedHeader = try {
                    AESCipher.decrypt(encryptedHeader, key1, key2, 0)
                } catch (e: Exception) {
                    key1.clear()
                    key2.clear()
                    randomAccess.close()
                    throw VolumeException("Failed to decrypt header - wrong password?", e)
                }
                
                // Parse el header descifrado
                val header = VolumeHeader.fromBytes(decryptedHeader)
                decryptedHeader.clear()
                
                // Verifica la integridad
                if (!header.verifyIntegrity()) {
                    key1.clear()
                    key2.clear()
                    throw VolumeException("Header integrity check failed")
                }
                
                Logger.i("Volume opened successfully")
                
                return EncryptedVolume(volumeFile, header, key1, key2)
                
            } catch (e: Exception) {
                try { randomAccess.close() } catch (_: Exception) {}
                throw e
            }
        }
        
        /**
         * Crea un nuevo volumen
         */
        fun create(
            volumeFile: File,
            password: CharArray,
            volumeSize: Long
        ): EncryptedVolume {
            if (volumeSize < Constants.MIN_VOLUME_SIZE || volumeSize > Constants.MAX_VOLUME_SIZE) {
                throw VolumeException("Invalid volume size: $volumeSize")
            }
            
            Logger.i("Creating new volume: ${volumeFile.name}, size: $volumeSize")
            
            // Genera salt
            val salt = KeyDerivation.generateSalt()
            
            // Deriva claves desde la contraseña
            val (key1, key2) = KeyDerivation.deriveKey(password, salt)
            
            // Crea el header
            val header = VolumeHeader.create(volumeSize, salt)
            
            // Encripta el header
            val encryptedHeader = header.encrypt(key1, key2)
            
            // Crea el archivo de volumen
            val randomAccess = RandomAccessFile(volumeFile, "rw")
            
            try {
                // Escribe el header encriptado
                randomAccess.seek(0)
                randomAccess.write(encryptedHeader)
                
                // Reserva espacio para los datos
                randomAccess.setLength(header.dataOffset + volumeSize)
                
                // Inicializa el área de datos con datos aleatorios (opcional, hace más lenta la creación)
                // Por ahora lo dejamos en ceros para rapidez
                
                Logger.i("Volume created successfully")
                randomAccess.close()
                
                encryptedHeader.clear()
                
                return EncryptedVolume(volumeFile, header, key1, key2)
                
            } catch (e: Exception) {
                randomAccess.close()
                key1.clear()
                key2.clear()
                volumeFile.delete()
                throw VolumeException("Failed to create volume", e)
            }
        }
    }
    
    /**
     * Lee datos encriptados desde el volumen
     */
    fun readData(offset: Long, length: Int): ByteArray {
        checkNotClosed()
        
        if (offset < 0 || offset + length > header.volumeSize) {
            throw VolumeException("Read out of bounds: offset=$offset, length=$length")
        }
        
        val encryptedData = ByteArray(length)
        randomAccessFile.seek(header.dataOffset + offset)
        randomAccessFile.readFully(encryptedData)
        
        // Calcula el sector de inicio para XTS
        val sectorIndex = (offset / 512) + 1 // +1 para evitar sector 0 (usado por header)
        
        // Descifra los datos
        val decryptedData = AESCipher.decryptStream(encryptedData, key1, key2, sectorIndex)
        encryptedData.clear()
        
        return decryptedData
    }
    
    /**
     * Escribe datos encriptados al volumen
     */
    fun writeData(offset: Long, data: ByteArray) {
        checkNotClosed()
        
        if (offset < 0 || offset + data.size > header.volumeSize) {
            throw VolumeException("Write out of bounds: offset=$offset, size=${data.size}")
        }
        
        val sectorIndex = (offset / 512) + 1
        
        // Cifra los datos
        val encryptedData = AESCipher.encryptStream(data, key1, key2, sectorIndex)
        
        randomAccessFile.seek(header.dataOffset + offset)
        randomAccessFile.write(encryptedData)
        
        encryptedData.clear()
    }
    
    /**
     * Obtiene el tamaño disponible del volumen
     */
    fun getAvailableSize(): Long {
        checkNotClosed()
        return header.volumeSize
    }
    
    /**
     * Obtiene información del volumen
     */
    fun getInfo(): VolumeInfo {
        checkNotClosed()
        return VolumeInfo(
            volumeId = header.volumeId.copyOf(),
            creationTime = header.creationTimestamp,
            size = header.volumeSize,
            cipherName = "AES-256-XTS",
            volumePath = volumeFile.absolutePath
        )
    }
    
    /**
     * Sincroniza los datos al disco
     */
    fun sync() {
        checkNotClosed()
        randomAccessFile.fd.sync()
    }
    
    /**
     * Cierra el volumen y limpia las claves de memoria
     */
    fun close() {
        if (isClosed) return
        
        Logger.i("Closing volume: ${volumeFile.name}")
        
        try {
            sync()
            randomAccessFile.close()
        } finally {
            // Limpia las claves de la memoria
            key1.clear()
            key2.clear()
            header.clear()
            isClosed = true
        }
    }
    
    private fun checkNotClosed() {
        if (isClosed) {
            throw VolumeException("Volume is closed")
        }
    }
    
    /**
     * Asegura que el volumen se cierre incluso si se olvida
     */
    protected fun finalize() {
        if (!isClosed) {
            Logger.w("Volume was not closed properly, forcing close")
            close()
        }
    }
}

/**
 * Información de un volumen
 */
data class VolumeInfo(
    val volumeId: ByteArray,
    val creationTime: Long,
    val size: Long,
    val cipherName: String,
    val volumePath: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VolumeInfo) return false
        return volumeId.contentEquals(other.volumeId)
    }
    
    override fun hashCode(): Int {
        return volumeId.contentHashCode()
    }
}
