package com.woojun.shocki.view.nav.funding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.woojun.shocki.R
import com.woojun.shocki.data.BannerType
import com.woojun.shocki.data.Category
import com.woojun.shocki.database.MainViewModel
import com.woojun.shocki.databinding.FragmentFundingBinding
import com.woojun.shocki.dto.SimpleProductResponse
import com.woojun.shocki.util.SpacingItemDecoration
import com.woojun.shocki.view.main.MainActivity
import com.woojun.shocki.view.nav.explore.BannerViewPagerAdapter

class FundingFragment : Fragment(), FundingAdapter.CategoryListener {

    private var _binding: FragmentFundingBinding? = null
    private val binding get() = _binding!!

    private var categoryId = ""

    private lateinit var mainViewModel: MainViewModel
    private val combinedLiveData = MediatorLiveData<Pair<List<SimpleProductResponse>?,  List<Category>?>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFundingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        combinedLiveData.addSource(mainViewModel.categoryList) { categoryList ->
            val fundingList = combinedLiveData.value?.first
            combinedLiveData.value = Pair(fundingList, categoryList)
        }

        combinedLiveData.addSource(mainViewModel.fundingList) { fundingList ->
            val categoryList = combinedLiveData.value?.second
            combinedLiveData.value = Pair(fundingList, categoryList)
        }

        combinedLiveData.observe(viewLifecycleOwner) { pair ->
            val fundingList = pair.first
            val categoryList = pair.second
            if (fundingList != null && categoryList != null) {
                binding.categoryList.apply {
                    categoryList[0].isSelected = true
                    categoryId = categoryList[0].id

                    this.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
                    this.adapter = FundingAdapter(categoryList, this@FundingFragment)
                    this.addItemDecoration(SpacingItemDecoration())
                }

                binding.itemList.apply {
                    layoutManager = GridLayoutManager(requireContext(), 2)
                    adapter = BannerViewPagerAdapter(fundingList.filter { it.categoryId == categoryId }, BannerType.Grid)
                    addItemDecoration(SpacingItemDecoration())
                }
            } else {
                Toast.makeText(requireContext(), "카테고리 목록을 불러오지 못했습니다", Toast.LENGTH_SHORT).show()
            }
        }

        binding.searchButton.setOnClickListener {
            (requireActivity() as MainActivity).animationNavigate(R.id.search)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun touchCategory(categoryId: String) {
        this.categoryId = categoryId
        mainViewModel.fundingList.value?.let { fundingList ->
            binding.itemList.adapter = BannerViewPagerAdapter(fundingList.filter { it.categoryId == categoryId }, BannerType.Grid)
        }
    }

}