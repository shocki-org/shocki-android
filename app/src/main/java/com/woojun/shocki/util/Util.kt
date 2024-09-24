package com.woojun.shocki.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.woojun.shocki.database.SharedPreference.isTestMode
import com.woojun.shocki.database.TokenManager
import com.woojun.shocki.dto.AccessTokenResponse
import com.woojun.shocki.dto.PostLoginRequest
import com.woojun.shocki.dto.ProductResponse
import com.woojun.shocki.network.RetrofitAPI
import com.woojun.shocki.network.RetrofitClient
import com.woojun.shocki.view.main.MainActivity
import com.woojun.shocki.view.wallet.WalletActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.regex.Pattern

object Util {
    fun checkPhone(phone: String): Boolean {
        var returnValue = false
        val regex = "^\\s*(010|011|012|013|014|015|016|017|018|019)(-|\\)|\\s)*(\\d{3,4})(-|\\s)*(\\d{4})\\s*$"
        val p = Pattern.compile(regex)

        val m = p.matcher(phone)

        if (m.matches()) {
            returnValue = true
        }

        return returnValue
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

    fun saveToken(context: Activity, postLoginRequest: PostLoginRequest, isTest: Boolean = false) {
        CoroutineScope(Dispatchers.Main).launch {
            val signInResponses = postSignIn(context, postLoginRequest)
            if (signInResponses != null) {
                TokenManager.accessToken = signInResponses.accessToken
                if (isTest) {
                    isTestMode = true
                    context.startActivity(Intent(context, MainActivity::class.java))
                } else {
                    context.startActivity(Intent(context, WalletActivity::class.java))
                }
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

    suspend fun getProduct(id: String): ProductResponse? {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.getProduct("bearer ${TokenManager.accessToken}", id)
                if (response.isSuccessful) {
                    response.body()
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    fun formatAmount(value: Number): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
        return numberFormat.format(value)
    }
}