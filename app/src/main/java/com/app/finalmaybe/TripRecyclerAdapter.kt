package com.app.finalmaybe

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class TripAdapter(private val context: Context, private val filesAndFolders: Array<File>) :
    RecyclerView.Adapter<TripAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val selectedFile = filesAndFolders[position]
        holder.textView.text = selectedFile.name
        holder.itemView.setOnClickListener {
            if (selectedFile.isDirectory) {
                val intent = Intent(context, FileListActivity::class.java)
                val path = selectedFile.absolutePath
                intent.putExtra("path", path)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            } else {
                Log.d("MyAdapter", "Attempting to open file: " + selectedFile.absolutePath)
                try {
                    val fileUri = FileProvider.getUriForFile(context, "com.finalmaybe.fileprovider", selectedFile)
                    val intent = Intent(Intent.ACTION_VIEW)

                    intent.setDataAndType(fileUri, "application/pdf")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Log.e("MyAdapter", "Error opening file: " + e.message, e)
                    Toast.makeText(context, "Cannot open the file", Toast.LENGTH_SHORT).show()
                }
            }
        }
        holder.itemView.setOnClickListener { item->
            Log.d("WHATCLICKED","${(filesAndFolders[position]).name}")
            val tripster = context.applicationContext as Tripster
            tripster.currentFile = (filesAndFolders[position]).name


        }
        holder.itemView.setOnLongClickListener { v ->
            val popupMenu = PopupMenu(context, v)
            popupMenu.menu.add("DELETE")
            popupMenu.menu.add("MOVE")
            popupMenu.menu.add("RENAME")

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "DELETE" -> {
                        val deleted = selectedFile.deleteRecursively()
                        if (deleted) {
                            Toast.makeText(context, "DELETED", Toast.LENGTH_SHORT).show()
                            v.visibility = View.GONE
                        }
                    }
                    "MOVE" -> Toast.makeText(context, "MOVED", Toast.LENGTH_SHORT).show()
                    "RENAME" -> Toast.makeText(context, "RENAME", Toast.LENGTH_SHORT).show()
                }
                true
            }

            popupMenu.show()
            true
        }
    }

    override fun getItemCount(): Int {
        return filesAndFolders.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.file_name)
        val imageView: ImageView = itemView.findViewById(R.id.icon_view)
    }
}