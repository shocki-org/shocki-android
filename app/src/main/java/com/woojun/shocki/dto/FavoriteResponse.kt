package com.woojun.shocki.dto

data class FavoriteResponse(
    val productId: String,
    val productImage: String,
    val productName: String,
    val productPrice: Int
)