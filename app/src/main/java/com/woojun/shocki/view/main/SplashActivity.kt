package com.woojun.shocki.view.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.woojun.shocki.R
import com.woojun.shocki.database.SharedPreference
import com.woojun.shocki.database.TokenManager
import com.woojun.shocki.view.auth.AuthActivity
import com.woojun.shocki.view.wallet.WalletActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Thread.sleep

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        CoroutineScope(Dispatchers.IO).launch {
            val intent = if (TokenManager.accessToken == "") {
                Intent(this@SplashActivity, AuthActivity::class.java)
            } else if (SharedPreference.walletAddress == "" && !SharedPreference.isTestMode) {
                Intent(this@SplashActivity, WalletActivity::class.java)
            } else {
                Intent(this@SplashActivity, MainActivity::class.java)
            }
            
            sleep(2000)
            startActivity(intent)
            finishAffinity()
        }
    }
}