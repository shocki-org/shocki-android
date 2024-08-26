package com.woojun.shocki.view.main

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.woojun.shocki.R
import androidx.navigation.NavOptions.Builder
import com.woojun.shocki.databinding.ActivityMainBinding
import com.woojun.shocki.util.BaseActivity


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private var recentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

        navController = findNavController(R.id.nav_host_fragment)

        binding.explore.setOnClickListener { animationNavigate(R.id.explore) }
        binding.store.setOnClickListener { animationNavigate(R.id.store) }
        binding.funding.setOnClickListener { animationNavigate(R.id.funding) }
        binding.profile.setOnClickListener { animationNavigate(R.id.profile) }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            resetNavigationItem()
            setStatusBar(destination.id)
            when (destination.id) {
                R.id.explore -> {
                    binding.exploreIcon.setColorFilter(ContextCompat.getColor(this, R.color.Text_Default_Primary))
                    binding.exploreText.setTextColor(ContextCompat.getColor(this, R.color.Text_Default_Primary))
                    binding.explore.isEnabled = false

                    navigationMode(false)
                }
                R.id.store -> {
                    binding.storeIcon.setColorFilter(ContextCompat.getColor(this, R.color.Text_Default_Primary))
                    binding.storeText.setTextColor(ContextCompat.getColor(this, R.color.Text_Default_Primary))
                    binding.store.isEnabled = false

                    navigationMode(false)
                }
                R.id.funding -> {
                    binding.fundingIcon.setColorFilter(ContextCompat.getColor(this, R.color.Text_Default_Primary))
                    binding.fundingText.setTextColor(ContextCompat.getColor(this, R.color.Text_Default_Primary))
                    binding.funding.isEnabled = false

                    navigationMode(false)
                }
                R.id.profile -> {
                    binding.profileIcon.setColorFilter(ContextCompat.getColor(this, R.color.Text_Default_Primary))
                    binding.profileText.setTextColor(ContextCompat.getColor(this, R.color.Text_Default_Primary))
                    binding.profile.isEnabled = false

                    navigationMode(false)
                }
                else -> {
                    navigationMode(true)
                }
            }

        }
    }

    fun animationNavigate(id: Int) {
        fun getNavOptions(enterAnim: Int, clearBackStack: Boolean): NavOptions {
            val builder = Builder()
                .setEnterAnim(enterAnim)
                .setExitAnim(R.anim.anim_fade_out)
                .setPopEnterAnim(R.anim.anim_slide_in_from_left_fade_in)
                .setPopExitAnim(R.anim.anim_fade_out)

            if (clearBackStack) {
                builder.setPopUpTo(R.id.explore, true)
            }

            return builder.build()
        }

        val (newPosition, enterAnim, clearBackStack) = when (id) {
            R.id.explore -> Triple(0, R.anim.anim_slide_in_from_left_fade_in, true)
            R.id.store -> Triple(1,
                if (recentPosition < 1) R.anim.anim_slide_in_from_right_fade_in else R.anim.anim_slide_in_from_left_fade_in,
                true)
            R.id.funding -> Triple(2,
                if (recentPosition < 2) R.anim.anim_slide_in_from_right_fade_in else R.anim.anim_slide_in_from_left_fade_in,
                true)
            R.id.profile -> Triple(3, R.anim.anim_slide_in_from_right_fade_in, true)
            else -> Triple(recentPosition, R.anim.anim_slide_in_from_right_fade_in, false)
        }

        val navOptions = getNavOptions(enterAnim, clearBackStack)
        recentPosition = newPosition
        navController.navigate(id, null, navOptions)
    }

    private fun setStatusBar(id: Int) {
        if (id !in listOf(R.id.explore, R.id.funding_detail, R.id.storeDetail)) {
            val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val controller = window.insetsController
                if (controller != null) {
                    if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                        controller.setSystemBarsAppearance(
                            0,
                            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                        )
                    } else {
                        controller.setSystemBarsAppearance(
                            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                        )
                    }
                }
            } else {
                if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                    @Suppress("DEPRECATION")
                    window.decorView.systemUiVisibility =
                        window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                } else {
                    @Suppress("DEPRECATION")
                    window.decorView.systemUiVisibility =
                        window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
            }
            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        } else {
            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
                insets
            }
        }

    }

    override fun getNavController(): NavController {
        return navController
    }

    private fun navigationMode(isGone: Boolean) {
        if (isGone) {
            binding.bottomNavigationLine.visibility = View.GONE
            binding.bottomNavigationBar.visibility = View.GONE
        } else {
            binding.bottomNavigationLine.visibility = View.VISIBLE
            binding.bottomNavigationBar.visibility = View.VISIBLE
        }
    }

    private fun resetNavigationItem() {
        binding.exploreIcon.setColorFilter(ContextCompat.getColor(this, R.color.Text_Status_Unselected))
        binding.exploreText.setTextColor(ContextCompat.getColor(this, R.color.Text_Status_Unselected))

        binding.storeIcon.setColorFilter(ContextCompat.getColor(this, R.color.Text_Status_Unselected))
        binding.storeText.setTextColor(ContextCompat.getColor(this, R.color.Text_Status_Unselected))

        binding.fundingIcon.setColorFilter(ContextCompat.getColor(this, R.color.Text_Status_Unselected))
        binding.fundingText.setTextColor(ContextCompat.getColor(this, R.color.Text_Status_Unselected))

        binding.profileIcon.setColorFilter(ContextCompat.getColor(this, R.color.Text_Status_Unselected))
        binding.profileText.setTextColor(ContextCompat.getColor(this, R.color.Text_Status_Unselected))

        binding.explore.isEnabled = true
        binding.store.isEnabled = true
        binding.funding.isEnabled = true
        binding.profile.isEnabled = true
    }



}