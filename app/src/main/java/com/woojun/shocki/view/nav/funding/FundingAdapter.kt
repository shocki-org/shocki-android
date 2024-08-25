package com.woojun.shocki.view.nav.funding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.woojun.shocki.R
import com.woojun.shocki.data.Category
import com.woojun.shocki.databinding.FundingItemBinding

class FundingAdapter (private val categoryList: List<Category>):
    RecyclerView.Adapter<FundingAdapter.ViewHolder>() {
    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FundingAdapter.ViewHolder {
        val binding = FundingItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding).also { handler ->
            binding.root.setOnClickListener {
                categoryList[selectedPosition].isSelected = false
                notifyItemChanged(selectedPosition)

                selectedPosition = handler.adapterPosition
                categoryList[handler.adapterPosition].isSelected = true
                notifyItemChanged(selectedPosition)
            }
        }
    }

    override fun onBindViewHolder(holder: FundingAdapter.ViewHolder, position: Int) {
        holder.bind(categoryList[position])
    }

    override fun getItemCount(): Int = categoryList.size

    inner class ViewHolder(private val binding : FundingItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(category : Category){
            binding.categoryText.text = category.name
            val resources = binding.root.resources
            if (category.isSelected) {
                binding.root.apply {
                    this.setCardBackgroundColor(resources.getColor(R.color.background_accent_Default))
                    this.strokeColor = resources.getColor(R.color.background_accent_Default)
                }
                binding.categoryText.apply {
                    this.setTextColor(resources.getColor(R.color.white))
                    this.typeface = getResources().getFont(R.font.wanted_semibold)
                }
            } else {
                binding.root.apply {
                    this.setCardBackgroundColor(resources.getColor(R.color.background))
                    this.strokeColor = resources.getColor(R.color.background_gray_Border)
                }
                binding.categoryText.apply {
                    this.setTextColor(resources.getColor(R.color.Text_Status_Unselected))
                    this.typeface = getResources().getFont(R.font.wanted_medium)
                }
            }
        }
    }
}