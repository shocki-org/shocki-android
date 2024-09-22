package com.woojun.shocki.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Patterns
import android.widget.Toast
import com.woojun.shocki.database.TokenManager
import com.woojun.shocki.dto.AccessTokenResponse
import com.woojun.shocki.dto.PostLoginRequest
import com.woojun.shocki.network.RetrofitAPI
import com.woojun.shocki.network.RetrofitClient
import com.woojun.shocki.view.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.regex.Pattern

object Util {
    fun checkPhone(phone: String): Boolean {
        return Patterns.PHONE.matcher(phone).matches()
    }

    fun checkPassword(password: String): Boolean {
        val pwPattern = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9$@$!%*#?&]{8,20}$"
        return Pattern.matches(pwPattern, password)
    }

    fun formatPhoneNumber(phone: String): String {
        return phone.replaceFirst("010", "+8210").replace("-", "")
    }

    private suspend fun postSignIn(context: Context, request: PostLoginRequest): AccessTokenResponse? {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.postLogin(request)
                if (response.isSuccessful) {
                    response.body()
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "로그인에 실패하였습니다", Toast.LENGTH_SHORT).show()
                    }
                    null
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "로그인에 실패하였습니다", Toast.LENGTH_SHORT).show()
            }
            null
        }
    }

    fun saveToken(context: Activity, postLoginRequest: PostLoginRequest) {
        CoroutineScope(Dispatchers.Main).launch {
            val signInResponses = postSignIn(context, postLoginRequest)
            if (signInResponses != null) {
                TokenManager.accessToken = signInResponses.accessToken
                context.startActivity(Intent(context, MainActivity::class.java))
                context.finishAffinity()
            } else {
                Toast.makeText(context, "오류 발생", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun calculateEndDate(endDateString: String): Long {
        val zoneKST = ZoneId.of("Asia/Seoul")
        val endDate = Instant.parse(endDateString).atZone(zoneKST).toLocalDate()

        val currentDate = LocalDate.now(zoneKST)
        val daysUntil = ChronoUnit.DAYS.between(currentDate, endDate)

        return daysUntil
    }
}