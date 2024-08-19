package com.woojun.shocki.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.woojun.shocki.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.emailSignInButton.setOnClickListener { emailSignIn() }
        binding.kakaoLoginButton.setOnClickListener { kakaoLogin() }
        binding.googleLoginButton.setOnClickListener { googleLogin() }
        binding.emailLoginButton.setOnClickListener { emailLogin() }
    }

    private fun emailSignIn() {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    private fun kakaoLogin() {}

    private fun googleLogin() {}

    private fun emailLogin() {
        startActivity(Intent(this, LogInActivity::class.java))
    }

}