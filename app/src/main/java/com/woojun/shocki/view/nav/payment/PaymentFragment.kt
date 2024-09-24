package com.woojun.shocki.view.nav.payment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tosspayments.paymentsdk.TossPayments
import com.tosspayments.paymentsdk.model.TossPaymentResult
import com.tosspayments.paymentsdk.model.paymentinfo.TossCardPaymentInfo
import com.woojun.shocki.R
import com.woojun.shocki.database.TokenManager
import com.woojun.shocki.databinding.FragmentPaymentBinding
import com.woojun.shocki.dto.AddressResponse
import com.woojun.shocki.dto.MarketRequest
import com.woojun.shocki.dto.PayRequest
import com.woojun.shocki.network.RetrofitAPI
import com.woojun.shocki.network.RetrofitClient
import com.woojun.shocki.util.Util.checkPhone
import com.woojun.shocki.util.Util.formatAmount
import com.woojun.shocki.util.Util.getProduct
import com.woojun.shocki.view.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class PaymentFragment : Fragment(), AddressAdapter.ItemClick {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    private var index = 1
    private var userCredit = 0
    private var productPrice = Int.MAX_VALUE
    private var productId: String? = null

    private val queryFlow = MutableStateFlow("")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.nextButton.setOnClickListener {
            nextAction()
        }

        productId = arguments?.getString("productId")
        if (productId != null) {
            lifecycleScope.launch {
                val productData = getProduct(productId!!)
                val credit = getUserCredit()

                if (productData != null) {
                    productPrice = productData.currentAmount
                    userCredit = credit
                    initView()
                } else {
                    Toast.makeText(requireContext(), "상품 조회를 실패하였습니다", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "상품 조회를 실패하였습니다", Toast.LENGTH_SHORT).show()
        }

    }

    private fun initView() {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        binding.addressList.layoutManager = LinearLayoutManager(context)

        binding.countNumberText.text = "${formatAmount(binding.countInput.text.toString().toInt() * productPrice)} 크레딧"

        binding.countInput.apply {
            setOnEditorActionListener(object : TextView.OnEditorActionListener{
                override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_GO && binding.countInput.text.isNotEmpty()){
                        nextAction()
                        binding.phoneInput.requestFocus()
                        inputMethodManager.showSoftInput(binding.phoneInput, InputMethodManager.SHOW_IMPLICIT)
                        return true
                    }
                    return false
                }
            })
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                }
                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    if (binding.countInput.text.isNotEmpty()) {
                        binding.nextButton.visibility = View.VISIBLE
                        binding.noneButton.visibility = View.GONE
                        binding.countNumberText.text = "${formatAmount(binding.countInput.text.toString().toInt() * productPrice)} 크레딧"
                    } else {
                        binding.noneButton.visibility = View.VISIBLE
                        binding.nextButton.visibility = View.GONE
                    }
                }
                override fun afterTextChanged(editable: Editable) {
                }
            })
        }

        binding.phoneInput.apply {
            addTextChangedListener(PhoneNumberFormattingTextWatcher())
            setOnEditorActionListener(object : TextView.OnEditorActionListener{
                override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_GO && binding.phoneInput.text.isNotEmpty()){
                        nextAction()
                        binding.addressInput.requestFocus()
                        inputMethodManager.showSoftInput(binding.addressInput, InputMethodManager.SHOW_IMPLICIT)
                        return true
                    }
                    return false
                }
            })
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                }
                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    if (binding.phoneInput.text.isNotEmpty()) {
                        binding.nextButton.visibility = View.VISIBLE
                        binding.noneButton.visibility = View.GONE
                    } else {
                        binding.noneButton.visibility = View.VISIBLE
                        binding.nextButton.visibility = View.GONE
                    }
                }
                override fun afterTextChanged(editable: Editable) {
                }
            })
        }

        binding.addressInput.apply {
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    queryFlow.value = text.toString()
                    true
                } else {
                    false
                }
            }
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                }
                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    queryFlow.value = charSequence.toString()
                    if (binding.addressInput.text.isNotEmpty()) {
                        binding.nextButton.visibility = View.VISIBLE
                        binding.noneButton.visibility = View.GONE
                    } else {
                        binding.noneButton.visibility = View.VISIBLE
                        binding.nextButton.visibility = View.GONE
                    }
                }
                override fun afterTextChanged(editable: Editable) {
                }
            })
        }

        lifecycleScope.launch {
            queryFlow
                .debounce(300)
                .collectLatest { query ->
                    if (query.isNotEmpty()) {
                        val addressResponse = searchAddress(query)
                        if (addressResponse != null) updateRecyclerView(addressResponse)
                    }
                }
        }

        binding.detailAddressInput.apply {
            setOnEditorActionListener(object : TextView.OnEditorActionListener{
                override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_GO && binding.detailAddressInput.text.isNotEmpty()){
                        nextAction()
                        return true
                    }
                    return false
                }
            })
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                }
                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    if (binding.phoneInput.text.isNotEmpty()) {
                        binding.nextButton.visibility = View.VISIBLE
                        binding.noneButton.visibility = View.GONE
                    } else {
                        binding.noneButton.visibility = View.VISIBLE
                        binding.nextButton.visibility = View.GONE
                    }
                }
                override fun afterTextChanged(editable: Editable) {
                }
            })
        }

    }

    private fun nextAction() {
        when(index) {
            1 -> {
                switchText(0)
                showViewWithAnimation(binding.countBox, requireContext())
                showViewWithAnimation(binding.phoneBox, requireContext())
                binding.countInput.isEnabled = false

                index = 2
            }
            2 -> {
                if (checkPhone(binding.phoneInput.text.toString())) {
                    switchText(1)
                    showViewWithAnimation(binding.countBox, requireContext())
                    showViewWithAnimation(binding.phoneBox, requireContext())
                    showViewWithAnimation(binding.addressBox, requireContext())
                    binding.phoneInput.isEnabled = false

                    index = 3
                } else {
                    Toast.makeText(requireContext(), "전화번호 형식을 지켜주세요", Toast.LENGTH_SHORT).show()
                }
            }
            3 -> {
                if (!binding.addressInput.isEnabled) {
                    showViewWithAnimation(binding.countBox, requireContext())
                    showViewWithAnimation(binding.phoneBox, requireContext())
                    showViewWithAnimation(binding.addressBox, requireContext())
                    binding.addressList.visibility = View.GONE
                    showViewWithAnimation(binding.detailAddressBox, requireContext())

                    index = 4
                } else {
                    Toast.makeText(requireContext(), "주소를 선택해주세요", Toast.LENGTH_SHORT).show()
                }
            }
            4 -> {
                if (binding.addressInput.text.isNotEmpty()) {
                    if (productPrice > userCredit) {
                        creditPaymentDialog((productPrice - userCredit).toLong())
                    } else {
                        lifecycleScope.launch {
                            val address = binding.addressInput.text.toString() + binding.detailAddressInput.text.toString()
                            val phone = binding.phoneInput.text.toString()
                            val isSuccess = buyMarket(MarketRequest(address, productPrice, phone, productId!!))

                            if (isSuccess) {
                                Toast.makeText(requireContext(), "구매 완료", Toast.LENGTH_SHORT).show()
                                (requireActivity() as MainActivity).animationNavigate(R.id.explore)
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "상세 주소를 입력해주세요", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun buyMarket(marketRequest: MarketRequest): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.buyMarket("bearer ${TokenManager.accessToken}", marketRequest)
                response.isSuccessful
            }
        } catch (e: Exception) {
            false
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

        customDialog.findViewById<TextView>(R.id.body_text).text = "결제를 성공적으로 진행하여\n${price} 크레딧 충전에 성공했어요!"

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
                (requireActivity() as MainActivity).tossPaymentActivityResult2
            )
        }

        customDialog.show()
    }

    fun handlePaymentResult(success: TossPaymentResult.Success) {
        lifecycleScope.launch {
            val isSuccess = postPay(PayRequest(success.amount.toInt(), success.orderId, success.paymentKey))
            if (isSuccess) {
                creditFinishDialog(success.amount.toLong())
                userCredit = getUserCredit()
            } else {
                Toast.makeText(requireContext(), "결제를 실패하였습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun handlePaymentFailure() {
        Toast.makeText(requireContext(), "결제를 실패하였습니다", Toast.LENGTH_SHORT).show()
    }

    private suspend fun getUserCredit(): Int {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.getAccount("bearer ${TokenManager.accessToken}")
                if (response.isSuccessful) {
                    response.body()?.credit ?: 0
                } else {
                    0
                }
            }
        } catch (e: Exception) {
            0
        }
    }

    override fun itemClick(item: String) {
        binding.addressInput.apply {
            setText(item)
            isEnabled = false
        }
        nextAction()
    }

    private fun updateRecyclerView(addressResponse: AddressResponse) {
        binding.addressList.adapter = AddressAdapter(addressResponse.documents, this)
    }

    private suspend fun searchAddress(name: String): AddressResponse? {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.searchAddress("bearer ${TokenManager.accessToken}", name)
                if (response.isSuccessful) {
                    response.body()
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun switchText(index: Int) {
        val texts = arrayOf(
            "상품을 받을\n전화번호를 입력해주세요",
            "상품을 받을\n주소를 입력해주세요"
        )
        binding.textView.setText(texts[index])
    }

    private fun showViewWithAnimation(view: View, context: Context) {
        val slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom)
        view.startAnimation(slideIn)
        view.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}