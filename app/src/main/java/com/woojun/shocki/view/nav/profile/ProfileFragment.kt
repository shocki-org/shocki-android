package com.woojun.shocki.view.nav.profile

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.card.MaterialCardView
import com.google.android.material.slider.Slider
import com.tosspayments.paymentsdk.TossPayments
import com.tosspayments.paymentsdk.model.TossPaymentResult
import com.tosspayments.paymentsdk.model.paymentinfo.TossCardPaymentInfo
import com.woojun.shocki.R
import com.woojun.shocki.database.TokenManager
import com.woojun.shocki.databinding.FragmentProfileBinding
import com.woojun.shocki.databinding.MiddleAccountBannerItemBinding
import com.woojun.shocki.dto.AccountResponse
import com.woojun.shocki.dto.FavoriteResponse
import com.woojun.shocki.dto.PayRequest
import com.woojun.shocki.dto.SimpleProductResponse
import com.woojun.shocki.network.RetrofitAPI
import com.woojun.shocki.network.RetrofitClient
import com.woojun.shocki.util.SpacingItemDecoration
import com.woojun.shocki.util.Util.formatAmount
import com.woojun.shocki.view.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

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

        binding.addCreditButton.setOnClickListener {
            creditNumberDialog()
        }

        initView()
    }

    private fun initView() {
        lifecycleScope.launch {
            getAccount()?.let {
                binding.assetText.text = formatAmount(it.credit+it.settlement.totalSettlementAmount)
                binding.nowCreditText.text = formatAmount(it.credit)
                binding.futureCreditText.text = formatAmount(it.settlement.totalSettlementAmount)

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
                        Pair(MiddleAccountBannerItemBinding.inflate(inflater),
                            SimpleProductResponse(item.productPrice, item.productId, item.productImage, item.productName, "")
                        )
                    }
                    adapter = MiddleAccountViewPagerAdapter(list)
                    if (list.size < 3) {
                        binding.pinList.setOnTouchListener { _, _ -> true }
                    }
                }
            }
        }
    }

    private suspend fun postPay(payRequest: PayRequest): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.postPay("bearer ${TokenManager.accessToken}", payRequest)
                response.isSuccessful
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun creditFinishDialog(price: Long) {
        val customDialog = Dialog(requireContext())
        customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        customDialog.window?.setGravity(Gravity.BOTTOM)

        customDialog.setContentView(R.layout.dialog_credit_finish)
        customDialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        customDialog.findViewById<TextView>(R.id.body_text).text = "결제를 성공적으로 진행하여\n${formatAmount(price)} 크레딧 충전에 성공했어요!"

        customDialog.findViewById<CardView>(R.id.main_button).setOnClickListener {
            customDialog.cancel()
        }

        customDialog.show()
    }

    private fun creditPaymentDialog(price: Long) {
        val customDialog = Dialog(requireContext())
        customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        customDialog.window?.setGravity(Gravity.BOTTOM)

        customDialog.setContentView(R.layout.dialog_credit_payment)
        customDialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        customDialog.findViewById<TextView>(R.id.title_text).text = "${formatAmount(price)} 크레딧 충전을 위해\n토스를 실행할게요"
        customDialog.findViewById<TextView>(R.id.body_text).text = "크레딧 구매를 위한 ${formatAmount(price)}원 결제가\n토스를 통해 진행 될 예정이에요"

        customDialog.findViewById<CardView>(R.id.main_button).setOnClickListener {
            val tossPayments = TossPayments("test_ck_D5GePWvyJnrK0W0k6q8gLzN97Eoq")
            val tossPaymentInfo = TossCardPaymentInfo(orderId = UUID.randomUUID().toString(), orderName = "Shocki 크레딧", price)

            tossPayments.requestCardPayment(
                requireActivity(),
                tossPaymentInfo,
                (requireActivity() as MainActivity).tossPaymentActivityResult1
            )
        }

        customDialog.show()
    }

    private fun creditNumberDialog() {
        val customDialog = Dialog(requireContext())
        customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        customDialog.window?.setGravity(Gravity.BOTTOM)

        customDialog.setContentView(R.layout.dialog_credit_number)
        customDialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        customDialog.findViewById<Slider>(R.id.slider).addOnChangeListener { _, value, _ ->
            customDialog.findViewById<TextView>(R.id.credit_text).text = "${formatAmount(value.toInt())} 크레딧 · "
            customDialog.findViewById<TextView>(R.id.won_text).text = "${formatAmount(value.toInt())}원"
        }

        customDialog.findViewById<CardView>(R.id.main_button).setOnClickListener {
            creditPaymentDialog(customDialog.findViewById<TextView>(R.id.won_text).text.toString().substring(0, customDialog.findViewById<TextView>(R.id.won_text).text.length - 1).toLong())
            customDialog.cancel()
        }

        customDialog.findViewById<MaterialCardView>(R.id.cancel_button).setOnClickListener {
            customDialog.cancel()
        }

        customDialog.show()
    }

    fun handlePaymentResult(success: TossPaymentResult.Success) {
        lifecycleScope.launch {
            val isSuccess = postPay(PayRequest(success.amount.toInt(), success.orderId, success.paymentKey))
            if (isSuccess) {
                creditFinishDialog(success.amount.toLong())
                initView()
            } else {
                Toast.makeText(requireContext(), "결제를 실패하였습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun handlePaymentFailure() {
        Toast.makeText(requireContext(), "결제를 실패하였습니다", Toast.LENGTH_SHORT).show()
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