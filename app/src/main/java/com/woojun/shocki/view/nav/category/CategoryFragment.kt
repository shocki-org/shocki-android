package com.woojun.shocki.view.nav.category

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.woojun.shocki.R
import com.woojun.shocki.data.Banner
import com.woojun.shocki.data.BannerType
import com.woojun.shocki.data.Category
import com.woojun.shocki.databinding.FragmentCategoryBinding
import com.woojun.shocki.util.GridSpacingItemDecoration
import com.woojun.shocki.view.main.MainActivity
import com.woojun.shocki.view.nav.explore.BannerViewPagerAdapter

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchButton.setOnClickListener {
            (requireActivity() as MainActivity).animationNavigate(R.id.search)
        }

        binding.categoryList.apply {
            this.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            this.adapter = CategoryAdapter(getCategoryList())
            this.addItemDecoration(GridSpacingItemDecoration())
        }

        binding.itemList.apply {
            this.layoutManager = GridLayoutManager(requireContext(), 2)
            this.adapter = BannerViewPagerAdapter(getTestBannerList(), BannerType.Grid)
            this.addItemDecoration(GridSpacingItemDecoration())
        }
    }

    private fun getTestBannerList(): List<Banner> {
        val text = "정성담아 키워낸, 해남 황토 꿀고구마정성담아 키워낸, 해남 황토 꿀고구마"
        return listOf(
            Banner(R.drawable.banner6, text, "1232145"),
            Banner(R.drawable.banner1, text, "1232145"),
            Banner(R.drawable.banner4, text, "1232145"),
            Banner(R.drawable.banner3, text, "1232145"),
            Banner(R.drawable.banner6, text, "1232145"),
            Banner(R.drawable.banner1, text, "1232145"),
            Banner(R.drawable.banner4, text, "1232145"),
            Banner(R.drawable.banner3, text, "1232145"),
        )
    }

    private fun getCategoryList(): List<Category> {
        return listOf(
            Category("이것들은", true),
            Category("카테고리임 ㅋ"),
            Category("선택된 칩"),
            Category("ㄹㅇㅋㅋ"),
            Category("힝 속았지"),
            Category("아직"),
            Category("남았당"),
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}