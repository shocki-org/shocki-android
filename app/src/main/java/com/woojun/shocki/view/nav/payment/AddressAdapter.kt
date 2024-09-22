package com.woojun.shocki.view.nav.payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.woojun.shocki.data.Document
import com.woojun.shocki.databinding.AddressItemBinding

class AddressAdapter(private val list: List<Document>, private val itemClick: ItemClick):
    RecyclerView.Adapter<AddressAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressAdapter.ViewHolder {
        val binding = AddressItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding).also { handler ->
            binding.root.setOnClickListener {
                val address = list[handler.adapterPosition]
                val textToShow = address.roadAddressName.ifBlank { address.addressName }
                itemClick.itemClick(textToShow)
            }
        }
    }

    override fun onBindViewHolder(holder: AddressAdapter.ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(private val binding: AddressItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(address: Document) {
            binding.titleText.text = address.placeName

            if (address.roadAddressName.isNotBlank() || address.addressName.isNotBlank()) {
                val textToShow = address.roadAddressName.ifBlank {
                    address.addressName
                }
                binding.bodyText.text = textToShow
            } else {
                binding.bodyText.visibility = View.GONE
            }
        }
    }

    interface ItemClick {
        fun itemClick(item: String)
    }
}