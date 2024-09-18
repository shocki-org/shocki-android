package com.woojun.shocki.database

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val NAME = "Shocki"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    private val ACCESS_TOKEN = Pair("accessToken", "")
    private val FCM_TOKEN = Pair("fcmToken", "")

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var accessToken: String
        get() = preferences.getString(ACCESS_TOKEN.first, ACCESS_TOKEN.second) ?: ""
        set(value) = preferences.edit {
            it.putString(ACCESS_TOKEN.first, value)
        }

    var fcmToken: String
        get() = preferences.getString(FCM_TOKEN.first, FCM_TOKEN.second) ?: ""
        set(value) = preferences.edit {
            it.putString(FCM_TOKEN.first, value)
        }
}