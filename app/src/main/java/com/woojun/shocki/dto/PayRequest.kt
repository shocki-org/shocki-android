package com.woojun.shocki.dto

data class PayRequest(
    val amount: Int,
    val orderId: String,
    val paymentKey: String
)