package com.woojun.shocki.data

data class Settlement(
    val settlementProducts: List<SettlementProduct>,
    val totalSettlementAmount: Int
)