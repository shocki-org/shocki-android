package com.woojun.shocki.network

import com.woojun.shocki.dto.PostLoginRequest
import com.woojun.shocki.dto.AccessTokenResponse
import com.woojun.shocki.dto.AccountResponse
import com.woojun.shocki.dto.AddressResponse
import com.woojun.shocki.dto.AlertResponse
import com.woojun.shocki.dto.CategoryResponse
import com.woojun.shocki.dto.FCMResponse
import com.woojun.shocki.dto.FavoriteResponse
import com.woojun.shocki.dto.MarketRequest
import com.woojun.shocki.dto.PayRequest
import com.woojun.shocki.dto.PhoneFinalRequest
import com.woojun.shocki.dto.PhoneFirstRequest
import com.woojun.shocki.dto.PhoneSecondRequest
import com.woojun.shocki.dto.PhoneSecondResponse
import com.woojun.shocki.dto.ProductResponse
import com.woojun.shocki.dto.QnaRequest
import com.woojun.shocki.dto.SaleTokenResponse
import com.woojun.shocki.dto.SearchResponse
import com.woojun.shocki.dto.ShippingResponse
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

    @GET("user/delivery")
    suspend fun getShippingList(
        @Header("Authorization") authorization: String,
    ): Response<List<ShippingResponse>>

    @GET("user/balance")
    suspend fun getAccount(
        @Header("Authorization") authorization: String,
    ): Response<AccountResponse>

    @GET("user/favorite")
    suspend fun getFavoriteList(
        @Header("Authorization") authorization: String,
    ): Response<List<FavoriteResponse>>

    @POST("user/pay")
    suspend fun postPay(
        @Header("Authorization") authorization: String,
        @Body body: PayRequest
    ): Response<Unit>

    @GET("address/search")
    suspend fun searchAddress(
        @Header("Authorization") authorization: String,
        @Query("query") query: String
    ): Response<AddressResponse>

    @POST("product/purchase/market")
    suspend fun buyMarket(
        @Header("Authorization") authorization: String,
        @Body body: MarketRequest
    ): Response<Unit>

    @PUT("product/favorite")
    suspend fun putLike(
        @Header("Authorization") authorization: String,
        @Query("productId") productId: String
    ): Response<Unit>

    @PUT("product/unfavorite")
    suspend fun putUnLike(
        @Header("Authorization") authorization: String,
        @Query("productId") productId: String
    ): Response<Unit>

    @POST("product/qna")
    suspend fun postQnA(
        @Header("Authorization") authorization: String,
        @Body body: QnaRequest
    ): Response<Unit>

    @POST("product/purchase/token")
    suspend fun buyToken(
        @Header("Authorization") authorization: String,
        @Query("productId") productId: String,
        @Query("amount") amount: String
    ): Response<Unit>

    @POST("product/sale/token")
    suspend fun saleToken(
        @Header("Authorization") authorization: String,
        @Query("productId") productId: String,
    ): Response<SaleTokenResponse>
}