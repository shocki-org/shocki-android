package com.woojun.shocki.view.nav.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.woojun.shocki.R
import com.woojun.shocki.databinding.NotificationItemBinding
import com.woojun.shocki.dto.AlertResponse

class NotificationAdapter (private val notificationList: List<AlertResponse>):
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationAdapter.ViewHolder {
        val binding = NotificationItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationAdapter.ViewHolder, position: Int) {
        holder.bind(notificationList[position])
    }

    override fun getItemCount(): Int = notificationList.size

    inner class ViewHolder(private val binding : NotificationItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item : AlertResponse){
            val resources = binding.root.resources

            val (INFO, PAYBACK, FAILURE, SYSTEM) = listOf("INFO", "PAYBACK", "FAILURE", "SYSTEM")

            val color = when (item.type) {
                INFO -> {
                    resources.getColor(R.color.Text_Status_Accent)
                }
                PAYBACK -> {
                    resources.getColor(R.color.Text_Status_Positive)
                }
                FAILURE -> {
                    resources.getColor(R.color.Text_Status_Negative)
                }
                SYSTEM -> {
                    resources.getColor(R.color.Text_Status_Warning)
                }
                else -> {
                    resources.getColor(R.color.Text_Status_Accent)
                }
            }
            binding.typeText.apply {
                this.text = item.title
                this.setTextColor(color)
            }
            binding.typeImage.setColorFilter(color)
            binding.dateText.text = item.date
            binding.contentText.text = item.content
        }
    }
}