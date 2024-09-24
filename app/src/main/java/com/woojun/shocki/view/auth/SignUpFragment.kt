package com.woojun.shocki.view.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.woojun.shocki.R
import com.woojun.shocki.database.TokenManager
import com.woojun.shocki.databinding.FragmentSignUpBinding
import com.woojun.shocki.dto.AccessTokenResponse
import com.woojun.shocki.dto.PhoneFinalRequest
import com.woojun.shocki.dto.PhoneFirstRequest
import com.woojun.shocki.dto.PhoneSecondRequest
import com.woojun.shocki.dto.PhoneSecondResponse
import com.woojun.shocki.network.RetrofitAPI
import com.woojun.shocki.network.RetrofitClient
import com.woojun.shocki.util.Util.checkPassword
import com.woojun.shocki.util.Util.checkPhone
import com.woojun.shocki.util.Util.formatPhoneNumber
import com.woojun.shocki.view.main.MainActivity
import com.woojun.shocki.view.wallet.WalletActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private var index = 0
    private var passwordToggle = true

    private var phone = ""
    private var token = ""

    private var lastCallTime = 0L

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

        binding.nextButton.setOnClickListener {
            nextInputAction()
        }

        binding.phoneInput.apply {
            setOnEditorActionListener(object : TextView.OnEditorActionListener{
                override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_GO && binding.phoneInput.text.isNotEmpty()){
                        nextInputAction()
                        binding.codeInput.requestFocus()
                        inputMethodManager.showSoftInput(binding.codeInput, InputMethodManager.SHOW_IMPLICIT)
                        return true
                    }
                    return false
                }
            })
            addTextChangedListener(PhoneNumberFormattingTextWatcher())
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                }
                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    if (binding.phoneText.text.isNotEmpty()) {
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
            setOnEditorActionListener(object : TextView.OnEditorActionListener{
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
            addTextChangedListener(object : TextWatcher {
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
            setOnEditorActionListener(object : TextView.OnEditorActionListener{
                override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_DONE && binding.passwordInput.text.isNotEmpty()){
                        nextInputAction()
                        return true
                    }
                    return false
                }
            })
            addTextChangedListener(object : TextWatcher {
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

    private suspend fun postCode(phone: String): Boolean? {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.phoneFirst(PhoneFirstRequest(phone))
                if (response.isSuccessful) {
                    true
                } else if (response.code() == 409) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "이미 가입된 번호입니다", Toast.LENGTH_SHORT).show()
                    }
                    null
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "코드 전송을 실패하였습니다", Toast.LENGTH_SHORT).show()
                    }
                    null
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "코드 전송을 실패하였습니다", Toast.LENGTH_SHORT).show()
            }
            null
        }
    }

    private suspend fun checkCode(code: String, phone: String): PhoneSecondResponse? {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.phoneSecond(PhoneSecondRequest(code, phone))
                if (response.isSuccessful) {
                    response.body()
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "코드 확인을 실패하였습니다", Toast.LENGTH_SHORT).show()
                    }
                    null
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "코드 확인을 실패하였습니다", Toast.LENGTH_SHORT).show()
            }
            null
        }
    }

    private suspend fun signUp(password: String): AccessTokenResponse? {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.phoneFinal(PhoneFinalRequest(password, token))
                if (response.isSuccessful) {
                    response.body()
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "회원가입을 실패하였습니다", Toast.LENGTH_SHORT).show()
                    }
                    null
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "회원가입을 실패하였습니다", Toast.LENGTH_SHORT).show()
            }
            null
        }
    }

    private fun nextInputAction() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastCallTime >= 3000) {
            lastCallTime = currentTime
            lifecycleScope.launch {
                when(index) {
                    0 -> {
                        val phone = binding.phoneInput.text.toString()
                        if (checkPhone(phone)) {
                            postCode(formatPhoneNumber(phone))?.let{
                                showViewWithAnimation(binding.phoneBox, requireContext())
                                showViewWithAnimation(binding.codeBox, requireContext())

                                binding.phoneInput.isEnabled = false
                                binding.phoneText.setTextColor(resources.getColor(R.color.Text_Status_Unable))
                                switchText(index)

                                index = 1
                            }
                        } else {
                            Toast.makeText(requireContext(), "유효한 전화번호가 아닙니다", Toast.LENGTH_SHORT).show()
                        }
                    }
                    1 -> {
                        val phone = formatPhoneNumber(binding.phoneInput.text.toString())
                        val code = binding.codeInput.text.toString()

                        if (code.isNotEmpty()) {
                            checkCode(code, phone)?.let {
                                this@SignUpFragment.phone = phone
                                token = it.token

                                showViewWithAnimation(binding.phoneBox, requireContext())
                                showViewWithAnimation(binding.codeBox, requireContext())
                                showViewWithAnimation(binding.passwordBox, requireContext())

                                switchText(index)
                                binding.codeInput.isEnabled = false
                                binding.codeText.setTextColor(resources.getColor(R.color.Text_Status_Unable))


                                index = 2
                            }
                        } else {
                            Toast.makeText(requireContext(), "유효한 코드가 아닙니다", Toast.LENGTH_SHORT).show()
                        }
                    }
                    2 -> {
                        val password = binding.passwordInput.text.toString()
                        if (checkPassword(password)) {
                            signUp(password)?.let {
                                TokenManager.accessToken = it.accessToken
                                requireActivity().startActivity(Intent(requireActivity(), WalletActivity::class.java))
                                requireActivity().finishAffinity()
                            }
                        } else {
                            Toast.makeText(requireContext(), "유효한 비밀번호가 아닙니다", Toast.LENGTH_SHORT).show()
                            Toast.makeText(requireContext(), "영문 대소문자, 특수문자, 숫자로 8자리 입력해주세요", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun showViewWithAnimation(view: View, context: Context) {
        val slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom)
        view.startAnimation(slideIn)
        view.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}