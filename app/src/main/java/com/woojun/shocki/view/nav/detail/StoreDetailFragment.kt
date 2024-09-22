package com.woojun.shocki.view.nav.detail

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.woojun.shocki.R
import com.woojun.shocki.databinding.FragmentStoreDetailBinding
import com.woojun.shocki.dto.ProductResponse
import com.woojun.shocki.util.Util.calculateEndDate
import com.woojun.shocki.util.Util.getProduct
import com.woojun.shocki.view.main.MainActivity
import kotlinx.coroutines.launch

class StoreDetailFragment : Fragment() {

    private var _binding: FragmentStoreDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoreDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWindowInsets()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        val productId = arguments?.getString("productId")
        if (productId != null) {
            lifecycleScope.launch {
                val productData = getProduct(productId)
                if (productData != null) {
                    initView(productData)
                } else {
                    Toast.makeText(requireContext(), "상품 조회를 실패하였습니다", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "상품 조회를 실패하였습니다", Toast.LENGTH_SHORT).show()
        }

    }

    private fun initView(productData: ProductResponse) {
        Glide
            .with(binding.root.context)
            .load(productData.image)
            .centerCrop()
            .into(binding.coverImage)
        binding.titleText.text = productData.name
        binding.priceText.text = productData.currentAmount.toString()
        binding.dateText.text = "${calculateEndDate(productData.marketEndDate)}일"

        binding.imageList.apply {
            this.adapter = ImageAdapter(productData.detailImages)
            this.layoutManager = LinearLayoutManager(requireContext())
        }

        binding.buyButton.setOnClickListener {
            (requireActivity() as MainActivity).animationNavigate(R.id.payment, productData.id)
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

            binding.backButton.setPadding(binding.backButton.paddingLeft, statusBarHeight, binding.backButton.paddingRight, navBarHeight)
            insets
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}