package com.woojun.shocki.view.nav.profile

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView
import com.woojun.shocki.R
import com.woojun.shocki.database.SharedPreference
import com.woojun.shocki.database.TokenManager
import com.woojun.shocki.databinding.FragmentSettingBinding
import com.woojun.shocki.network.RetrofitAPI
import com.woojun.shocki.network.RetrofitClient
import com.woojun.shocki.view.auth.AuthActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.logoutButton.setOnClickListener {
            createDialog("계정에서 로그아웃하시겠어요?", "계정에서 로그아웃 할 시,\n" + "로그인 할 때까지 Shocki를 사용하지 못해요") {
                TokenManager.accessToken = ""
                TokenManager.fcmToken = ""
                SharedPreference.walletAddress = ""
                SharedPreference.isTestMode = false
                startActivity(Intent(requireContext(), AuthActivity::class.java))
                requireActivity().finishAffinity()
            }
        }

        binding.withdrawalButton.setOnClickListener {
            createDialog("계정에서 탈퇴하시겠어요?", "계정에서 탈퇴 할 시,\n" + "계정을 생성 할 때까지 Shocki를 사용하지 못해요") {
                lifecycleScope.launch {
                    val isSuccess = deleteUser()
                    if (isSuccess) {
                        TokenManager.accessToken = ""
                        TokenManager.fcmToken = ""
                        SharedPreference.walletAddress = ""
                        SharedPreference.isTestMode = false
                        startActivity(Intent(requireContext(), AuthActivity::class.java))
                        requireActivity().finishAffinity()
                    } else {
                        Toast.makeText(requireContext(), "회원 탈퇴를 실패하셨습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    private fun createDialog(title: String, content: String, action: () -> Unit) {
        val customDialog = Dialog(requireContext())
        customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        customDialog.window?.setGravity(Gravity.CENTER)

        customDialog.setContentView(R.layout.dialog_setting)
        customDialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        customDialog.findViewById<TextView>(R.id.title_text).text = title
        customDialog.findViewById<TextView>(R.id.content_text).text = content

        customDialog.findViewById<CardView>(R.id.main_button).setOnClickListener {
            action()
            customDialog.cancel()
        }

        customDialog.findViewById<MaterialCardView>(R.id.cancel_button).setOnClickListener {
            customDialog.cancel()
        }

        customDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun deleteUser(): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.deleteUser("bearer ${TokenManager.accessToken}")
                response.isSuccessful
            }
        } catch (e: Exception) {
            false
        }
    }

}