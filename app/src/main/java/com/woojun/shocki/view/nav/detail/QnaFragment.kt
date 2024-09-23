package com.woojun.shocki.view.nav.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.woojun.shocki.R
import com.woojun.shocki.database.TokenManager
import com.woojun.shocki.databinding.FragmentQnaBinding
import com.woojun.shocki.dto.QnaRequest
import com.woojun.shocki.network.RetrofitAPI
import com.woojun.shocki.network.RetrofitClient
import com.woojun.shocki.view.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QnaFragment : Fragment() {

    private var _binding: FragmentQnaBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQnaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val productId = arguments?.getString("productId")
        if (productId != null) {
            binding.writeButton.setOnClickListener {
                lifecycleScope.launch {
                    val isSuccess = postQnA(QnaRequest(binding.input.text.toString(), productId))
                    if (isSuccess) {
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "에러 발생", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(requireContext(), "에러 발생", Toast.LENGTH_SHORT).show()
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }



    }

    private suspend fun postQnA(qnaRequest: QnaRequest): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.postQnA("bearer ${TokenManager.accessToken}", qnaRequest)
                response.isSuccessful
            }
        } catch (e: Exception) {
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}