package com.rb.filepicker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Switch
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rb.filepicker.databinding.ListItemImgBinding
import java.io.File

class GalleryAdapter(private val context:Context, private val fileArray: Array<File>) :
    RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    class ViewHolder(private val context : Context, private val fileArray : Array<File>, private val binding: ListItemImgBinding) :
        RecyclerView.ViewHolder(binding.root) , OnClickListener{
            init {
                binding.ivSelect.setOnClickListener(this)
            }
        fun bind(file: File) {
            Glide.with(binding.root).load(file).into(binding.localImg)
        }

        override fun onClick(v: View?) {
           when(v!!.id){
               binding.ivSelect.id -> {
//                   Toast.makeText(context, "${fileArray[adapterPosition].absolutePath}", Toast.LENGTH_LONG).show()
//                   Toast.makeText(context, "${fileArray[adapterPosition].name}", Toast.LENGTH_LONG).show()
//                   Toast.makeText(context, "${fileArray[adapterPosition].isFile}", Toast.LENGTH_LONG).show()
//                   Toast.makeText(context, "${fileArray[adapterPosition].path}", Toast.LENGTH_LONG).show()
               }
            else ->{}
           }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(context, fileArray, ListItemImgBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(fileArray[position])
    }

    override fun getItemCount(): Int {
        return fileArray.size
    }
}