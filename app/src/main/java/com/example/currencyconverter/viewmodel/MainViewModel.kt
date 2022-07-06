package com.example.currencyconverter.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.helper.Resource
import com.example.currencyconverter.model.CurrencyConvertedResultModel
import com.example.currencyconverter.model.HistoryResponse
import com.example.currencyconverter.model.LatestResponse
import com.example.currencyconverter.repo.MainRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepo: MainRepo) : ViewModel() {

    val convertedData = MutableLiveData<Resource<CurrencyConvertedResultModel>>()

    val historyRates = MutableLiveData<Resource<HistoryResponse>>()
    val otherCountryCurrencyRates = MutableLiveData<Resource<LatestResponse>>()

    fun getConvertedCurrency(from: String, to: String, amount: Double) {
        viewModelScope.launch {
            mainRepo.getConvertedData(from, to, amount).collect {
                convertedData.value = it
            }
        }
    }

    fun getHistoryRates(startDate: String, endDate: String, base: String, symbols: String) {
        viewModelScope.launch {
            mainRepo.getHistoryRates(startDate, endDate, base, symbols).collect {
                historyRates.value = it
            }
        }
    }

    fun getLatestRates(base: String) {
        viewModelScope.launch {
            mainRepo.getLatestRates(base).collect {
                otherCountryCurrencyRates.value = it
            }
        }
    }
}