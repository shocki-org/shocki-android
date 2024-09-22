package com.woojun.shocki.dto

import com.woojun.shocki.data.Settlement
import com.woojun.shocki.data.TokenBalance

data class AccountResponse(
    val credit: Int,
    val settlement: Settlement,
    val tokenBalances: List<TokenBalance>
)