package com.woojun.shocki.view.nav.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.woojun.shocki.databinding.ImageItemBinding

class ImageAdapter(private val imageList: List<Int>):
    RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageAdapter.ViewHolder {
        val binding = ImageItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding).also { handler ->

        }
    }

    override fun onBindViewHolder(holder: ImageAdapter.ViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    override fun getItemCount(): Int = imageList.size

    inner class ViewHolder(private val binding : ImageItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(image: Int){
            binding.imageView.setImageResource(image)
        }
    }
}