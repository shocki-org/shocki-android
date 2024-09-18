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
import androidx.lifecycle.MediatorLiveData
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
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    private val indicatorList by lazy { listOf(binding.indicator0, binding.indicator1, binding.indicator2, binding.indicator3) }

    private val bannerHandler = BannerHandler(this)
    private val intervalTime = 1500.toLong()
    private var currentPosition = 0

    private lateinit var mainViewModel: MainViewModel
    private val combinedLiveData = MediatorLiveData<Pair<List<SimpleProductResponse>?,  List<SimpleProductResponse>?>>()

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

        combinedLiveData.addSource(mainViewModel.sellingList) { sellingList ->
            val fundingList = combinedLiveData.value?.first
            combinedLiveData.value = Pair(fundingList, sellingList)
        }

        combinedLiveData.addSource(mainViewModel.fundingList) { fundingList ->
            val sellingList = combinedLiveData.value?.second
            combinedLiveData.value = Pair(fundingList, sellingList)
        }

        combinedLiveData.observe(viewLifecycleOwner) { pair ->
            val fundingList = pair.first
            val sellingList = pair.second
            if (fundingList != null && sellingList != null) {
                bannerInit(fundingList.slice(0..7), sellingList.slice(0..11))
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

    private fun bannerInit(fundingList: List<SimpleProductResponse>, sellingList: List<SimpleProductResponse>) {
        binding.topBannerViewPager.apply {
            this.adapter = BannerViewPagerAdapter(fundingList.slice(0..3), BannerType.Top)
            this.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
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
            val list = sellingList.slice(0..5).map { item ->
                Pair(MiddleBannerItemBinding.inflate(inflater), item)
            }
            adapter = MiddleViewPagerAdapter(list)
        }

        binding.linearList.apply {
            this.layoutManager = LinearLayoutManager(requireContext())
            this.adapter = BannerViewPagerAdapter(fundingList.slice(4..7), BannerType.Linear)
        }

        binding.gridList.apply {
            this.layoutManager = GridLayoutManager(requireContext(), 2)
            this.adapter = BannerViewPagerAdapter(sellingList.slice(6..11), BannerType.Grid)
            this.addItemDecoration(SpacingItemDecoration())
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

    override fun onDestroyView() {
        super.onDestroyView()
        autoScrollStop()
        _binding = null
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