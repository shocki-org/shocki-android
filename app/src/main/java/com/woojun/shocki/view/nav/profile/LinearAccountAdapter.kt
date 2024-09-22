package com.woojun.shocki.view.nav.profile

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.woojun.shocki.R
import com.woojun.shocki.data.TokenBalance
import com.woojun.shocki.databinding.LinearAccountItemBinding
import com.woojun.shocki.view.main.MainActivity

class LinearAccountAdapter(private val list: List<TokenBalance>):
    RecyclerView.Adapter<LinearAccountAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinearAccountAdapter.ViewHolder {
        val binding = LinearAccountItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding).also { handler ->
            binding.root.setOnClickListener {
                (binding.root.context as MainActivity).animationNavigate(R.id.funding_detail, list[handler.adapterPosition].productId)
            }
        }
    }

    override fun onBindViewHolder(holder: LinearAccountAdapter.ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(private val binding: LinearAccountItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TokenBalance) {
            binding.titleText.text = item.productName

            binding.creditText.apply {
                val fullText = "총 ${item.tokenAmount}개를 가지고 있어요"
                val spannableString = SpannableString(fullText)

                val start = fullText.indexOf(item.tokenAmount.toString())
                val end = start + item.tokenAmount.toString().length+1

                spannableString.setSpan(
                    ForegroundColorSpan(binding.root.context.resources.getColor(R.color.Text_Status_Accent)),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                text = spannableString
            }
            Glide
                .with(binding.root.context)
                .load(item.productImage)
                .centerCrop()
                .into(binding.thumbnailIcon)
        }
    }
}