package com.woojun.shocki.view.nav.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.woojun.shocki.R
import com.woojun.shocki.data.Notification
import com.woojun.shocki.data.NotificationColor
import com.woojun.shocki.databinding.NotificationItemBinding

class NotificationAdapter (private val notificationList: List<Notification>):
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
        fun bind(item : Notification){
            val resources = binding.root.resources
            val color = when (item.notificationColor) {
                NotificationColor.Red -> resources.getColor(R.color.Text_Status_Negative)
                NotificationColor.Green -> resources.getColor(R.color.Text_Status_Accent)
                NotificationColor.Blue -> resources.getColor(R.color.Text_Status_Positive)
            }
            binding.typeText.apply {
                this.text = item.notificationText
                this.setTextColor(color)
            }
            binding.typeImage.setColorFilter(color)
            binding.dateText.text = item.date
            binding.contentText.text = item.content
        }
    }
}