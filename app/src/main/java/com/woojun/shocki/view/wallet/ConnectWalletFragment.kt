package com.woojun.shocki.view.wallet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.woojun.shocki.R
import com.woojun.shocki.database.SharedPreference.walletAddress
import com.woojun.shocki.database.TokenManager
import com.woojun.shocki.databinding.FragmentConnectWalletBinding
import com.woojun.shocki.dto.WalletRequest
import com.woojun.shocki.network.RetrofitAPI
import com.woojun.shocki.network.RetrofitClient
import com.woojun.shocki.model.MetamaskModel
import io.metamask.androidsdk.Ethereum
import io.metamask.androidsdk.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ConnectWalletFragment : Fragment() {

    private var _binding: FragmentConnectWalletBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConnectWalletBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mainButton.setOnClickListener {
            lifecycleScope.launch {
                MetamaskModel.connectToEthereum(requireContext()) { result, _ ->
                    when (result) {
                        is Result.Success.Items -> {
                            lifecycleScope.launch {
                                val isSuccess = setWallet(result.value[0])
                                if (isSuccess) {
                                    walletAddress = result.value[0]
                                    (activity as WalletActivity).animationNavigate(R.id.walletFinish)
                                } else {
                                    Toast.makeText(requireContext(), "메타마스크 지갑 연결을 실패했어요", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        else -> {
                            Toast.makeText(requireContext(), "메타마스크 지갑 연결을 실패했어요", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private suspend fun setWallet(address: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.setWallet("bearer ${TokenManager.accessToken}", WalletRequest(address))
                response.isSuccessful
            }
        } catch (e: Exception) {
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}