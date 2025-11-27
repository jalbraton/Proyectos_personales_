package com.securevault.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.securevault.R
import com.securevault.storage.FileEntry
import com.securevault.utils.formatAsFileSize

/**
 * Adapter para mostrar la lista de archivos en el RecyclerView
 */
class FilesAdapter(
    private val onExtractClick: (FileEntry) -> Unit,
    private val onDeleteClick: (FileEntry) -> Unit
) : ListAdapter<FileEntry, FilesAdapter.FileViewHolder>(FileDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_file, parent, false)
        return FileViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvFileName: TextView = itemView.findViewById(R.id.tvFileName)
        private val tvFileSize: TextView = itemView.findViewById(R.id.tvFileSize)
        private val btnExtract: ImageButton = itemView.findViewById(R.id.btnExtract)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
        
        fun bind(file: FileEntry) {
            tvFileName.text = file.name
            tvFileSize.text = file.size.formatAsFileSize()
            
            btnExtract.setOnClickListener {
                onExtractClick(file)
            }
            
            btnDelete.setOnClickListener {
                onDeleteClick(file)
            }
        }
    }
    
    private class FileDiffCallback : DiffUtil.ItemCallback<FileEntry>() {
        override fun areItemsTheSame(oldItem: FileEntry, newItem: FileEntry): Boolean {
            return oldItem.name == newItem.name
        }
        
        override fun areContentsTheSame(oldItem: FileEntry, newItem: FileEntry): Boolean {
            return oldItem == newItem
        }
    }
}
