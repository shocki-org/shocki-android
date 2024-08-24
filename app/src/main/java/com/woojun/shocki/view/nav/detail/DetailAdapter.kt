package com.woojun.shocki.view.nav.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.woojun.shocki.databinding.StoreDetailItemBinding

class DetailAdapter(private val imageList: List<Int>):
    RecyclerView.Adapter<DetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailAdapter.ViewHolder {
        val binding = StoreDetailItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding).also { handler ->

        }
    }

    override fun onBindViewHolder(holder: DetailAdapter.ViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    override fun getItemCount(): Int = imageList.size

    inner class ViewHolder(private val binding : StoreDetailItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(image: Int){
            binding.imageView.setImageResource(image)
        }
    }
}