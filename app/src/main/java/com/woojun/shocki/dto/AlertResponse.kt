package com.woojun.shocki.dto

data class AlertResponse(
    val content: String,
    val title: String,
    val type: String,
    val date: String
)