package com.woojun.shocki.view.nav.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.woojun.shocki.R
import com.woojun.shocki.data.Banner
import com.woojun.shocki.data.BannerType
import com.woojun.shocki.databinding.GridBannerItemBinding
import com.woojun.shocki.databinding.LinearBannerItemBinding
import com.woojun.shocki.databinding.TopBannerItemBinding
import com.woojun.shocki.view.main.MainActivity

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
                GridViewHolder(binding).also { hanler->
                    binding.root.setOnClickListener {
                        (binding.root.context as MainActivity).animationNavigate(R.id.funding)
                    }
                }
            }
            else -> throw IllegalArgumentException("Invalid viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TopBannerViewHolder -> holder.bind(bannerList[position % bannerList.size])
            is LinearBannerViewHolder -> holder.bind(bannerList[position])
            is GridViewHolder -> holder.bind(bannerList[position])
        }
    }

    override fun getItemCount(): Int {
        return if (bannerType == BannerType.Top) {
            Int.MAX_VALUE
        } else {
            bannerList.size
        }
    }

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
