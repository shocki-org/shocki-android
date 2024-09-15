package com.woojun.shocki.view.nav.shipping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.woojun.shocki.databinding.FragmentShippingBinding

class ShippingFragment : Fragment() {

    private var _binding: FragmentShippingBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShippingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.shippingList.apply {
            this.layoutManager = LinearLayoutManager(requireContext())
            this.adapter = ShippingAdapter(testList())
        }


    }

    private fun testList(): List<String> {
        return listOf(
            "활용법까지 알려주는 디자이너 PICK <무료 폰트 가이드>",
            "활용법까지 알려주는 디자이너 PICK <무료 폰트 가이드>",
            "활용법까지 알려주는 디자이너 PICK <무료 폰트 가이드>",
            "활용법까지 알려주는 디자이너 PICK <무료 폰트 가이드>"
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}