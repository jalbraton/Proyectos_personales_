package com.securevault.utils

import java.io.File
import java.io.InputStream
import java.io.OutputStream

/**
 * Extensiones útiles para la aplicación
 */

/**
 * Limpia un ByteArray sobrescribiéndolo con ceros
 */
fun ByteArray.clear() {
    this.fill(0)
}

/**
 * Limpia un CharArray sobrescribiéndolo con ceros
 */
fun CharArray.clear() {
    this.fill(0.toChar())
}

/**
 * Convierte String a ByteArray de forma segura
 */
fun String.toSecureByteArray(): ByteArray {
    return this.toByteArray(Charsets.UTF_8)
}

/**
 * Copia segura de InputStream a OutputStream con buffer
 */
fun InputStream.copyToWithProgress(
    out: OutputStream,
    bufferSize: Int = Constants.BUFFER_SIZE,
    onProgress: ((Long) -> Unit)? = null
): Long {
    var bytesCopied = 0L
    val buffer = ByteArray(bufferSize)
    var bytes = read(buffer)
    
    while (bytes >= 0) {
        out.write(buffer, 0, bytes)
        bytesCopied += bytes
        onProgress?.invoke(bytesCopied)
        bytes = read(buffer)
    }
    
    buffer.clear()
    return bytesCopied
}

/**
 * Formatea bytes a string legible (KB, MB, GB)
 */
fun Long.formatAsFileSize(): String {
    val kb = 1024.0
    val mb = kb * 1024
    val gb = mb * 1024
    
    return when {
        this >= gb -> String.format("%.2f GB", this / gb)
        this >= mb -> String.format("%.2f MB", this / mb)
        this >= kb -> String.format("%.2f KB", this / kb)
        else -> "$this B"
    }
}

/**
 * Verifica si un archivo existe y es legible
 */
fun File.isReadableFile(): Boolean {
    return this.exists() && this.isFile && this.canRead()
}

/**
 * Verifica si un archivo es escribible
 */
fun File.isWritableFile(): Boolean {
    return this.exists() && this.isFile && this.canWrite()
}

/**
 * Elimina un archivo de forma segura sobrescribiendo su contenido
 */
fun File.secureDelete(): Boolean {
    if (!exists()) return true
    
    try {
        // Sobrescribe el contenido con datos aleatorios
        val fileSize = length()
        outputStream().use { out ->
            val buffer = ByteArray(Constants.BUFFER_SIZE)
            var remaining = fileSize
            
            while (remaining > 0) {
                val toWrite = minOf(remaining, buffer.size.toLong()).toInt()
                java.security.SecureRandom().nextBytes(buffer)
                out.write(buffer, 0, toWrite)
                remaining -= toWrite
            }
            
            buffer.clear()
        }
        
        return delete()
    } catch (e: Exception) {
        Logger.e("Error in secure delete", e)
        return false
    }
}

/**
 * Valida que una contraseña cumpla los requisitos mínimos
 */
fun String.isValidPassword(): Boolean {
    if (length < Constants.MIN_PASSWORD_LENGTH) return false
    
    var hasUpper = false
    var hasLower = false
    var hasDigit = false
    var hasSpecial = false
    
    for (char in this) {
        when {
            char.isUpperCase() -> hasUpper = true
            char.isLowerCase() -> hasLower = true
            char.isDigit() -> hasDigit = true
            !char.isLetterOrDigit() -> hasSpecial = true
        }
    }
    
    // Requiere al menos 3 de 4 categorías
    val categories = listOf(hasUpper, hasLower, hasDigit, hasSpecial).count { it }
    return categories >= 3
}

/**
 * Calcula la fortaleza de una contraseña (0-100)
 */
fun String.passwordStrength(): Int {
    if (isEmpty()) return 0
    
    var score = 0
    
    // Longitud
    score += when {
        length >= 20 -> 30
        length >= 16 -> 25
        length >= 12 -> 20
        length >= 8 -> 10
        else -> 0
    }
    
    // Variedad de caracteres
    if (any { it.isUpperCase() }) score += 15
    if (any { it.isLowerCase() }) score += 15
    if (any { it.isDigit() }) score += 15
    if (any { !it.isLetterOrDigit() }) score += 20
    
    // Diversidad
    val uniqueChars = toSet().size
    score += minOf(uniqueChars * 2, 15)
    
    return minOf(score, 100)
}
