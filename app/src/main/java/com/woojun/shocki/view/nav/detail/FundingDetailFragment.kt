package com.woojun.shocki.view.nav.detail

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
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
import com.woojun.shocki.network.RetrofitAPI
import com.woojun.shocki.network.RetrofitClient
import com.woojun.shocki.util.Util.calculateEndDate
import com.woojun.shocki.util.Util.formatAmount
import com.woojun.shocki.util.Util.getProduct
import com.woojun.shocki.view.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
        binding.fundingDate.text = "${formatFundingDate(productData.fundingEndDate)}일"
        binding.nowFundingAmount.text = "${formatAmount(productData.collectedAmount)}크레딧 ${(productData.collectedAmount / productData.targetAmount) * 100}%"
        binding.goalFundingPrice.text = "${formatAmount(productData.targetAmount)}크레딧"

        binding.graphView.apply {
            val dataSet = createLineDataSet(productData.graph)
            val lineData = LineData(dataSet)
            setupLineChart(this, lineData)
        }

        bindTabLayout(productData)

        binding.buyButton.apply {
            if (!productData.purchaseIsDisabled) {
                isEnabled = false
                strokeColor = resources.getColor(R.color.background_gray_Border)
                setCardBackgroundColor(resources.getColor(R.color.background_gray_Default))
                binding.buyText.setTextColor(resources.getColor(R.color.Text_Status_Unable))
            } else {
                setOnClickListener {
                    creditNumberDialog(productData.id, productData.currentAmount)
                }
            }
        }

        binding.saleButton.apply {
            if (!productData.saleIsDisabled) {
                isEnabled = false
                strokeColor = resources.getColor(R.color.background_gray_Border)
                setCardBackgroundColor(resources.getColor(R.color.background_gray_Default))
                binding.saleText.setTextColor(resources.getColor(R.color.Text_Status_Unable))
            } else {
                setOnClickListener {
                    // TODO 미정
                    Toast.makeText(requireContext(), "판매는 웹에서 가능합니다", Toast.LENGTH_SHORT).show()
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

    private fun creditNumberDialog(productId: String, price: Int) {
        var tokenPrice = price
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
            customDialog.findViewById<TextView>(R.id.credit_text).text = "${formatAmount(tokenPrice)} 크레딧"
        }

        customDialog.findViewById<CardView>(R.id.main_button).setOnClickListener {
            lifecycleScope.launch {
                if (userToken >= tokenPrice) {
                    val isSuccess = buyToken(productId, amount.toString())
                    if (isSuccess) {
                        Toast.makeText(requireContext(), "구매 완료", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "구매 실패", Toast.LENGTH_SHORT).show()
                    }
                    customDialog.cancel()
                } else {
                    Toast.makeText(requireContext(), "잔액 부족, 크레딧을 충전해주세요", Toast.LENGTH_SHORT).show()
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
            addTab(newTab().setCustomView(createTabView("제품소개")))
            addTab(newTab().setCustomView(createTabView("QnA")))

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


    private fun createTabView(tabName: String): View {
        val tabView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab, null)
        val tabTextView = tabView.findViewById<TextView>(R.id.tab_text)
        tabTextView.text = tabName
        return tabView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}