package com.woojun.shocki.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tosspayments.paymentsdk.model.TossPaymentResult

class PaymentViewModel : ViewModel() {
    private val _paymentResult = MutableLiveData<TossPaymentResult?>()
    val paymentResult: LiveData<TossPaymentResult?> get() = _paymentResult

    fun setPaymentResult(result: TossPaymentResult) {
        _paymentResult.value = result
    }

    fun resetPaymentResult() {
        _paymentResult.value = null
    }

}