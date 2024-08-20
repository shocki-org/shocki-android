package com.woojun.shocki.view.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.woojun.shocki.R
import com.woojun.shocki.databinding.FragmentSignUpBinding
import com.woojun.shocki.view.main.MainActivity
import com.woojun.shocki.util.Util.checkEmail
import com.woojun.shocki.util.Util.checkPassword

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private var index = 0
    private var passwordToggle = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

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

        binding.nextButton.setOnClickListener {
            nextInputAction()
        }

        binding.emailInput.apply {
            this.setOnEditorActionListener(object : TextView.OnEditorActionListener{
                override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_GO && binding.emailInput.text.isNotEmpty()){
                        nextInputAction()
                        binding.codeInput.requestFocus()
                        inputMethodManager.showSoftInput(binding.codeInput, InputMethodManager.SHOW_IMPLICIT)
                        return true
                    }
                    return false
                }
            })
            this.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                }
                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    if (binding.emailText.text.isNotEmpty()) {
                        binding.nextButton.visibility = View.VISIBLE
                        binding.noneButton.visibility = View.GONE
                    } else {
                        binding.noneButton.visibility = View.VISIBLE
                        binding.nextButton.visibility = View.GONE
                    }
                }
                override fun afterTextChanged(editable: Editable) {
                }
            })
        }

        binding.codeInput.apply {
            this.setOnEditorActionListener(object : TextView.OnEditorActionListener{
                override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_GO && binding.codeInput.text.isNotEmpty()){
                        nextInputAction()
                        binding.passwordInput.requestFocus()
                        inputMethodManager.showSoftInput(binding.passwordInput, InputMethodManager.SHOW_IMPLICIT)
                        return true
                    }
                    return false
                }
            })
            this.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                }
                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    if (binding.codeInput.text.isNotEmpty()) {
                        binding.nextButton.visibility = View.VISIBLE
                        binding.noneButton.visibility = View.GONE
                    } else {
                        binding.noneButton.visibility = View.VISIBLE
                        binding.nextButton.visibility = View.GONE
                    }
                }
                override fun afterTextChanged(editable: Editable) {
                }
            })
        }

        binding.passwordInput.apply {
            this.setOnEditorActionListener(object : TextView.OnEditorActionListener{
                override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_DONE && binding.passwordInput.text.isNotEmpty()){
                        nextInputAction()
                        return true
                    }
                    return false
                }
            })
            this.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                }
                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                }
                override fun afterTextChanged(editable: Editable) {
                    if (binding.passwordText.text.isNotEmpty()) {
                        binding.nextButtonText.text = "완료"
                        binding.nextButton.visibility = View.VISIBLE
                        binding.noneButton.visibility = View.GONE
                    } else {
                        binding.nextButtonText.text = "다음"
                        binding.noneButton.visibility = View.VISIBLE
                        binding.nextButton.visibility = View.GONE
                    }
                }
            })
        }
    }

    private fun switchText(index: Int) {
        val texts = arrayOf(
            "인증코드를 입력해주세요",
            "비밀번호를 입력해주세요"
        )
        binding.textView.setText(texts[index])
    }

    private fun checkCode(code: String): Boolean {
        return code.isNotEmpty()
    }

    fun nextInputAction() {
        when(index) {
            0 -> {
                if (checkEmail(binding.emailInput.text.toString())) {
                    showViewWithAnimation(binding.emailBox, requireContext())
                    showViewWithAnimation(binding.codeBox, requireContext())

                    binding.emailInput.isEnabled = false
                    switchText(index)

                    index = 1
                } else {
                    Toast.makeText(requireContext(), "유효한 이메일 주소가 아닙니다", Toast.LENGTH_SHORT).show()
                }
            }
            1 -> {
                if (checkCode(binding.codeInput.text.toString())) {
                    showViewWithAnimation(binding.emailBox, requireContext())
                    showViewWithAnimation(binding.codeBox, requireContext())
                    showViewWithAnimation(binding.passwordBox, requireContext())

                    switchText(index)
                    binding.codeInput.isEnabled = false

                    index = 2
                } else {
                    Toast.makeText(requireContext(), "유효한 코드 아닙니다", Toast.LENGTH_SHORT).show()
                }
            }
            2 -> {
                if (checkPassword(binding.passwordInput.text.toString())) {
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().finishAffinity()
                } else {
                    Toast.makeText(requireContext(), "유효한 비밀번호가 아닙니다", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showViewWithAnimation(view: View, context: Context) {
        val slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom)
        view.startAnimation(slideIn)
        view.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}