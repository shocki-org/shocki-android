package com.woojun.shocki.view.nav.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.woojun.shocki.R
import com.woojun.shocki.database.TokenManager
import com.woojun.shocki.databinding.FragmentProfileBinding
import com.woojun.shocki.databinding.MiddleBannerItemBinding
import com.woojun.shocki.dto.AccountResponse
import com.woojun.shocki.dto.FavoriteResponse
import com.woojun.shocki.dto.SimpleProductResponse
import com.woojun.shocki.network.RetrofitAPI
import com.woojun.shocki.network.RetrofitClient
import com.woojun.shocki.util.SpacingItemDecoration
import com.woojun.shocki.view.main.MainActivity
import com.woojun.shocki.view.nav.explore.MiddleViewPagerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.settingButton.setOnClickListener {
            (requireActivity() as MainActivity).animationNavigate(R.id.setting)
        }

        lifecycleScope.launch {
            getAccount()?.let {
                binding.assetText.text = (it.credit+it.settlement.totalSettlementAmount).toString()
                binding.nowCreditText.text = it.credit.toString()
                binding.futureCreditText.text = it.settlement.totalSettlementAmount.toString()

                binding.tokenAssetsList.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = LinearAccountAdapter(it.tokenBalances)
                }

                binding.moneyList.apply {
                    layoutManager = GridLayoutManager(requireContext(), 2)
                    adapter = GridAccountAdapter(it.settlement.settlementProducts)
                    addItemDecoration(SpacingItemDecoration())
                }
            }
            getFavoriteList()?.let {
                binding.pinList.apply {
                    val inflater = LayoutInflater.from(context)
                    val list = it.map { item ->
                        Pair(MiddleBannerItemBinding.inflate(inflater),
                            SimpleProductResponse(item.productPrice, item.productId, item.productImage, item.productName, "")
                        )
                    }
                    adapter = MiddleViewPagerAdapter(list)
                }
            }
        }

    }

    private suspend fun getFavoriteList(): List<FavoriteResponse>? {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.getFavoriteList("bearer ${TokenManager.accessToken}")
                if (response.isSuccessful) {
                    response.body()
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "좋아요 목록 조회를 실패하였습니다", Toast.LENGTH_SHORT).show()
                    }
                    null
                }
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "좋아요 목록 조회를 실패하였습니다", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private suspend fun getAccount(): AccountResponse? {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.getAccount("bearer ${TokenManager.accessToken}")
                if (response.isSuccessful) {
                    response.body()
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "유저 정보 조회를 실패하였습니다", Toast.LENGTH_SHORT).show()
                    }
                    null
                }
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "유저 정보 조회를 실패하였습니다", Toast.LENGTH_SHORT).show()
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}