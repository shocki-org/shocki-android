package com.woojun.shocki.view.nav.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.woojun.shocki.R
import com.woojun.shocki.data.BannerType
import com.woojun.shocki.databinding.GridBannerItemBinding
import com.woojun.shocki.databinding.LinearBannerItemBinding
import com.woojun.shocki.databinding.TopBannerItemBinding
import com.woojun.shocki.dto.SimpleProductResponse
import com.woojun.shocki.util.Util.formatAmount
import com.woojun.shocki.view.main.MainActivity

class BannerViewPagerAdapter(
    private var bannerList: List<SimpleProductResponse>,
    private val bannerType: BannerType,
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
                TopBannerViewHolder(binding).also { handler ->
                    binding.root.setOnClickListener {
                        (binding.root.context as MainActivity).animationNavigate(R.id.funding_detail, bannerList[handler.adapterPosition % bannerList.size].id)
                    }
                }
            }
            2 -> {
                val binding = LinearBannerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LinearBannerViewHolder(binding).also { handler ->
                    binding.root.setOnClickListener {
                        (binding.root.context as MainActivity).animationNavigate(R.id.funding_detail, bannerList[handler.adapterPosition].id)
                    }
                }
            }
            3 -> {
                val binding = GridBannerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                GridViewHolder(binding).also { handler ->
                    binding.root.setOnClickListener {
                        (binding.root.context as MainActivity).animationNavigate(R.id.funding_detail, bannerList[handler.adapterPosition].id)
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

    fun changeDataSet(list: List<SimpleProductResponse>) {
        bannerList = list
        notifyDataSetChanged()
    }

    inner class TopBannerViewHolder(private val binding: TopBannerItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SimpleProductResponse) {
            binding.textView.text = item.name
            binding.background.background.alpha = (0.4 * 255).toInt()
            Glide
                .with(binding.root.context)
                .load(item.image)
                .centerCrop()
                .into(binding.image)
        }
    }

    inner class LinearBannerViewHolder(private val binding: LinearBannerItemBinding) : RecyclerView.ViewHolder(binding.root) {
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

    inner class GridViewHolder(private val binding: GridBannerItemBinding) : RecyclerView.ViewHolder(binding.root) {
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
