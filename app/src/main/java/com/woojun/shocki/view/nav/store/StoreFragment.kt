package com.woojun.shocki.view.nav.store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.woojun.shocki.R
import com.woojun.shocki.data.Banner
import com.woojun.shocki.databinding.FragmentStoreBinding
import com.woojun.shocki.view.main.MainActivity

class StoreFragment : Fragment() {

    private var _binding: FragmentStoreBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = (requireActivity() as MainActivity)

        binding.shippingButton.setOnClickListener {
            activity.animationNavigate(R.id.shipping)
        }

        binding.searchButton.setOnClickListener {
            activity.animationNavigate(R.id.search)
        }

        binding.storeList.apply {
            this.layoutManager = GridLayoutManager(requireContext(), 2)
            this.adapter = StoreAdapter(getTestBannerList())
        }

    }

    private fun getTestBannerList(): List<Banner> {
        val text = "정성담아 키워낸, 해남 황토 꿀고구마정성담아 키워낸, 해남 황토 꿀고구마"
        return listOf(
            Banner(R.drawable.banner6, text, "1,232,145"),
            Banner(R.drawable.banner1, text, "1,232,145"),
            Banner(R.drawable.banner4, text, "1,232,145"),
            Banner(R.drawable.banner3, text, "1,232,145"),
            Banner(R.drawable.banner6, text, "1,232,145"),
            Banner(R.drawable.banner1, text, "1,232,145"),
            Banner(R.drawable.banner4, text, "1,232,145"),
            Banner(R.drawable.banner3, text, "1,232,145"),
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}