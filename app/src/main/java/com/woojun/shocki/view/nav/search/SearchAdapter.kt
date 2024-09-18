package com.woojun.shocki.view.nav.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.woojun.shocki.R
import com.woojun.shocki.data.SearchType
import com.woojun.shocki.databinding.SearchItemBinding
import com.woojun.shocki.dto.SearchResponse
import com.woojun.shocki.view.main.MainActivity

class SearchAdapter (private val searchList: List<SearchResponse>, private val searchType: SearchType):
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdapter.ViewHolder {
        val binding = SearchItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding).also { handler ->
            binding.root.setOnClickListener {
                val activity = (binding.root.context as MainActivity)
                val item = searchList[handler.adapterPosition]
                if (item.type == "SELLING") {
                    activity.animationNavigate(R.id.storeDetail, item.id)
                } else {
                    activity.animationNavigate(R.id.funding_detail, item.id)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: SearchAdapter.ViewHolder, position: Int) {
        holder.bind(searchList[position])
    }

    override fun getItemCount(): Int {
        return if (searchList.isEmpty()) {
            1
        } else {
            searchList.size
        }
    }

    inner class ViewHolder(private val binding : SearchItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: SearchResponse){
            when (searchType) {
                SearchType.Default -> {
                    binding.defaultTitleText.apply {
                        text = "위 입력창을 통해"
                        visibility = View.VISIBLE
                    }
                    binding.defaultBodyText.apply {
                        text = "찾고 싶은 상품을 입력해주세요"
                        visibility = View.VISIBLE
                    }
                    binding.titleText.visibility = View.GONE
                    binding.creditText.visibility = View.GONE
                }
                SearchType.None -> {
                    binding.defaultIcon.setImageResource(R.drawable.null_search_icon)
                    binding.titleText.apply {
                        text = item.name
                        typeface = resources.getFont(R.font.wanted_semibold)
                        visibility = View.VISIBLE
                    }
                    binding.creditText.apply {
                        text = "라는 상품을 못찾았어요."
                        visibility = View.VISIBLE
                    }
                    binding.defaultTitleText.visibility = View.GONE
                    binding.defaultBodyText.visibility = View.GONE
                }
                SearchType.Item -> {
                    with(binding.thumbnailIcon) {
                        visibility = View.VISIBLE
                        Glide
                            .with(binding.root.context)
                            .load(item.image)
                            .centerCrop()
                            .into(this)
                    }
                    binding.titleText.apply {
                        text = item.name
                        typeface = resources.getFont(R.font.wanted_medium)
                        visibility = View.VISIBLE
                    }
                    binding.creditText.apply {
                        text = item.currentAmount.toString()
                        visibility = View.VISIBLE
                    }
                    binding.defaultTitleText.visibility = View.GONE
                    binding.defaultBodyText.visibility = View.GONE
                }

            }

        }
    }
}