package com.woojun.shocki.view.nav.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.woojun.shocki.R
import com.woojun.shocki.databinding.SearchItemBinding

class SearchAdapter (private val searchList: List<String>):
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdapter.ViewHolder {
        val binding = SearchItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding).also { handler ->

        }
    }

    override fun onBindViewHolder(holder: SearchAdapter.ViewHolder, position: Int) {
        holder.bind(searchList[position])
    }

    override fun getItemCount(): Int = searchList.size

    inner class ViewHolder(private val binding : SearchItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(title: String){
            if (title.isNotEmpty()) {
                binding.defaultIcon.setImageResource(R.drawable.null_search_icon)
                binding.titleText.apply {
                    this.text = title
                    this.setTextColor(resources.getColor(R.color.Text_Default_Primary))
                    this.typeface = resources.getFont(R.font.wanted_semibold)
                }
                binding.bodyText.text = "라는 상품을 못찾았어요."
            }
        }
    }
}