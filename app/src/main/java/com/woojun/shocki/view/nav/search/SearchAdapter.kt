package com.woojun.shocki.view.nav.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.woojun.shocki.R
import com.woojun.shocki.data.SearchType
import com.woojun.shocki.database.MainViewModel
import com.woojun.shocki.databinding.SearchDefaultItemBinding
import com.woojun.shocki.databinding.SearchItemBinding
import com.woojun.shocki.databinding.SearchNoneItemBinding
import com.woojun.shocki.dto.SearchResponse
import com.woojun.shocki.view.main.MainActivity

class SearchAdapter (private val searchList: List<SearchResponse>, private val searchType: SearchType) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when (searchType) {
            SearchType.Default -> 1
            SearchType.None -> 2
            SearchType.Item -> 3
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> {
                val binding = SearchDefaultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                DefaultViewHolder(binding)
            }
            2 -> {
                val binding = SearchNoneItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                NoneViewHolder(binding)
            }
            3 -> {
                val binding = SearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SearchViewHolder(binding).also { handler ->
                    binding.root.setOnClickListener {
                        val item = searchList[handler.adapterPosition]
                        if (item.type == MainViewModel.SELLING) {
                            (binding.root.context as MainActivity).animationNavigate(R.id.storeDetail, item.id)
                        } else {
                            (binding.root.context as MainActivity).animationNavigate(R.id.funding_detail, item.id)
                        }
                    }
                }
            }
            else -> throw IllegalArgumentException("Invalid viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NoneViewHolder -> holder.bind(searchList[position].name)
            is DefaultViewHolder ->holder.bind()
            is SearchViewHolder -> holder.bind(searchList[position])
        }
    }

    override fun getItemCount(): Int = searchList.size

    inner class NoneViewHolder(private val binding: SearchNoneItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(title: String) {
            binding.titleText.text = title
        }
    }

    inner class DefaultViewHolder(private val binding: SearchDefaultItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {

        }
    }

    inner class SearchViewHolder(private val binding: SearchItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SearchResponse) {
            binding.titleText.text = item.name
            binding.creditText.text = item.currentAmount.toString()
            Glide
                .with(binding.root.context)
                .load(item.image)
                .centerCrop()
                .into(binding.thumbnailIcon)
        }
    }
}