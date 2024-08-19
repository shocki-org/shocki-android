package com.woojun.shocki.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.woojun.shocki.databinding.FragmentAuthHomeBinding

class AuthHomeFragment : Fragment() {

    private var _binding: FragmentAuthHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.emailSignInButton.setOnClickListener { emailSignIn() }
        binding.kakaoLoginButton.setOnClickListener { kakaoLogin() }
        binding.googleLoginButton.setOnClickListener { googleLogin() }
        binding.emailLoginButton.setOnClickListener { emailLogin() }
    }

    private fun emailSignIn() {
    }

    private fun kakaoLogin() {}

    private fun googleLogin() {}

    private fun emailLogin() {}

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}