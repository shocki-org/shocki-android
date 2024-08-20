package com.woojun.shocki.util

import android.util.Patterns
import java.util.regex.Pattern

object Util {
    fun checkEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun checkPassword(password: String): Boolean {
        val pwPattern = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9$@$!%*#?&]{8,20}$"
        return Pattern.matches(pwPattern, password)
    }
}