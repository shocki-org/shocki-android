package com.woojun.shocki.view.wallet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.woojun.shocki.databinding.FragmentWalletFinishBinding
import com.woojun.shocki.model.MetamaskModel
import com.woojun.shocki.view.main.MainActivity
import io.metamask.androidsdk.EthereumRequest
import io.metamask.androidsdk.Result

class WalletFinishFragment : Fragment() {

    private var _binding: FragmentWalletFinishBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletFinishBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mainButton.setOnClickListener {
            MetamaskModel.connectToEthereum(requireContext()) { _, ethereum ->
                val rpcRequest = EthereumRequest(
                    method = "wallet_addEthereumChain",
                    params = listOf(mutableMapOf(
                        "chainId" to "0x79A",
                        "chainName" to "Minato",
                        "rpcUrls" to listOf("https://rpc.minato.soneium.org/"),
                        "nativeCurrency" to mapOf(
                            "name" to "Ether",
                            "symbol" to "ETH",
                            "decimals" to 18,
                        ),
                        "blockExplorerUrls" to listOf("https://explorer-testnet.soneium.org")
                    ))
                )

                ethereum?.sendRequest(rpcRequest) { result ->
                    when (result) {
                        is Result.Success.Item -> {
                            startActivity(Intent(requireActivity(), MainActivity::class.java))
                            requireActivity().finishAffinity()
                        }
                        else -> {
                            Log.d("METAMASK WALLET", result.toString())
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}