package com.woojun.shocki.dto

data class PostLoginRequest(
    val accessToken: String,
    val password: String,
    val phone: String,
    val provider: String
)