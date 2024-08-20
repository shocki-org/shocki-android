package com.woojun.shocki.auth

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.woojun.shocki.R
import com.woojun.shocki.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var passwordToggle = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.passwordToggleButton.setOnClickListener {
            if (!passwordToggle) {
                (it as ImageView).setImageResource(R.drawable.password_hide_icon)
                binding.passwordInput.apply {
                    this.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    this.setSelection(this.text.length)
                }
            } else {
                (it as ImageView).setImageResource(R.drawable.password_show_icon)
                binding.passwordInput.apply {
                    this.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    this.setSelection(this.text.length)
                }
            }
            passwordToggle = !passwordToggle
        }

        binding.finishButton.setOnClickListener { findNavController().popBackStack() }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}