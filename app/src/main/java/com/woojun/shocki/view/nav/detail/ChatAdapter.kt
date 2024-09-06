package com.woojun.shocki.view.nav.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.woojun.shocki.R
import com.woojun.shocki.data.Chat
import com.woojun.shocki.data.ChatType
import com.woojun.shocki.databinding.ChatItemBinding
import com.woojun.shocki.view.main.MainActivity

class ChatAdapter(private val chatList: List<Chat>):
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ViewHolder {
        val binding = ChatItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding).also { handler ->
            binding.root.setOnClickListener {
                if (handler.adapterPosition == chatList.size.dec()) {
                    (binding.root.context as MainActivity).animationNavigate(R.id.qnaFragment)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ChatAdapter.ViewHolder, position: Int) {
        holder.bind(chatList[position])
    }

    override fun getItemCount(): Int = chatList.size

    inner class ViewHolder(private val binding : ChatItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(chat: Chat){
            when (chat.type) {
                ChatType.Question -> {
                    binding.chatBox.isVisible = true
                    binding.otherChatBox.isVisible = false
                    binding.addChatButton.isVisible = false

                    binding.chatText.text = chat.text
                }
                ChatType.Answer -> {
                    binding.otherChatBox.isVisible = true
                    binding.chatBox.isVisible = false
                    binding.addChatButton.isVisible = false

                    binding.otherChatText.text = chat.text
                }
                ChatType.Button -> {
                    binding.otherChatBox.isVisible = false
                    binding.chatBox.isVisible = false

                    binding.addChatButton.apply {
                        isVisible = true
                    }
                }
            }
        }
    }
}