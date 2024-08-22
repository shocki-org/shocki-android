package com.woojun.shocki.view.nav.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.woojun.shocki.data.Banner
import com.woojun.shocki.data.BannerType
import com.woojun.shocki.databinding.GridBannerItemBinding
import com.woojun.shocki.databinding.LinearBannerItemBinding
import com.woojun.shocki.databinding.TopBannerItemBinding

class BannerViewPagerAdapter(
    private val bannerList: List<Banner>,
    private val bannerType: BannerType
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when (bannerType) {
            BannerType.Top -> 1
            BannerType.Linear -> 2
            BannerType.Grid -> 3
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> {
                val binding = TopBannerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TopBannerViewHolder(binding)
            }
            2 -> {
                val binding = LinearBannerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LinearBannerViewHolder(binding)
            }
            3 -> {
                val binding = GridBannerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                GridViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = bannerList[position % bannerList.size]
        when (holder) {
            is TopBannerViewHolder -> holder.bind(item)
            is LinearBannerViewHolder -> holder.bind(item)
            is GridViewHolder -> holder.bind(item)
        }
    }

    override fun getItemCount(): Int = Int.MAX_VALUE

    inner class TopBannerViewHolder(private val binding: TopBannerItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Banner) {
            binding.textView.text = item.text
            binding.image.setImageResource(item.image)
            binding.background.background.alpha = (0.4 * 255).toInt()
        }
    }

    inner class LinearBannerViewHolder(private val binding: LinearBannerItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Banner) {
            binding.nameText.text = item.text
            binding.priceText.text = item.price
            binding.imageView.setImageResource(item.image)
        }
    }

    inner class GridViewHolder(private val binding: GridBannerItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Banner) {
            binding.nameText.text = item.text
            binding.priceText.text = item.price
            binding.imageView.setImageResource(item.image)
        }
    }
}
