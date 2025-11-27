package com.securevault.volume

import com.securevault.utils.Constants
import com.securevault.utils.Logger
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * Gestiona la creación, apertura y cierre de volúmenes encriptados
 * Singleton thread-safe
 */
object VolumeManager {
    
    private val openVolumes = ConcurrentHashMap<String, EncryptedVolume>()
    
    /**
     * Crea un nuevo volumen encriptado
     */
    fun createVolume(
        volumePath: String,
        password: CharArray,
        size: Long = Constants.DEFAULT_VOLUME_SIZE
    ): Result<EncryptedVolume> {
        return try {
            val volumeFile = File(volumePath)
            
            // Verifica que el archivo no existe
            if (volumeFile.exists()) {
                return Result.failure(VolumeException("Volume file already exists"))
            }
            
            // Crea el directorio padre si no existe
            volumeFile.parentFile?.mkdirs()
            
            // Crea el volumen
            val volume = EncryptedVolume.create(volumeFile, password, size)
            
            // Registra el volumen abierto
            openVolumes[volumePath] = volume
            
            Logger.i("Volume created and opened: $volumePath")
            Result.success(volume)
            
        } catch (e: Exception) {
            Logger.e("Failed to create volume", e)
            Result.failure(e)
        }
    }
    
    /**
     * Abre un volumen existente
     */
    fun openVolume(
        volumePath: String,
        password: CharArray
    ): Result<EncryptedVolume> {
        return try {
            // Verifica si ya está abierto
            if (openVolumes.containsKey(volumePath)) {
                return Result.failure(VolumeException("Volume is already open"))
            }
            
            val volumeFile = File(volumePath)
            
            if (!volumeFile.exists()) {
                return Result.failure(VolumeException("Volume file does not exist"))
            }
            
            // Abre el volumen
            val volume = EncryptedVolume.open(volumeFile, password)
            
            // Registra el volumen abierto
            openVolumes[volumePath] = volume
            
            Logger.i("Volume opened: $volumePath")
            Result.success(volume)
            
        } catch (e: Exception) {
            Logger.e("Failed to open volume", e)
            Result.failure(e)
        }
    }
    
    /**
     * Cierra un volumen abierto
     */
    fun closeVolume(volumePath: String): Result<Unit> {
        return try {
            val volume = openVolumes.remove(volumePath)
                ?: return Result.failure(VolumeException("Volume is not open"))
            
            volume.close()
            
            Logger.i("Volume closed: $volumePath")
            Result.success(Unit)
            
        } catch (e: Exception) {
            Logger.e("Failed to close volume", e)
            Result.failure(e)
        }
    }
    
    /**
     * Obtiene un volumen abierto
     */
    fun getVolume(volumePath: String): EncryptedVolume? {
        return openVolumes[volumePath]
    }
    
    /**
     * Verifica si un volumen está abierto
     */
    fun isVolumeOpen(volumePath: String): Boolean {
        return openVolumes.containsKey(volumePath)
    }
    
    /**
     * Obtiene la lista de volúmenes abiertos
     */
    fun getOpenVolumes(): List<String> {
        return openVolumes.keys.toList()
    }
    
    /**
     * Cierra todos los volúmenes abiertos
     */
    fun closeAllVolumes() {
        Logger.i("Closing all volumes...")
        val volumes = openVolumes.values.toList()
        openVolumes.clear()
        
        volumes.forEach { volume ->
            try {
                volume.close()
            } catch (e: Exception) {
                Logger.e("Error closing volume", e)
            }
        }
        
        Logger.i("All volumes closed")
    }
    
    /**
     * Valida si un archivo es un volumen válido (verifica el magic number)
     */
    fun isValidVolume(volumePath: String): Boolean {
        return try {
            val file = File(volumePath)
            if (!file.exists() || file.length() < VolumeHeader.HEADER_SIZE) {
                false
            } else {
                file.inputStream().use { input ->
                    val magic = ByteArray(4)
                    input.read(magic)
                    String(magic, Charsets.UTF_8) == Constants.VOLUME_MAGIC
                }
            }
        } catch (e: Exception) {
            Logger.e("Error validating volume", e)
            false
        }
    }
    
    /**
     * Obtiene información básica de un volumen sin abrirlo
     */
    fun getVolumeInfo(volumePath: String): Result<BasicVolumeInfo> {
        return try {
            val file = File(volumePath)
            if (!file.exists()) {
                return Result.failure(VolumeException("Volume file does not exist"))
            }
            
            file.inputStream().use { input ->
                val headerBytes = ByteArray(VolumeHeader.HEADER_SIZE)
                input.read(headerBytes)
                
                // Solo lee información no encriptada
                val magic = String(headerBytes.copyOfRange(0, 4), Charsets.UTF_8)
                
                if (magic != Constants.VOLUME_MAGIC) {
                    return Result.failure(VolumeException("Invalid volume file"))
                }
                
                val info = BasicVolumeInfo(
                    path = volumePath,
                    fileSize = file.length(),
                    lastModified = file.lastModified()
                )
                
                Result.success(info)
            }
        } catch (e: Exception) {
            Logger.e("Failed to get volume info", e)
            Result.failure(e)
        }
    }
}

/**
 * Información básica de un volumen (sin abrirlo)
 */
data class BasicVolumeInfo(
    val path: String,
    val fileSize: Long,
    val lastModified: Long
)
