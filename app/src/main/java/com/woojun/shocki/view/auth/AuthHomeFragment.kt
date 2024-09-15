package com.woojun.shocki.view.auth

import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.woojun.shocki.R
import com.woojun.shocki.databinding.FragmentAuthHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.woojun.shocki.BuildConfig
import com.woojun.shocki.dto.PostLoginRequest
import com.woojun.shocki.util.Util.saveToken
import com.woojun.shocki.view.auth.AuthActivity.Companion.GOOGLE
import com.woojun.shocki.view.auth.AuthActivity.Companion.KAKAO


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

        binding.phoneSignInButton.setOnClickListener { phoneSignIn() }
        binding.kakaoLoginButton.setOnClickListener { kakaoLogin() }
        binding.googleLoginButton.setOnClickListener { googleLogin() }
        binding.phoneLoginButton.apply {
            paintFlags = Paint.UNDERLINE_TEXT_FLAG
            setOnClickListener { phoneLogin() }
        }

    }

    private fun phoneSignIn() {
        (activity as AuthActivity).animationNavigate(R.id.signUp)
    }

    private fun phoneLogin() {
        (activity as AuthActivity).animationNavigate(R.id.logIn)
    }

    private fun kakaoLogin() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, _ ->
            if (token != null) {
                saveToken(requireActivity(), PostLoginRequest(token.accessToken, "", "", KAKAO))
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
            UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                if (error != null) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
                } else if (token != null) {
                    saveToken(requireActivity(), PostLoginRequest(token.accessToken, "", "", KAKAO))
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
        }
    }

    private fun googleLogin() {
        val credentialManager = CredentialManager.create(requireContext())

        val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(BuildConfig.CLIENT_ID)
            .setNonce(UUID.randomUUID().toString())
            .build()

        val request: GetCredentialRequest = GetCredentialRequest
            .Builder()
            .addCredentialOption(signInWithGoogleOption)
            .build()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = requireContext(),
                )
                val credential = result.credential
                when (result.credential) {
                    is CustomCredential -> {
                        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                            try {
                                val googleIdTokenCredential = GoogleIdTokenCredential
                                    .createFrom(credential.data)
                                saveToken(requireActivity(), PostLoginRequest(googleIdTokenCredential.idToken, "", "", GOOGLE))

                            } catch (e: GoogleIdTokenParsingException) {
                                Toast.makeText(context, "구글 로그인에 실패하였습니다", Toast.LENGTH_SHORT).show()
                            }
                        }
                        else {
                            Toast.makeText(context, "구글 로그인에 실패하였습니다", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else -> {
                        Toast.makeText(context, "구글 로그인에 실패하였습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "구글 로그인에 실패하였습니다", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}