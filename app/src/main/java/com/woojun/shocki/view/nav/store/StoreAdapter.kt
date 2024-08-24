package com.woojun.shocki.view.nav.store

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.woojun.shocki.R
import com.woojun.shocki.data.Banner
import com.woojun.shocki.databinding.StoreItemBinding
import com.woojun.shocki.view.main.MainActivity

class StoreAdapter (private val storeList: List<Banner>):
    RecyclerView.Adapter<StoreAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreAdapter.ViewHolder {
        val binding = StoreItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding).also { handler ->
            binding.root.setOnClickListener {
                (binding.root.context as MainActivity).animationNavigate(R.id.storeDetail)
            }
        }
    }

    override fun onBindViewHolder(holder: StoreAdapter.ViewHolder, position: Int) {
        holder.bind(storeList[position])
    }

    override fun getItemCount(): Int = storeList.size

    inner class ViewHolder(private val binding : StoreItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: Banner) {
            binding.nameText.text = item.text
            binding.priceText.text = item.price
            binding.imageView.setImageResource(item.image)
        }
    }
}