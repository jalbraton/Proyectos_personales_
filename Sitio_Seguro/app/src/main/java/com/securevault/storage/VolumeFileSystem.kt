package com.securevault.storage

import com.securevault.volume.EncryptedVolume
import com.securevault.utils.Logger
import java.io.*

/**
 * Gestiona archivos dentro de un volumen encriptado
 * Implementa un sistema de archivos simple sobre el volumen
 */
class VolumeFileSystem(private val volume: EncryptedVolume) {
    
    companion object {
        private const val FILE_TABLE_OFFSET = 0L
        private const val FILE_TABLE_SIZE = 1024 * 64 // 64KB para la tabla de archivos
        private const val DATA_START_OFFSET = FILE_TABLE_SIZE.toLong()
        private const val MAX_FILES = 512
        private const val MAX_FILENAME_LENGTH = 256
    }
    
    private val fileTable = mutableListOf<FileEntry>()
    
    init {
        loadFileTable()
    }
    
    /**
     * Agrega un archivo al volumen
     */
    fun addFile(sourcePath: String, destinationName: String): Result<FileEntry> {
        return try {
            val sourceFile = File(sourcePath)
            
            if (!sourceFile.exists() || !sourceFile.canRead()) {
                return Result.failure(IOException("Source file not accessible"))
            }
            
            if (fileTable.size >= MAX_FILES) {
                return Result.failure(IOException("Volume is full (max files)"))
            }
            
            val fileSize = sourceFile.length()
            
            // Encuentra espacio libre
            val offset = findFreeSpace(fileSize)
                ?: return Result.failure(IOException("Not enough space in volume"))
            
            // Crea la entrada del archivo
            val entry = FileEntry(
                name = destinationName,
                size = fileSize,
                offset = offset,
                timestamp = System.currentTimeMillis()
            )
            
            // Lee y escribe el archivo
            sourceFile.inputStream().use { input ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                var currentOffset = offset
                
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    val dataToWrite = if (bytesRead == buffer.size) buffer else buffer.copyOf(bytesRead)
                    volume.writeData(currentOffset, dataToWrite)
                    currentOffset += bytesRead
                }
            }
            
            // Agrega a la tabla
            fileTable.add(entry)
            saveFileTable()
            
            Logger.i("File added to volume: $destinationName")
            Result.success(entry)
            
        } catch (e: Exception) {
            Logger.e("Failed to add file", e)
            Result.failure(e)
        }
    }
    
    /**
     * Extrae un archivo del volumen
     */
    fun extractFile(fileName: String, destinationPath: String): Result<Unit> {
        return try {
            val entry = fileTable.find { it.name == fileName }
                ?: return Result.failure(FileNotFoundException("File not found in volume"))
            
            val destFile = File(destinationPath)
            destFile.parentFile?.mkdirs()
            
            destFile.outputStream().use { output ->
                var remaining = entry.size
                var currentOffset = entry.offset
                
                while (remaining > 0) {
                    val toRead = minOf(remaining, 8192L).toInt()
                    val data = volume.readData(currentOffset, toRead)
                    output.write(data)
                    currentOffset += toRead
                    remaining -= toRead
                }
            }
            
            Logger.i("File extracted: $fileName")
            Result.success(Unit)
            
        } catch (e: Exception) {
            Logger.e("Failed to extract file", e)
            Result.failure(e)
        }
    }
    
    /**
     * Elimina un archivo del volumen
     */
    fun deleteFile(fileName: String): Result<Unit> {
        return try {
            val removed = fileTable.removeIf { it.name == fileName }
            
            if (!removed) {
                return Result.failure(FileNotFoundException("File not found"))
            }
            
            saveFileTable()
            Logger.i("File deleted: $fileName")
            Result.success(Unit)
            
        } catch (e: Exception) {
            Logger.e("Failed to delete file", e)
            Result.failure(e)
        }
    }
    
    /**
     * Lista todos los archivos en el volumen
     */
    fun listFiles(): List<FileEntry> {
        return fileTable.toList()
    }
    
    /**
     * Obtiene información de un archivo
     */
    fun getFileInfo(fileName: String): FileEntry? {
        return fileTable.find { it.name == fileName }
    }
    
    /**
     * Calcula el espacio usado
     */
    fun getUsedSpace(): Long {
        return fileTable.sumOf { it.size } + FILE_TABLE_SIZE
    }
    
    /**
     * Calcula el espacio libre
     */
    fun getFreeSpace(): Long {
        return volume.getAvailableSize() - getUsedSpace()
    }
    
    /**
     * Carga la tabla de archivos desde el volumen
     */
    private fun loadFileTable() {
        try {
            val tableData = volume.readData(FILE_TABLE_OFFSET, FILE_TABLE_SIZE)
            val input = DataInputStream(ByteArrayInputStream(tableData))
            
            val fileCount = input.readInt()
            
            for (i in 0 until minOf(fileCount, MAX_FILES)) {
                val nameLength = input.readInt()
                val nameBytes = ByteArray(nameLength)
                input.readFully(nameBytes)
                val name = String(nameBytes, Charsets.UTF_8)
                
                val size = input.readLong()
                val offset = input.readLong()
                val timestamp = input.readLong()
                
                fileTable.add(FileEntry(name, size, offset, timestamp))
            }
            
            Logger.i("Loaded ${fileTable.size} files from volume")
            
        } catch (e: Exception) {
            // Si no puede leer la tabla, asume que está vacía
            Logger.w("Could not load file table, assuming empty volume", e)
            fileTable.clear()
        }
    }
    
    /**
     * Guarda la tabla de archivos al volumen
     */
    private fun saveFileTable() {
        val output = ByteArrayOutputStream()
        val dataOutput = DataOutputStream(output)
        
        dataOutput.writeInt(fileTable.size)
        
        for (entry in fileTable) {
            val nameBytes = entry.name.toByteArray(Charsets.UTF_8)
            dataOutput.writeInt(nameBytes.size)
            dataOutput.write(nameBytes)
            dataOutput.writeLong(entry.size)
            dataOutput.writeLong(entry.offset)
            dataOutput.writeLong(entry.timestamp)
        }
        
        // Rellena el resto con ceros
        val tableData = output.toByteArray()
        val paddedTable = ByteArray(FILE_TABLE_SIZE)
        tableData.copyInto(paddedTable)
        
        volume.writeData(FILE_TABLE_OFFSET, paddedTable)
        Logger.i("File table saved")
    }
    
    /**
     * Encuentra espacio libre en el volumen
     */
    private fun findFreeSpace(requiredSize: Long): Long? {
        if (fileTable.isEmpty()) {
            return DATA_START_OFFSET
        }
        
        // Ordena archivos por offset
        val sorted = fileTable.sortedBy { it.offset }
        
        // Busca espacio entre archivos
        var currentOffset = DATA_START_OFFSET
        
        for (entry in sorted) {
            if (entry.offset - currentOffset >= requiredSize) {
                return currentOffset
            }
            currentOffset = entry.offset + entry.size
        }
        
        // Verifica espacio al final
        val remainingSpace = volume.getAvailableSize() - currentOffset
        return if (remainingSpace >= requiredSize) currentOffset else null
    }
}

/**
 * Representa un archivo dentro del volumen
 */
data class FileEntry(
    val name: String,
    val size: Long,
    val offset: Long,
    val timestamp: Long
)
