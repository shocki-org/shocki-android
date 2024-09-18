package com.woojun.shocki.view.nav.store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.woojun.shocki.R
import com.woojun.shocki.database.MainViewModel
import com.woojun.shocki.databinding.FragmentStoreBinding
import com.woojun.shocki.view.main.MainActivity

class StoreFragment : Fragment() {

    private var _binding: FragmentStoreBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel

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
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        binding.shippingButton.setOnClickListener {
            activity.animationNavigate(R.id.shipping)
        }

        binding.searchButton.setOnClickListener {
            activity.animationNavigate(R.id.search)
        }

         mainViewModel.sellingList.observe(viewLifecycleOwner) { sellingList ->
             if (sellingList != null) {
                 binding.storeList.apply {
                     layoutManager = GridLayoutManager(requireContext(), 2)
                     adapter = StoreAdapter(sellingList)
                 }
             } else {
                 Toast.makeText(requireContext(), "상품 목록 조회를 실패했습니다", Toast.LENGTH_SHORT).show()
             }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}