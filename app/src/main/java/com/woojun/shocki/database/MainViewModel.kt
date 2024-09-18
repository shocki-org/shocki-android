package com.woojun.shocki.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woojun.shocki.data.Category
import com.woojun.shocki.dto.CategoryResponse
import com.woojun.shocki.dto.SimpleProductResponse
import com.woojun.shocki.network.RetrofitAPI
import com.woojun.shocki.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    companion object {
        const val FUNDING = "FUNDING"
        const val SELLING = "SELLING"
    }

    private val _sellingList = MutableLiveData<List<SimpleProductResponse>?>()
    val sellingList: LiveData<List<SimpleProductResponse>?> get() = _sellingList

    private val _fundingList = MutableLiveData<List<SimpleProductResponse>?>()
    val fundingList: LiveData<List<SimpleProductResponse>?> get() = _fundingList

    private val _categoryList = MutableLiveData<List<Category>?>()
    val categoryList: LiveData<List<Category>?> get() = _categoryList

    fun fetchDates() {
        viewModelScope.launch {
            val sellingDeferred = async { getProductList(SELLING) }
            val fundingDeferred = async { getProductList(FUNDING) }
            val categoryDeferred = async { getCategoryList() }

            _sellingList.value = sellingDeferred.await()
            _fundingList.value = fundingDeferred.await()
            _categoryList.value = categoryDeferred.await()?.map {
                Category(it.name, it.id)
            }
        }
    }

    private suspend fun getProductList(type: String): List<SimpleProductResponse>? {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.getProductList("bearer ${TokenManager.accessToken}", type)
                if (response.isSuccessful) {
                    response.body()
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getCategoryList(): List<CategoryResponse>? {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.getCategoryList("bearer ${TokenManager.accessToken}")
                if (response.isSuccessful) {
                    response.body()
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

}
