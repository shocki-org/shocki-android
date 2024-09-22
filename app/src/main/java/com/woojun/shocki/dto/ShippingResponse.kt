package com.woojun.shocki.dto

data class ShippingResponse(
    val productId: String,
    val productImage: String,
    val productName: String,
    val purchaseId: String,
    val status: String
)