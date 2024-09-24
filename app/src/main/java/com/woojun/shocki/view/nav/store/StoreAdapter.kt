package com.woojun.shocki.view.nav.store

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.woojun.shocki.R
import com.woojun.shocki.databinding.StoreItemBinding
import com.woojun.shocki.dto.SimpleProductResponse
import com.woojun.shocki.util.Util.formatAmount
import com.woojun.shocki.view.main.MainActivity

class StoreAdapter (private val storeList: List<SimpleProductResponse>):
    RecyclerView.Adapter<StoreAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreAdapter.ViewHolder {
        val binding = StoreItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding).also { handler ->
            binding.root.setOnClickListener {
                (binding.root.context as MainActivity).animationNavigate(R.id.storeDetail, storeList[handler.adapterPosition].id)
            }
        }
    }

    override fun onBindViewHolder(holder: StoreAdapter.ViewHolder, position: Int) {
        holder.bind(storeList[position])
    }

    override fun getItemCount(): Int = storeList.size

    inner class ViewHolder(private val binding : StoreItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: SimpleProductResponse) {
            binding.nameText.text = item.name
            binding.priceText.text = formatAmount(item.currentAmount)
            Glide
                .with(binding.root.context)
                .load(item.image)
                .centerCrop()
                .into(binding.imageView)
        }
    }
}