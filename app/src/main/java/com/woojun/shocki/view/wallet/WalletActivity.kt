package com.woojun.shocki.view.wallet

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavOptions.Builder
import androidx.navigation.findNavController
import com.woojun.shocki.R
import com.woojun.shocki.databinding.ActivityWalletBinding
import com.woojun.shocki.util.BaseActivity

class WalletActivity : BaseActivity() {

    private lateinit var binding: ActivityWalletBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletBinding.inflate(layoutInflater)
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