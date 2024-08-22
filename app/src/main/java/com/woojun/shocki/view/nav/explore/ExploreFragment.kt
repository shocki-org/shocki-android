package com.woojun.shocki.view.nav.explore

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.woojun.shocki.R
import com.woojun.shocki.data.Banner
import com.woojun.shocki.databinding.FragmentExploreBinding
import com.woojun.shocki.databinding.MiddleBannerItemBinding
import java.lang.ref.WeakReference

class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    private val indicatorList by lazy { listOf(binding.indicator0, binding.indicator1, binding.indicator2, binding.indicator3) }

    private val bannerHandler = BannerHandler(this)
    private val intervalTime = 1500.toLong()
    private var currentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWindowInsets()

        binding.topBannerViewPager.apply {
            this.adapter = BannerViewPagerAdapter(getTestBannerList())
            this.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    currentPosition = position

                    indicatorList.forEach { it.setBackgroundResource(R.drawable.shape_circle_gray) }
                    val selectedIndicator = indicatorList[position % indicatorList.size]
                    selectedIndicator.setBackgroundResource(R.drawable.shape_circle_green)
                }
                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    when (state) {
                        ViewPager2.SCROLL_STATE_IDLE -> autoScrollStart(intervalTime)
                        ViewPager2.SCROLL_STATE_DRAGGING -> autoScrollStop()
                        ViewPager2.SCROLL_STATE_SETTLING -> {}
                    }
                }
            })

            currentPosition = Int.MAX_VALUE / 2 - (Int.MAX_VALUE / 2) % indicatorList.size
            this.setCurrentItem(currentPosition, false)
        }

        binding.middleBannerViewPager.apply {
            val inflater = LayoutInflater.from(context)
            val list = getTestBannerList().plus(getTestBannerList()).map { item ->
                Pair(MiddleBannerItemBinding.inflate(inflater), item)
            }
            adapter = MiddleViewPagerAdapter(list)
        }
    }

    private fun setupWindowInsets() {
        requireActivity().enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.insetsController?.setSystemBarsAppearance(
                0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            requireActivity().window.apply {
                this.decorView.systemUiVisibility = this.decorView.systemUiVisibility and
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())

            val statusBarHeight = statusBars.top
            val navBarHeight = systemBars.bottom

            binding.buttonLayout.setPadding(0, statusBarHeight, 0, navBarHeight)
            insets
        }
    }

    private fun getTestBannerList(): List<Banner> {
        return listOf(
            Banner(R.drawable.banner6, "정성담아 키워낸,\n해남 황토 꿀고구마", "1232145"),
            Banner(R.drawable.banner1, "정성담아 키워낸,\n해남 황토 꿀고구마", "1232145"),
            Banner(R.drawable.banner4, "정성담아 키워낸,\n해남 황토 꿀고구마", "1232145"),
            Banner(R.drawable.banner3, "정성담아 키워낸,\n해남 황토 꿀고구마", "1232145"),
        )
    }

    private fun autoScrollStart(intervalTime: Long) {
        bannerHandler.removeMessages(0)
        bannerHandler.sendEmptyMessageDelayed(0, intervalTime)
    }

    private fun autoScrollStop(){
        bannerHandler.removeMessages(0)
    }

    override fun onResume() {
        super.onResume()
        autoScrollStart(intervalTime)
    }

    override fun onPause() {
        super.onPause()
        autoScrollStop()
    }

    private class BannerHandler(fragment: ExploreFragment) : Handler(Looper.getMainLooper()) {
        private val fragmentRef = WeakReference(fragment)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val fragment = fragmentRef.get()

            if(msg.what == 0) {
                fragment?.let {
                    fragment.binding.topBannerViewPager.setCurrentItemWithDuration(++fragment.currentPosition, 500)
                    fragment.autoScrollStart(fragment.intervalTime)
                }
            }
        }


        private fun ViewPager2.setCurrentItemWithDuration(
            item: Int,
            duration: Long,
            interpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
            pagePxWidth: Int = width
        ) {
            val pxToDrag: Int = pagePxWidth * (item - currentItem)
            val animator = ValueAnimator.ofInt(0, pxToDrag)
            var previousValue = 0
            animator.addUpdateListener { valueAnimator ->
                val currentValue = valueAnimator.animatedValue as Int
                val currentPxToDrag = (currentValue - previousValue).toFloat()
                fakeDragBy(-currentPxToDrag)
                previousValue = currentValue
            }
            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) { beginFakeDrag() }
                override fun onAnimationEnd(animation: Animator) { endFakeDrag() }
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
            animator.interpolator = interpolator
            animator.duration = duration
            animator.start()
        }
    }

}