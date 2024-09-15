package com.woojun.shocki.view.nav.payment

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.woojun.shocki.R
import com.woojun.shocki.databinding.FragmentPaymentBinding
import com.woojun.shocki.view.main.MainActivity

class PaymentFragment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    private var paymentOption: Int? = null
    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRadioButton()

        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.nextButton.setOnClickListener {
            nextAction()
        }

        binding.countInput.apply {
            this.setOnEditorActionListener(object : TextView.OnEditorActionListener{
                override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_GO && binding.countInput.text.isNotEmpty()){
                        nextAction()
                        binding.phoneInput.requestFocus()
                        inputMethodManager.showSoftInput(binding.phoneInput, InputMethodManager.SHOW_IMPLICIT)
                        return true
                    }
                    return false
                }
            })
            this.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                }
                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    if (binding.countInput.text.isNotEmpty()) {
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

        binding.phoneInput.apply {
            this.setOnEditorActionListener(object : TextView.OnEditorActionListener{
                override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_GO && binding.phoneInput.text.isNotEmpty()){
                        nextAction()
                        binding.addressInput.requestFocus()
                        inputMethodManager.showSoftInput(binding.addressInput, InputMethodManager.SHOW_IMPLICIT)
                        return true
                    }
                    return false
                }
            })
            this.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                }
                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    if (binding.phoneInput.text.isNotEmpty()) {
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

        binding.addressInput.apply {
            this.setOnEditorActionListener(object : TextView.OnEditorActionListener{
                override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_GO && binding.addressInput.text.isNotEmpty()){
                        nextAction()
                        return true
                    }
                    return false
                }
            })
            this.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                }
                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    if (binding.addressInput.text.isNotEmpty()) {
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

    }

    private fun nextAction() {
        when(index) {
            0 -> {
                val defaultColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Text_Status_Unable))
                when(paymentOption) {
                    R.id.token -> {
                        binding.token.apply {
                            this.isEnabled = false
                            this.buttonTintList = defaultColor
                            this.visibility = View.VISIBLE
                        }

                        binding.credit.visibility = View.GONE
                        binding.cash.visibility = View.GONE
                    }
                    R.id.credit -> {
                        binding.credit.apply {
                            this.isEnabled = false
                            this.buttonTintList = defaultColor
                            this.visibility = View.VISIBLE
                        }

                        binding.token.visibility = View.GONE
                        binding.cash.visibility = View.GONE
                    }
                    R.id.cash -> {
                        binding.cash.apply {
                            this.isEnabled = false
                            this.buttonTintList = defaultColor
                            this.visibility = View.VISIBLE
                        }

                        binding.credit.visibility = View.GONE
                        binding.token.visibility = View.GONE
                    }
                }

                switchText(index)
                showViewWithAnimation(binding.radioGroup, requireContext())
                showViewWithAnimation(binding.countBox, requireContext())
                index = 1
            }
            1 -> {
                switchText(index)
                showViewWithAnimation(binding.radioGroup, requireContext())
                showViewWithAnimation(binding.countBox, requireContext())
                showViewWithAnimation(binding.phoneBox, requireContext())
                binding.countInput.isEnabled = false

                index = 2
            }
            2 -> {
                switchText(index)
                showViewWithAnimation(binding.radioGroup, requireContext())
                showViewWithAnimation(binding.countBox, requireContext())
                showViewWithAnimation(binding.phoneBox, requireContext())
                showViewWithAnimation(binding.addressBox, requireContext())
                binding.nextButtonText.text = "완료"
                binding.phoneInput.isEnabled = false

                index = 3
            }
            3 -> {
                if (binding.addressInput.text.isNotEmpty()) {
                    (requireActivity() as MainActivity).animationNavigate(R.id.store)
                } else {
                    Toast.makeText(requireContext(), "주소를 입력해주세요", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun switchText(index: Int) {
        val texts = arrayOf(
            "몇 개를 구매할건지\n선택해주세요",
            "상품을 받을\n전화번호를 입력해주세요",
            "상품을 받을\n주소를 입력해주세요"
        )
        binding.textView.setText(texts[index])
    }

    private fun showViewWithAnimation(view: View, context: Context) {
        val slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom)
        view.startAnimation(slideIn)
        view.visibility = View.VISIBLE
    }

    private fun setRadioButton() {
        val defaultColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.background_gray_Border))
        val selectedColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.background_accent_Default))

        fun initRadioButtonColor() {
            for (i in 0 until binding.radioGroup.childCount) {
                val radioButton = binding.radioGroup.getChildAt(i) as RadioButton
                radioButton.buttonTintList = defaultColor
            }
        }

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            initRadioButtonColor()
            when(checkedId) {
                R.id.token -> binding.token.buttonTintList = selectedColor
                R.id.credit -> binding.credit.buttonTintList = selectedColor
                R.id.cash -> binding.cash.buttonTintList = selectedColor
            }
            paymentOption = checkedId
        }
        initRadioButtonColor()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}