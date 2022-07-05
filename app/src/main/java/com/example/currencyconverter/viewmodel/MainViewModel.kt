package com.example.currencyconverter.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.helper.Resource
import com.example.currencyconverter.model.CurrencyConvertedResultModel
import com.example.currencyconverter.model.SeriesDataModel
import com.example.currencyconverter.repo.MainRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepo: MainRepo) : ViewModel() {

    val convertedData = MutableLiveData<Resource<CurrencyConvertedResultModel>>()

    val timeSeriesData = MutableLiveData<Resource<SeriesDataModel>>()
    val otherCountrySeriesData = MutableLiveData<Resource<SeriesDataModel>>()

    fun getConvertedCurrency(from: String, to: String, amount: Double) {
        viewModelScope.launch {
            mainRepo.getConvertedData(from, to, amount).collect {
                convertedData.value = it
            }
        }
    }

    fun getSeriesData(startDate: String, endDate: String, base: String, symbols: String) {
        viewModelScope.launch {
            mainRepo.getTimeSeriesData(startDate, endDate, base, symbols).collect {
                timeSeriesData.value = it
            }
        }
    }

    fun getOtherCountrySeriesData(startDate: String, endDate: String, base: String, symbols: String) {
        viewModelScope.launch {
            mainRepo.getTimeSeriesData(startDate, endDate, base, symbols).collect {
                otherCountrySeriesData.value = it
            }
        }
    }
}