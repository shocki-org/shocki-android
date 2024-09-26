package com.woojun.shocki.view.nav.detail

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.TextSwitcher
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.card.MaterialCardView
import com.google.android.material.slider.Slider
import com.google.android.material.tabs.TabLayout
import com.woojun.shocki.R
import com.woojun.shocki.data.Chat
import com.woojun.shocki.data.ChatType
import com.woojun.shocki.data.Graph
import com.woojun.shocki.database.TokenManager
import com.woojun.shocki.databinding.FragmentFundingDetailBinding
import com.woojun.shocki.dto.ProductResponse
import com.woojun.shocki.model.MetamaskModel
import com.woojun.shocki.network.RetrofitAPI
import com.woojun.shocki.network.RetrofitClient
import com.woojun.shocki.util.Util.calculateEndDate
import com.woojun.shocki.util.Util.formatAmount
import com.woojun.shocki.util.Util.getProduct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import io.metamask.androidsdk.EthereumRequest
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Bool
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.utils.Convert
import io.metamask.androidsdk.Result
import java.math.BigInteger


class FundingDetailFragment : Fragment() {

    private var _binding: FragmentFundingDetailBinding? = null
    private val binding get() = _binding!!

    private var isLike = false
    private var productId: String? = ""
    private var userToken = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFundingDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWindowInsets()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        lifecycleScope.launch {
            userToken = getUserCredit()
        }

