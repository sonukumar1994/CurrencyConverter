package com.example.currencyconverter.network

import com.example.currencyconverter.helper.Constant
import com.example.currencyconverter.model.CurrencyModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET(Constant.CONVERT_URL)
    suspend fun convertCurrency(
        @Query("access_key") access_key: String,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("amount") amount: Double
    ): Response<CurrencyModel>
}