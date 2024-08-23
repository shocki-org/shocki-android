package com.woojun.shocki.view.main

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.woojun.shocki.R
import androidx.navigation.NavOptions.Builder
import com.woojun.shocki.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

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
        binding.myAssets.setOnClickListener { animationNavigate(R.id.my_assets) }
        binding.bookmark.setOnClickListener { animationNavigate(R.id.bookmark) }
        binding.profile.setOnClickListener { animationNavigate(R.id.profile) }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            resetNavigationItem()
            setStatusBar(destination.id)
            when (destination.id) {
                R.id.explore -> {
                    binding.exploreIcon.setColorFilter(ContextCompat.getColor(this, R.color.Text_Default_Primary))
                    binding.exploreText.setTextColor(ContextCompat.getColor(this, R.color.Text_Default_Primary))

                    navigationMode(false)
                }
                R.id.my_assets -> {
                    binding.myAssetsIcon.setColorFilter(ContextCompat.getColor(this, R.color.Text_Default_Primary))
                    binding.myAssetsText.setTextColor(ContextCompat.getColor(this, R.color.Text_Default_Primary))

                    navigationMode(false)
                }
                R.id.bookmark -> {
                    binding.bookmarkIcon.setColorFilter(ContextCompat.getColor(this, R.color.Text_Default_Primary))
                    binding.bookmarkText.setTextColor(ContextCompat.getColor(this, R.color.Text_Default_Primary))

                    navigationMode(false)
                }
                R.id.profile -> {
                    binding.profileIcon.setColorFilter(ContextCompat.getColor(this, R.color.Text_Default_Primary))
                    binding.profileText.setTextColor(ContextCompat.getColor(this, R.color.Text_Default_Primary))

                    navigationMode(false)
                }
                else -> {
                    navigationMode(true)
                }
            }

        }

    }

    fun animationNavigate(id: Int) {
        fun getNavOptions(enterAnim: Int): NavOptions {
            return Builder()
                .setEnterAnim(enterAnim)
                .setExitAnim(R.anim.anim_fade_out)
                .setPopEnterAnim(R.anim.anim_slide_in_from_left_fade_in)
                .setPopExitAnim(R.anim.anim_fade_out)
                .build()
        }

        val (newPosition, enterAnim) = when (id) {
            R.id.explore -> 0 to R.anim.anim_slide_in_from_left_fade_in
            R.id.my_assets -> if (recentPosition < 1) 1 to R.anim.anim_slide_in_from_right_fade_in else 1 to R.anim.anim_slide_in_from_left_fade_in
            R.id.bookmark -> if (recentPosition < 2) 2 to R.anim.anim_slide_in_from_right_fade_in else 2 to R.anim.anim_slide_in_from_left_fade_in
            R.id.profile -> 3 to R.anim.anim_slide_in_from_right_fade_in
            else -> recentPosition to R.anim.anim_slide_in_from_right_fade_in
        }

        val navOptions = getNavOptions(enterAnim)
        recentPosition = newPosition
        navController.navigate(id, null, navOptions)
    }

    private fun setStatusBar(id: Int) {
        if (id != R.id.explore) {
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

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
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

        binding.myAssetsIcon.setColorFilter(ContextCompat.getColor(this, R.color.Text_Status_Unselected))
        binding.myAssetsText.setTextColor(ContextCompat.getColor(this, R.color.Text_Status_Unselected))

        binding.bookmarkIcon.setColorFilter(ContextCompat.getColor(this, R.color.Text_Status_Unselected))
        binding.bookmarkText.setTextColor(ContextCompat.getColor(this, R.color.Text_Status_Unselected))

        binding.profileIcon.setColorFilter(ContextCompat.getColor(this, R.color.Text_Status_Unselected))
        binding.profileText.setTextColor(ContextCompat.getColor(this, R.color.Text_Status_Unselected))
    }



}