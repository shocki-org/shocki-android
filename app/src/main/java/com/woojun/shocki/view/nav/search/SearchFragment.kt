package com.woojun.shocki.view.nav.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.woojun.shocki.data.SearchType
import com.woojun.shocki.database.TokenManager
import com.woojun.shocki.databinding.FragmentSearchBinding
import com.woojun.shocki.dto.SearchResponse
import com.woojun.shocki.network.RetrofitAPI
import com.woojun.shocki.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val queryFlow = MutableStateFlow("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.searchList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = SearchAdapter(listOf(), SearchType.Default)
        }

        binding.searchInput.apply {
            this.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH && text.isNotEmpty()) {
                    queryFlow.value = text.toString()
                    true
                } else {
                    false
                }
            }

            this.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    queryFlow.value = s.toString()
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
        }

        lifecycleScope.launch {
            queryFlow
                .debounce(300)
                .collectLatest { query ->
                    if (query.isNotEmpty()) {
                        val searchList = getSearch(query)
                        if (searchList != null) updateRecyclerView(searchList)
                    }
                }
        }

    }

    private suspend fun getSearch(keyword: String): List<SearchResponse>? {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.getSearch("bearer ${TokenManager.accessToken}", keyword)
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

    private fun updateRecyclerView(searchResponse: List<SearchResponse>) {
        if (searchResponse.isEmpty()) {
            binding.searchList.adapter = SearchAdapter(listOf(), SearchType.None)
        } else {
            binding.searchList.adapter = SearchAdapter(searchResponse, SearchType.Item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}