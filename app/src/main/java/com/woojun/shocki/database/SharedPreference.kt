package com.woojun.shocki.database

import android.content.Context
import android.content.SharedPreferences

object SharedPreference {
    private const val NAME = "Shocki SharedPreference"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    private val WALLET_ADDRESS = Pair("walletAddress", "")
    private val IS_TEST_MODE = Pair("isTestMode", false)

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var walletAddress: String
        get() = preferences.getString(WALLET_ADDRESS.first, WALLET_ADDRESS.second) ?: ""
        set(value) = preferences.edit {
            it.putString(WALLET_ADDRESS.first, value)
        }
    var isTestMode: Boolean
        get() = preferences.getBoolean(IS_TEST_MODE.first, IS_TEST_MODE.second)
        set(value) = preferences.edit {
            it.putBoolean(IS_TEST_MODE.first, value)
        }
}