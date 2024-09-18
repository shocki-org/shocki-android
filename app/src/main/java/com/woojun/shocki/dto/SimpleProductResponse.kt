package com.woojun.shocki.dto

data class SimpleProductResponse(
    val currentAmount: Int,
    val id: String,
    val image: String,
    val name: String,
    val categoryId: String
)