package com.woojun.shocki.view.main

import android.os.Bundle
import android.view.View
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
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        navController = findNavController(R.id.nav_host_fragment)

        binding.explore.setOnClickListener { animationNavigate(R.id.explore) }
        binding.myAssets.setOnClickListener { animationNavigate(R.id.my_assets) }
        binding.bookmark.setOnClickListener { animationNavigate(R.id.bookmark) }
        binding.profile.setOnClickListener { animationNavigate(R.id.profile) }

    }

    private fun animationNavigate(id: Int) {
        resetNavigationItem()

        fun getNavOptions(enterAnim: Int): NavOptions {
            return Builder()
                .setEnterAnim(enterAnim)
                .setExitAnim(R.anim.anim_fade_out)
                .build()
        }

        val (newPosition, enterAnim) = when (id) {
            R.id.explore -> 0 to R.anim.anim_slide_in_from_left_fade_in
            R.id.my_assets -> if (recentPosition < 1) 1 to R.anim.anim_slide_in_from_right_fade_in else 1 to R.anim.anim_slide_in_from_left_fade_in
            R.id.bookmark -> if (recentPosition < 2) 2 to R.anim.anim_slide_in_from_right_fade_in else 2 to R.anim.anim_slide_in_from_left_fade_in
            R.id.profile -> 3 to R.anim.anim_slide_in_from_right_fade_in
            else -> return
        }

        val navOptions = getNavOptions(enterAnim)
        recentPosition = newPosition
        navController.navigate(id, null, navOptions)

        selectNavigationItem(id)
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
        binding.exploreIcon.setColorFilter(ContextCompat.getColor(this, R.color.gray_50))
        binding.exploreText.setTextColor(ContextCompat.getColor(this, R.color.gray_50))

        binding.myAssetsIcon.setColorFilter(ContextCompat.getColor(this, R.color.gray_50))
        binding.myAssetsText.setTextColor(ContextCompat.getColor(this, R.color.gray_50))

        binding.bookmarkIcon.setColorFilter(ContextCompat.getColor(this, R.color.gray_50))
        binding.bookmarkText.setTextColor(ContextCompat.getColor(this, R.color.gray_50))

        binding.profileIcon.setColorFilter(ContextCompat.getColor(this, R.color.gray_50))
        binding.profileText.setTextColor(ContextCompat.getColor(this, R.color.gray_50))
    }

    private fun selectNavigationItem(destinationId: Int) {
        when (destinationId) {
            R.id.explore -> {
                binding.exploreIcon.setColorFilter(ContextCompat.getColor(this, R.color.gray_100))
                binding.exploreText.setTextColor(ContextCompat.getColor(this, R.color.gray_100))

                navigationMode(false)
            }

            R.id.my_assets -> {
                binding.myAssetsIcon.setColorFilter(ContextCompat.getColor(this, R.color.gray_100))
                binding.myAssetsText.setTextColor(ContextCompat.getColor(this, R.color.gray_100))

                navigationMode(false)
            }

            R.id.bookmark -> {
                binding.bookmarkIcon.setColorFilter(ContextCompat.getColor(this, R.color.gray_100))
                binding.bookmarkText.setTextColor(ContextCompat.getColor(this, R.color.gray_100))

                navigationMode(false)
            }

            R.id.profile -> {
                binding.profileIcon.setColorFilter(ContextCompat.getColor(this, R.color.gray_100))
                binding.profileText.setTextColor(ContextCompat.getColor(this, R.color.gray_100))

                navigationMode(false)
            }
        }
    }

}