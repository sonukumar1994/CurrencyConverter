package com.example.currencyconverter.network

import javax.inject.Inject

class ApiDataSource @Inject constructor(private val apiService: ApiService){
    suspend fun getConvertedRate( from: String, to: String, amount: Double)=
        apiService.convertCurrency(from,to,amount)


    suspend fun getHistoryRates( startDate: String, endDate: String, base: String,symbols:String)=
        apiService.getHistoryRates(startDate,endDate,base,symbols)

    suspend fun getLatestRates( base: String)=
        apiService.getLatestRates(base)

}