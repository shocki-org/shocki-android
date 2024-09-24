package com.woojun.shocki.view.nav.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.woojun.shocki.R
import com.woojun.shocki.data.SettlementProduct
import com.woojun.shocki.databinding.GridAccountItemBinding
import com.woojun.shocki.util.Util.formatAmount
import com.woojun.shocki.view.main.MainActivity
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class GridAccountAdapter(private val list: List<SettlementProduct>):
    RecyclerView.Adapter<GridAccountAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridAccountAdapter.ViewHolder {
        val binding = GridAccountItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding).also { handler ->
            binding.root.setOnClickListener {
                (binding.root.context as MainActivity).animationNavigate(R.id.funding_detail, list[handler.adapterPosition].productId)
            }
        }
    }

    override fun onBindViewHolder(holder: GridAccountAdapter.ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(private val binding: GridAccountItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SettlementProduct) {
            binding.nameText.text = item.productName
            binding.priceText.text = formatAmount(item.settlementAmount)
            binding.dateText.text = formatToDate(item.settlementDate)
            Glide
                .with(binding.root.context)
                .load(item.productImage)
                .centerCrop()
                .into(binding.imageView)
        }
        private fun formatToDate(dateString: String): String {
            val zoneKST = ZoneId.of("Asia/Seoul")
            val zonedDateTime = Instant.parse(dateString).atZone(zoneKST)

            val formatter = DateTimeFormatter.ofPattern("MM월 dd일")

            return zonedDateTime.format(formatter)
        }
    }
}