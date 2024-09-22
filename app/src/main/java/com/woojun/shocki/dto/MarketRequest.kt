package com.woojun.shocki.dto

data class MarketRequest(
    val address: String,
    val amount: Int,
    val phone: String,
    val productId: String
)