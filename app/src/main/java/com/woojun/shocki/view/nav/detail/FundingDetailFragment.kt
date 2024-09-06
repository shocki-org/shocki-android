package com.woojun.shocki.view.nav.detail

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.tabs.TabLayout
import com.woojun.shocki.R
import com.woojun.shocki.data.Chat
import com.woojun.shocki.data.ChatType
import com.woojun.shocki.databinding.FragmentFundingDetailBinding

class FundingDetailFragment : Fragment() {
    private var _binding: FragmentFundingDetailBinding? = null
    private val binding get() = _binding!!

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

        val adapter1 = ImageAdapter(listOf(R.drawable.banner1, R.drawable.banner2, R.drawable.banner3, R.drawable.banner4, R.drawable.banner5))
        val adapter2 = ChatAdapter(listOf(Chat("제품 구성품은 무엇이 있나요?", ChatType.Question), Chat("이것 저것 등등등이 들어가있습니다", ChatType.Answer), Chat("", ChatType.Button)))

        binding.graphView.apply {
            val dataSet = createLineDataSet()
            val lineData = LineData(dataSet)
            setupLineChart(this, lineData)
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapter1
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
                            binding.recyclerView.apply {
                                visibility = View.VISIBLE
                                adapter = adapter1
                                layoutManager = LinearLayoutManager(requireContext())
                            }
                        }
                        1 -> {
                            if (adapter2.itemCount == 0) {
                                binding.recyclerView.visibility = View.GONE
                                binding.defaultImage.visibility = View.VISIBLE
                            } else {
                                binding.recyclerView.apply {
                                    visibility = View.VISIBLE
                                    adapter = adapter2
                                    layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
                                }
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

    private fun createLineDataSet(): LineDataSet {
        val values = ArrayList<Entry>()
        for (i in 0 until 10) {
            values.add(Entry(i.toFloat(), (Math.random() * 100).toFloat()))
        }

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

            binding.backButton.setPadding(0, statusBarHeight, 0, navBarHeight)
            insets
        }
    }


    private fun createTabView(tabName: String): View {
        val tabView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab, null)
        val tabTextView = tabView.findViewById<TextView>(R.id.tab_text)
        tabTextView.text = tabName
        return tabView
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}