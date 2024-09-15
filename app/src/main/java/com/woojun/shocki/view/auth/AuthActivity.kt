package com.woojun.shocki.view.auth

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavOptions.Builder
import androidx.navigation.findNavController
import com.woojun.shocki.R
import com.woojun.shocki.databinding.ActivityAuthBinding
import com.woojun.shocki.util.BaseActivity

class AuthActivity : BaseActivity() {
    private lateinit var binding: ActivityAuthBinding
    private lateinit var navController: NavController

    companion object {
        const val KAKAO = "KAKAO"
        const val GOOGLE = "GOOGLE"
        const val PHONE = "PHONE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.nav_host_fragment)
    }

    fun animationNavigate(id: Int) {
        val navOptions = Builder()
            .setEnterAnim(R.anim.anim_slide_in_from_right_fade_in)
            .setExitAnim(R.anim.anim_fade_out)
            .setPopEnterAnim(R.anim.anim_slide_in_from_left_fade_in)
            .setPopExitAnim(R.anim.anim_fade_out)
            .build()

        navController.navigate(id, null, navOptions)
    }

    override fun getNavController(): NavController {
        return navController
    }

}