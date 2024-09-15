package com.woojun.shocki.util

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.woojun.shocki.BuildConfig
import com.woojun.shocki.database.TokenManager

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.init(this)
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_KEY)
    }
}