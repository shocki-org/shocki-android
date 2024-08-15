package com.woojun.shocki.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.woojun.shocki.R
import com.woojun.shocki.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.nav_host_fragment)

        binding.explore.setOnClickListener {
            navController.navigate(R.id.explore)
        }
        binding.myAssets.setOnClickListener {
            navController.navigate(R.id.my_assets)
        }
        binding.bookmark.setOnClickListener {
            navController.navigate(R.id.bookmark)
        }
        binding.profile.setOnClickListener {
            navController.navigate(R.id.profile)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            resetNavigationItem()
            selectNavigationItem(destination.id)
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
                Log.d("확인", "explore")
                binding.exploreIcon.setColorFilter(ContextCompat.getColor(this, R.color.gray_100))
                binding.exploreText.setTextColor(ContextCompat.getColor(this, R.color.gray_100))

                navigationMode(false)
            }

            R.id.my_assets -> {
                Log.d("확인", "my_assets")
                binding.myAssetsIcon.setColorFilter(ContextCompat.getColor(this, R.color.gray_100))
                binding.myAssetsText.setTextColor(ContextCompat.getColor(this, R.color.gray_100))

                navigationMode(false)
            }

            R.id.bookmark -> {
                Log.d("확인", "bookmark")
                binding.bookmarkIcon.setColorFilter(ContextCompat.getColor(this, R.color.gray_100))
                binding.bookmarkText.setTextColor(ContextCompat.getColor(this, R.color.gray_100))

                navigationMode(false)
            }

            R.id.profile -> {
                Log.d("확인", "profile")
                binding.profileIcon.setColorFilter(ContextCompat.getColor(this, R.color.gray_100))
                binding.profileText.setTextColor(ContextCompat.getColor(this, R.color.gray_100))

                navigationMode(false)
            }
        }
    }

}