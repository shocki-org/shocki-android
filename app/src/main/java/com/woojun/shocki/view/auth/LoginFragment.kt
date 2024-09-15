package com.woojun.shocki.view.auth

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
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
import com.woojun.shocki.dto.PostLoginRequest
import com.woojun.shocki.util.Util.checkPassword
import com.woojun.shocki.util.Util.checkPhone
import com.woojun.shocki.util.Util.saveToken
import com.woojun.shocki.view.auth.AuthActivity.Companion.PHONE


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
                    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    setSelection(text.length)
                }
            } else {
                (it as ImageView).setImageResource(R.drawable.password_show_icon)
                binding.passwordInput.apply {
                    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    setSelection(text.length)
                }
            }
            passwordToggle = !passwordToggle
        }

        binding.backButton.setOnClickListener { findNavController().popBackStack() }

        binding.finishButton.setOnClickListener {
            val phone = binding.phoneInput.text.toString()
            val password = binding.passwordInput.toString()

            if (checkPhone(phone) && checkPassword(password)) {
                saveToken(requireActivity(), PostLoginRequest("", password, phone, PHONE))
            }
        }

        binding.phoneInput.apply {
            addTextChangedListener(PhoneNumberFormattingTextWatcher())
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                }
                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                }
                override fun afterTextChanged(editable: Editable) {
                    if (checkField()) {
                        binding.noneButton.visibility = View.GONE
                        binding.finishButton.visibility = View.VISIBLE
                    } else {
                        binding.finishButton.visibility = View.GONE
                        binding.noneButton.visibility = View.VISIBLE
                    }
                }
            })
        }

        binding.passwordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }
            override fun afterTextChanged(editable: Editable) {
                if (checkField()) {
                    binding.noneButton.visibility = View.GONE
                    binding.finishButton.visibility = View.VISIBLE
                } else {
                    binding.finishButton.visibility = View.GONE
                    binding.noneButton.visibility = View.VISIBLE
                }
            }
        })

    }

    private fun checkField(): Boolean {
        return binding.passwordInput.text.isNotEmpty() && binding.phoneInput.text.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}