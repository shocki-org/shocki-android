package com.woojun.shocki.view.nav.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.woojun.shocki.data.Banner
import com.woojun.shocki.databinding.BannerItemBinding

class BannerViewPagerAdapter (private val bannerList: List<Banner>):
    RecyclerView.Adapter<BannerViewPagerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewPagerAdapter.ViewHolder {
        val binding = BannerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannerViewPagerAdapter.ViewHolder, position: Int) {
        holder.bind(bannerList[position % bannerList.size])
    }

    override fun getItemCount(): Int = Int.MAX_VALUE

    inner class ViewHolder(private val binding : BannerItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item : Banner){
            binding.textView.text = item.text
            binding.image.setImageResource(item.image)
            binding.background.background.alpha = (0.4 * 255).toInt()
        }
    }
}