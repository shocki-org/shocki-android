package com.woojun.shocki.data

data class Chat (
    val text: String,
    val type: ChatType,
    val productId: String? = null
)