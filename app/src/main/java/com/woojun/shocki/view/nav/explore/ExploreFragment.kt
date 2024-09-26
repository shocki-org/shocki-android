package com.woojun.shocki.view.nav.explore

import android.animation.Animator
import android.animation.ObjectAnimator
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
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.woojun.shocki.R
import com.woojun.shocki.data.BannerType
import com.woojun.shocki.database.MainViewModel
import com.woojun.shocki.databinding.FragmentExploreBinding
import com.woojun.shocki.databinding.MiddleBannerItemBinding
import com.woojun.shocki.dto.SimpleProductResponse
import com.woojun.shocki.util.SpacingItemDecoration
import com.woojun.shocki.view.main.MainActivity
import java.lang.ref.WeakReference

class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    private val bannerHandler = BannerHandler(this)
    private var currentPosition = 0

    private lateinit var mainViewModel: MainViewModel

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
        val activity = (requireActivity() as MainActivity)

        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        mainViewModel.fundingList.observe(viewLifecycleOwner) { fundingList ->
            if (fundingList != null && fundingList.size > 7 ) {
                fundingBannerInit(fundingList.slice(0..7))
            } else {
                Toast.makeText(requireContext(), "펀딩 목록 조회를 실패했습니다", Toast.LENGTH_SHORT).show()
            }
        }

        mainViewModel.sellingList.observe(viewLifecycleOwner) { sellingList ->
            if (sellingList != null && sellingList.size > 11) {
                sellingBannerInit(sellingList.slice(0..11))
            } else {
                Toast.makeText(requireContext(), "상품 목록 조회를 실패했습니다", Toast.LENGTH_SHORT).show()
            }
        }

        binding.notificationButton.setOnClickListener {
            activity.animationNavigate(R.id.notification)
        }

        binding.searchButton.setOnClickListener {
            activity.animationNavigate(R.id.search)
        }
    }

    private fun fundingBannerInit(fundingList: List<SimpleProductResponse>) {
        binding.topBannerViewPager.apply {

            val indicatorList = listOf(binding.indicator0, binding.indicator1, binding.indicator2, binding.indicator3)

            adapter = BannerViewPagerAdapter(fundingList.slice(0..3), BannerType.Top)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    currentPosition = position

                    indicatorList.forEach { it.setBackgroundResource(R.drawable.shape_circle_gray) }
                    val selectedIndicator = indicatorList[position % indicatorList.size]
                    selectedIndicator.setBackgroundResource(R.drawable.shape_circle_green)

                    val animator = ObjectAnimator.ofFloat(selectedIndicator, "alpha", 0.0f, 1.0f)
                    animator.duration = 500
                    animator.start()
                }
                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    when (state) {
                        ViewPager2.SCROLL_STATE_IDLE -> autoScrollStart()
                        ViewPager2.SCROLL_STATE_DRAGGING -> autoScrollStop()
                        ViewPager2.SCROLL_STATE_SETTLING -> {}
                    }
                }
            })

            currentPosition = Int.MAX_VALUE / 2 - (Int.MAX_VALUE / 2) % indicatorList.size
            setCurrentItem(currentPosition, false)
        }

        binding.linearList.apply {
            val list = fundingList.slice(4..7)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = BannerViewPagerAdapter(list, BannerType.Linear)
        }
    }

    private fun sellingBannerInit(sellingList: List<SimpleProductResponse>) {
        binding.middleBannerViewPager.apply {
            val inflater = LayoutInflater.from(context)
            val list = sellingList.slice(0..5).map { item ->
                Pair(MiddleBannerItemBinding.inflate(inflater), item)
            }
            adapter = MiddleViewPagerAdapter(list)
        }

        binding.gridList.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = BannerViewPagerAdapter(sellingList.slice(6..11), BannerType.Grid)
            addItemDecoration(SpacingItemDecoration())
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
                decorView.systemUiVisibility = decorView.systemUiVisibility and
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

    private fun autoScrollStart() {
        bannerHandler.removeMessages(0)
        bannerHandler.sendEmptyMessageDelayed(0, 1000)
    }

    private fun autoScrollStop(){
        bannerHandler.removeMessages(0)
    }

    override fun onResume() {
        super.onResume()
        autoScrollStart()
    }

    override fun onPause() {
        super.onPause()
        autoScrollStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        autoScrollStop()
        _binding = null
    }

    private class BannerHandler(fragment: ExploreFragment?) : Handler(Looper.getMainLooper()) {
        private val fragmentRef = WeakReference(fragment)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val fragment = fragmentRef.get()

            if (msg.what == 0) {
                fragment?.activity?.runOnUiThread {
                    fragment.view?.let { view ->
                        val viewPager = view.findViewById<ViewPager2>(R.id.top_banner_view_pager)
                        viewPager?.setCurrentItemWithDuration(++fragment.currentPosition, 500)

                        fragment.autoScrollStart()
                    }
                }
            }
        }


        private fun ViewPager2.setCurrentItemWithDuration(
            item: Int,
            duration: Long,
            interpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
            pagePxWidth: Int = width
        ) {
            if (!isFakeDragging) {
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

}