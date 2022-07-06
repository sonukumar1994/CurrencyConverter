package com.example.currencyconverter.network

import com.example.currencyconverter.helper.Constant
import com.example.currencyconverter.model.CurrencyConvertedResultModel
import com.example.currencyconverter.model.HistoryResponse
import com.example.currencyconverter.model.LatestResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET(Constant.CONVERT_URL)
    suspend fun convertCurrency(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("amount") amount: Double
    ): Response<CurrencyConvertedResultModel>

    @GET(Constant.TIME_SERIES_URL)
    suspend fun getHistoryRates(
        @Query("start_date") start_date: String,
        @Query("end_date") end_date: String,
        @Query("base") base: String,
        @Query("symbols") symbols: String
    ): Response<HistoryResponse>

    @GET(Constant.LATEST_URL)
   suspend fun getLatestRates(
        @Query("base") base: String
    ): Response<LatestResponse>
}