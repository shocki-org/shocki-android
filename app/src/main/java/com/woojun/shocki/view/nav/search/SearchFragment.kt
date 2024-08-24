package com.woojun.shocki.view.nav.search

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.woojun.shocki.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

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
            this.layoutManager = LinearLayoutManager(requireContext())
            this.adapter = SearchAdapter(listOf(""))
        }

        binding.searchInput.apply {
            this.setOnEditorActionListener(object : TextView.OnEditorActionListener{
                override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH && binding.searchInput.text.isNotEmpty()){
                        binding.searchList.adapter = SearchAdapter(listOf(binding.searchInput.text.toString()))
                        return true
                    }
                    return false
                }
            })
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}