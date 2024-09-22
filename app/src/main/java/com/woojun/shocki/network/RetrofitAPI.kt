package com.woojun.shocki.network

import com.woojun.shocki.dto.PostLoginRequest
import com.woojun.shocki.dto.AccessTokenResponse
import com.woojun.shocki.dto.AlertResponse
import com.woojun.shocki.dto.CategoryResponse
import com.woojun.shocki.dto.FCMResponse
import com.woojun.shocki.dto.PhoneFinalRequest
import com.woojun.shocki.dto.PhoneFirstRequest
import com.woojun.shocki.dto.PhoneSecondRequest
import com.woojun.shocki.dto.PhoneSecondResponse
import com.woojun.shocki.dto.ProductResponse
import com.woojun.shocki.dto.SearchResponse
import com.woojun.shocki.dto.SimpleProductResponse
import com.woojun.shocki.dto.WalletRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

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
    ): Response<List<AlertResponse>>

    @GET("product/search")
    suspend fun getSearch(
        @Header("Authorization") authorization: String,
        @Query("keyword") keyword: String
    ): Response<List<SearchResponse>>

    @GET("product/list")
    suspend fun getProductList(
        @Header("Authorization") authorization: String,
        @Query("type") type: String
    ): Response<List<SimpleProductResponse>>

    @GET("category")
    suspend fun getCategoryList(
        @Header("Authorization") authorization: String,
    ): Response<List<CategoryResponse>>

    @PUT("user/fcm")
    suspend fun setFCMToken(
        @Header("Authorization") authorization: String,
        @Body body: FCMResponse
    ): Response<Unit>

    @GET("product")
    suspend fun getProduct(
        @Header("Authorization") authorization: String,
        @Query("id") id: String
    ): Response<ProductResponse>

    @PUT("user/address")
    suspend fun setWallet(
        @Header("Authorization") authorization: String,
        @Body body: WalletRequest
    ): Response<Unit>
}