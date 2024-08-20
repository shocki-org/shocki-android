package com.woojun.shocki.view.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.woojun.shocki.R
import com.woojun.shocki.databinding.FragmentLoginBinding
import com.woojun.shocki.view.main.MainActivity
import com.woojun.shocki.util.Util.checkEmail
import com.woojun.shocki.util.Util.checkPassword


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

        binding.backButton.setOnClickListener { findNavController().popBackStack() }

        binding.finishButton.setOnClickListener {
            if (emailLogin()) {
                startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finishAffinity()
            }
        }

        binding.emailInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }
            override fun afterTextChanged(editable: Editable) {
                if (checkEmail(binding.emailInput.text.toString()) && checkPassword(binding.passwordInput.text.toString())) {
                    binding.noneButton.visibility = View.GONE
                    binding.finishButton.visibility = View.VISIBLE
                } else {
                    binding.finishButton.visibility = View.GONE
                    binding.noneButton.visibility = View.VISIBLE
                }
            }
        })

        binding.passwordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }
            override fun afterTextChanged(editable: Editable) {
                if (checkEmail(binding.emailInput.text.toString()) && checkPassword(binding.passwordInput.text.toString())) {
                    binding.noneButton.visibility = View.GONE
                    binding.finishButton.visibility = View.VISIBLE
                } else {
                    binding.finishButton.visibility = View.GONE
                    binding.noneButton.visibility = View.VISIBLE
                }
            }
        })

    }

    private fun emailLogin(): Boolean {
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}