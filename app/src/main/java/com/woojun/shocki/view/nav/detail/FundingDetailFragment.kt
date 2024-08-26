package com.woojun.shocki.view.nav.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.woojun.shocki.R
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

        val adapter1 = ImageAdapter(listOf(R.drawable.banner1, R.drawable.banner2, R.drawable.banner3, R.drawable.banner4, R.drawable.banner5))
        val adapter2 = ChatAdapter(listOf())

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
                            binding.recyclerView.visibility = View.VISIBLE
                            binding.recyclerView.adapter = adapter1
                        }
                        1 -> {
                            if (adapter2.itemCount == 0) {
                                binding.recyclerView.visibility = View.GONE
                                binding.defaultImage.visibility = View.VISIBLE
                            } else {
                                binding.recyclerView.visibility = View.VISIBLE
                                binding.defaultImage.visibility = View.GONE
                                binding.recyclerView.adapter = adapter2
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