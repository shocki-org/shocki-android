package com.woojun.shocki.data

data class SettlementProduct(
    val productId: String,
    val productImage: String,
    val productName: String,
    val settlementAmount: Int,
    val settlementDate: String
)