package com.woojun.shocki.dto

import com.woojun.shocki.data.Graph
import com.woojun.shocki.data.QnA

data class ProductResponse(
    val categories: List<String>,
    val collectedAmount: Int,
    val createdAt: String,
    val currentAmount: Int,
    val detailImages: List<String>,
    val distributionPercent: Int,
    val fundingEndDate: String,
    val graph: List<Graph>,
    val id: String,
    val image: String,
    val marketEndDate: String,
    val name: String,
    val ownerId: String,
    val productQnA: List<QnA>,
    val purchaseIsDisabled: Boolean,
    val startAmount: Int,
    val targetAmount: Int,
    val tokenAddress: String,
    val type: String,
    val userFavorite: Boolean
)