        binding.tabLayout.apply {
            addTab(newTab().setCustomView(createTabView("제품소개", true)))
            addTab(newTab().setCustomView(createTabView("QnA")))
        }

    }

    override fun onResume() {
        super.onResume()
        productId = arguments?.getString("productId")
        if (productId != null) {
            lifecycleScope.launch {
                val productData = getProduct(productId!!)
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
        binding.tagText.text = productData.categories[0]
        binding.titleText.text = productData.name
        binding.priceText.text = formatAmount(productData.currentAmount)
        binding.dateText.text = "${calculateEndDate(productData.fundingEndDate)}일"

        binding.nowTokenPrice.text = "${formatAmount(productData.currentAmount)}크레딧"
        binding.fundingDate.text = "${formatFundingDate(productData.fundingEndDate)}"
        binding.nowFundingAmount.text = "${formatAmount(productData.collectedAmount)}크레딧 ${(productData.collectedAmount / productData.targetAmount) * 100}%"
        binding.goalFundingPrice.text = "${formatAmount(productData.targetAmount)}크레딧"

        binding.graphView.apply {
            val dataSet = createLineDataSet(productData.graph)
            val lineData = LineData(dataSet)
            setupLineChart(this, lineData)
        }

        bindTabLayout(productData)

        binding.buyButton.apply {
            if (productData.purchaseIsDisabled) {
                isEnabled = false
                strokeColor = resources.getColor(R.color.background_gray_Border)
                setCardBackgroundColor(resources.getColor(R.color.background_gray_Default))
                binding.buyText.setTextColor(resources.getColor(R.color.Text_Status_Unable))
            } else {
                setOnClickListener {
                    buyCreditNumberDialog(productData.id, productData.currentAmount)
                }
            }
        }

        binding.saleButton.apply {
            if (productData.saleIsDisabled) {
                isEnabled = false
                strokeColor = resources.getColor(R.color.background_gray_Border)
                setCardBackgroundColor(resources.getColor(R.color.background_gray_Default))
                binding.saleText.setTextColor(resources.getColor(R.color.Text_Status_Unable))
            } else {
                setOnClickListener {
                    saleCreditNumberDialog(productData.currentAmount, productData.tokenAddress , productData.userTokenBalance)
                }
            }
        }

        isLike = productData.userFavorite

        if (isLike) {
            binding.likeButtonIcon.setImageResource(R.drawable.like_pin_icon)
        } else {
            binding.likeButtonIcon.setImageResource(R.drawable.like_icon)
        }

        binding.likeButton.setOnClickListener {
            lifecycleScope.launch {
                if (isLike) {
                    putUnLike(productData.id)
                } else {
                    putLike(productData.id)
                }

                isLike = !isLike

                if (isLike) {
                    binding.likeButtonIcon.setImageResource(R.drawable.like_pin_icon)
                } else {
                    binding.likeButtonIcon.setImageResource(R.drawable.like_icon)
                }
            }
        }

    }

    private fun saleCreditNumberDialog(price: Int, tokenAddress: String, maxAmount: Int) {
        var amount = 1

        val customDialog = Dialog(requireContext())
        customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        customDialog.window?.setGravity(Gravity.BOTTOM)

        customDialog.setContentView(R.layout.dialog_token)
        customDialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        customDialog.findViewById<TextView>(R.id.title_text).text = "토큰을 몇 개 판매하시겠어요?"
        customDialog.findViewById<TextView>(R.id.token_text).text = "${amount}개 · "
        customDialog.findViewById<TextView>(R.id.credit_text).text = "${formatAmount(price * amount)} 크레딧"

        customDialog.findViewById<Slider>(R.id.slider).apply {
            valueTo = maxAmount.toFloat()
            addOnChangeListener { _, value, _ ->
                amount = value.toInt()
                customDialog.findViewById<TextView>(R.id.token_text).text = "${value.toInt()}개 · "
                customDialog.findViewById<TextView>(R.id.credit_text).text = "${formatAmount(price * amount)} 크레딧"
            }
        }

        customDialog.findViewById<CardView>(R.id.main_button).setOnClickListener {
            MetamaskModel.connectToEthereum(requireContext()) { _, ethereum ->
                if (ethereum?.selectedAddress == null) {
                    Toast.makeText(requireContext(), "먼저 지갑을 선택해주세요", Toast.LENGTH_SHORT).show()
                    return@connectToEthereum
                }

                ethereum.switchEthereumChain("0x79A") { result ->
                    when (result) {
                        is Result.Success.Items -> {
                            val from = ethereum.selectedAddress
                            val to = "0x1ebf3eBD147E4D16C50c856F84A9e0e3aD672d99"
                            val value = BigInteger.ZERO

                            val amountToSend = Convert.toWei("$amount", Convert.Unit.ETHER)
                            Log.d("METAMASK TRANSFER", amountToSend.toString())

                            val transferFunction = org.web3j.abi.datatypes.Function(
                                "transfer",
                                listOf(Address(to), Uint256(amountToSend.toBigInteger())),
                                listOf(TypeReference.create(Bool::class.java))
                            )
                            val data = FunctionEncoder.encode(transferFunction)

                            val rpcRequest = EthereumRequest(
                                method = "eth_sendTransaction",
                                params = listOf(mutableMapOf(
                                    "from" to from,
                                    "to" to tokenAddress,
                                    "value" to value,
                                    "data" to data,
                                    "gas" to "0x927C0",
                                    "gasPrice" to "0x4a817c800"
                                ))
                            )

                            ethereum.sendRequest(rpcRequest) { transferResult ->
                                when(transferResult) {
                                    is Result.Success.Item -> {
                                        lifecycleScope.launch {
                                            val isSuccess = saleToken(productId!!)
                                            if (isSuccess) {
                                                Toast.makeText(requireContext(), "판매 성공", Toast.LENGTH_SHORT).show()
                                                customDialog.cancel()
                                            } else {
                                                Toast.makeText(requireContext(), "판매 실패", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }

                                    else -> {
                                        Toast.makeText(requireContext(), "판매 실패", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                        else -> {
                            Toast.makeText(requireContext(), "메타 마스크 연결 오류", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }
        }

        customDialog.findViewById<MaterialCardView>(R.id.cancel_button).setOnClickListener {
            customDialog.cancel()
        }

        customDialog.show()
    }

    private fun buyCreditNumberDialog(productId: String, price: Int) {
        var amount = 1

        val customDialog = Dialog(requireContext())
        customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        customDialog.window?.setGravity(Gravity.BOTTOM)

        customDialog.setContentView(R.layout.dialog_token)
        customDialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        customDialog.findViewById<TextView>(R.id.token_text).text = "1개 · "
        customDialog.findViewById<TextView>(R.id.credit_text).text = "${formatAmount(price)} 크레딧"

        customDialog.findViewById<Slider>(R.id.slider).addOnChangeListener { _, value, _ ->
            amount = value.toInt()
            customDialog.findViewById<TextView>(R.id.token_text).text = "${value.toInt()}개 · "
            customDialog.findViewById<TextView>(R.id.credit_text).text = "${formatAmount(price * value)} 크레딧"
        }

        customDialog.findViewById<CardView>(R.id.main_button).setOnClickListener {
            lifecycleScope.launch {
                if (userToken >= price) {
                    val (loadingDialog, setDialogText) = createLoadingDialog(requireContext())
                    loadingDialog.show()
                    setDialogText("구매 중")
                    val isSuccess = buyToken(productId, amount.toString())
                    if (isSuccess) {
                        Toast.makeText(requireContext(), "구매 완료", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "구매 실패", Toast.LENGTH_SHORT).show()
                    }
                    loadingDialog.dismiss()
                    customDialog.cancel()
                } else {
                    Toast.makeText(requireContext(), "잔액 부족, 크레딧을 충전해주세요", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        customDialog.findViewById<MaterialCardView>(R.id.cancel_button).setOnClickListener {
            customDialog.cancel()
        }

        customDialog.show()
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

    private suspend fun saleToken(productId: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.saleToken("bearer ${TokenManager.accessToken}", productId)
                response.isSuccessful
            }
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun buyToken(productId: String, amount: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.buyToken("bearer ${TokenManager.accessToken}", productId, amount)
                response.isSuccessful
            }
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun putLike(productId: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.putLike("bearer ${TokenManager.accessToken}", productId)
                response.isSuccessful
            }
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun putUnLike(productId: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.putUnLike("bearer ${TokenManager.accessToken}", productId)
                response.isSuccessful
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun createLoadingDialog(context: Context): Pair<Dialog, (String) -> Unit> {
        val dialog = Dialog(context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.loading_dialog)

        val textSwitcher = dialog.findViewById<TextSwitcher>(R.id.text_switcher)
        textSwitcher.setFactory {
            val textView = TextView(context)
            textView.gravity = Gravity.CENTER
            textView.setTextColor(Color.WHITE)
            textView.textSize = 16f
            textView.typeface = ResourcesCompat.getFont(context, R.font.wanted_semibold)
            textView
        }

        val inAnim = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
        val outAnim = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right)

        textSwitcher.inAnimation = inAnim
        textSwitcher.outAnimation = outAnim

        val setText: (String) -> Unit = { newText ->
            textSwitcher.setText(newText)
        }

        return Pair(dialog, setText)
    }

    private fun bindTabLayout(productData: ProductResponse) {
        val qnaList = productData.productQnA.map { Chat(it.content, if (it.authorType == "BUYER") ChatType.Question else ChatType.Answer) }.toMutableList()
        qnaList.add(Chat("", ChatType.Button, productData.id))

        val imageAdapter = ImageAdapter(productData.detailImages)
        val qnaAdapter = ChatAdapter(qnaList)

        binding.bottomList.apply {
            binding.defaultImage.visibility = View.GONE
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
            adapter = imageAdapter
        }

        binding.buyButton.setOnClickListener {
            tokenDialog(requireContext())
        }

        binding.tabLayout.apply {
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    val tabView = tab.customView?.findViewById<TextView>(R.id.tab_text)
                    tabView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.Text_Default_Primary))

                    when (tab.position) {
                        0 -> {
                            binding.defaultImage.visibility = View.GONE
                            binding.bottomList.apply {
                                adapter = imageAdapter
                                layoutManager = LinearLayoutManager(requireContext())
                            }
                        }
                        1 -> {
                            binding.bottomList.apply {
                                adapter = qnaAdapter
                                layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
                            }
                            if (qnaAdapter.itemCount <= 1) {
                                binding.defaultImage.visibility = View.VISIBLE
                            } else {

                                binding.defaultImage.visibility = View.GONE
                            }
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    val tabView = tab.customView?.findViewById<TextView>(R.id.tab_text)
                    tabView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.Text_Status_Unselected))
                }

                override fun onTabReselected(tab: TabLayout.Tab) {
                }
            })
        }
    }

    private fun formatFundingDate(dateString: String): String {
        val zoneKST = ZoneId.of("Asia/Seoul")
        val zonedDateTime = Instant.parse(dateString).atZone(zoneKST)

        val formatter = DateTimeFormatter.ofPattern("M월 dd일")
        return zonedDateTime.format(formatter)
    }

    private fun tokenDialog(context: Context) {
        val customDialog = Dialog(context)
        customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        customDialog.window?.setGravity(Gravity.BOTTOM)

        customDialog.setContentView(R.layout.dialog_token)
        customDialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        customDialog.findViewById<Slider>(R.id.slider).addOnChangeListener { _, value, _ ->
            customDialog.findViewById<TextView>(R.id.token_text).text = "${value.toInt()}개 · "
            customDialog.findViewById<TextView>(R.id.credit_text).text = "${value.toInt() * 10} 크레딧"
        }

        customDialog.findViewById<CardView>(R.id.main_button).setOnClickListener {
            customDialog.cancel()
        }

        customDialog.findViewById<MaterialCardView>(R.id.cancel_button).setOnClickListener {
            customDialog.cancel()
        }

        customDialog.show()
    }

    private fun createLineDataSet(chartData: List<Graph>): LineDataSet {
        val values = chartData.map { Entry(it.x.toFloat(), it.y.toFloat()) }

        return LineDataSet(values, "").apply {
            color = resources.getColor(R.color.background_accent_Default)
            setDrawFilled(true)
            setDrawCircles(false)
            setDrawValues(false)
            fillAlpha = 80
            fillColor = resources.getColor(R.color.background_accent_Default)
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }
    }

    private fun setupLineChart(lineChart: LineChart, lineData: LineData) {
        lineChart.data = lineData
        lineChart.description.isEnabled = false
        lineChart.xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        lineChart.axisRight.isEnabled = false
        lineChart.axisLeft.setDrawGridLines(false)
        lineChart.xAxis.setDrawGridLines(false)
        lineChart.setTouchEnabled(false)
        lineChart.isDragEnabled = false
        lineChart.isScaleXEnabled = false
        lineChart.isScaleYEnabled = false
        lineChart.isHighlightPerDragEnabled = false
        lineChart.legend.isEnabled = false
        lineChart.xAxis.setDrawLabels(false)
        lineChart.axisLeft.setDrawLabels(false)
        lineChart.invalidate()
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


    private fun createTabView(tabName: String, isChecked: Boolean = false): View {
        val tabView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab, null)
        val tabTextView = tabView.findViewById<TextView>(R.id.tab_text)
        tabTextView.text = tabName
        if (isChecked) {
            tabTextView.setTextColor(resources.getColor(R.color.Text_Default_Primary))
        }
        return tabView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}