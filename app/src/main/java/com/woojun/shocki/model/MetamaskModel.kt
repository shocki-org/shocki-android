package com.woojun.shocki.model

import android.content.Context
import com.woojun.shocki.BuildConfig
import io.metamask.androidsdk.DappMetadata
import io.metamask.androidsdk.Ethereum
import io.metamask.androidsdk.Result
import io.metamask.androidsdk.SDKOptions

object MetamaskModel {
    private val dappMetadata = DappMetadata("Shocki", "https://www.shocki.org")

    private val infuraAPIKey = BuildConfig.INFURAAPIKEY

    private val readonlyRPCMap = mapOf("0x79a" to BuildConfig.READONLYRPCMAP)

    fun connectToEthereum(context: Context, callback: (Result?, Ethereum?) -> Unit) {
        val ethereum = Ethereum(context, dappMetadata, SDKOptions(infuraAPIKey, readonlyRPCMap))
        ethereum.connect { result ->
            callback(result, ethereum)
        }
    }
}
