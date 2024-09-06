package com.woojun.shocki.data

sealed class ChatType {
    data object Question : ChatType()
    data object Answer : ChatType()
    data object Button : ChatType()
}