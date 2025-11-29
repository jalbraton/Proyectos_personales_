package com.securevault.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.securevault.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class EncryptedFilesAdapter(
    private val onFileClick: (File) -> Unit,
    private val onFileDelete: (File) -> Unit,
    private val onFileExport: (File) -> Unit
) : ListAdapter<File, EncryptedFilesAdapter.ViewHolder>(FileDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_encrypted_file, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconFile: ImageView = itemView.findViewById(R.id.iconFile)
        private val tvFileName: TextView = itemView.findViewById(R.id.tvFileName)
        private val tvFileSize: TextView = itemView.findViewById(R.id.tvFileSize)
        private val tvFileDate: TextView = itemView.findViewById(R.id.tvFileDate)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteFile)
        private val btnExport: ImageButton = itemView.findViewById(R.id.btnExportFile)

        fun bind(file: File) {
            // Nombre sin extensión .enc
            val displayName = file.name.removeSuffix(".enc")
            tvFileName.text = displayName
            
            // Tamaño
            val sizeKB = file.length() / 1024.0
            tvFileSize.text = when {
                sizeKB < 1024 -> "%.1f KB".format(sizeKB)
                else -> "%.1f MB".format(sizeKB / 1024.0)
            }
            
            // Fecha
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            tvFileDate.text = dateFormat.format(Date(file.lastModified()))
            
            // Icono según extensión
            iconFile.setImageResource(getFileIcon(displayName))
            
            // Click handlers
            itemView.setOnClickListener { onFileClick(file) }
            btnDelete.setOnClickListener { onFileDelete(file) }
            btnExport.setOnClickListener { onFileExport(file) }
        }
        
        private fun getFileIcon(fileName: String): Int {
            return when (fileName.substringAfterLast('.', "").lowercase()) {
                "jpg", "jpeg", "png", "gif", "bmp" -> R.drawable.ic_image
                "pdf" -> R.drawable.ic_pdf
                "doc", "docx", "txt" -> R.drawable.ic_document
                "mp3", "wav", "m4a" -> R.drawable.ic_audio
                "mp4", "avi", "mkv" -> R.drawable.ic_video
                "zip", "rar", "7z" -> R.drawable.ic_archive
                else -> R.drawable.ic_file
            }
        }
    }

    class FileDiffCallback : DiffUtil.ItemCallback<File>() {
        override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem.absolutePath == newItem.absolutePath
        }

        override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem.lastModified() == newItem.lastModified() &&
                   oldItem.length() == newItem.length()
        }
    }
}
