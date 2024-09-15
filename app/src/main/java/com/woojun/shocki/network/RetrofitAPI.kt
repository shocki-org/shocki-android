package com.woojun.shocki.network

import com.woojun.shocki.dto.PostLoginRequest
import com.woojun.shocki.dto.AccessTokenResponse
import com.woojun.shocki.dto.AlertResponse
import com.woojun.shocki.dto.PhoneFinalRequest
import com.woojun.shocki.dto.PhoneFirstRequest
import com.woojun.shocki.dto.PhoneSecondRequest
import com.woojun.shocki.dto.PhoneSecondResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface RetrofitAPI {

    @POST("register/login")
    suspend fun postLogin(
        @Body body: PostLoginRequest
    ): Response<AccessTokenResponse>

    @POST("register/phone/first")
    suspend fun phoneFirst(
        @Body body: PhoneFirstRequest
    ): Response<Unit>

    @POST("/register/phone/second")
    suspend fun phoneSecond(
        @Body body: PhoneSecondRequest
    ): Response<PhoneSecondResponse>

    @POST("register/phone/final")
    suspend fun phoneFinal(
        @Body body: PhoneFinalRequest
    ): Response<AccessTokenResponse>

    @DELETE("user/delete")
    suspend fun deleteUser(
        @Header("Authorization") authorization: String,
    ): Response<Unit>

    @GET("alert")
    suspend fun getAlert(
        @Header("Authorization") authorization: String,
    ): Response<Array<AlertResponse>>


}