package com.example.currencyconverter.repo

import com.example.currencyconverter.helper.Resource
import com.example.currencyconverter.model.CurrencyConvertedResultModel
import com.example.currencyconverter.model.HistoryResponse
import com.example.currencyconverter.model.LatestResponse
import com.example.currencyconverter.network.ApiDataSource
import com.example.currencyconverter.network.BaseDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


import javax.inject.Inject

class MainRepo @Inject constructor(private val apiDataSource: ApiDataSource) : BaseDataSource() {
    //Using coroutines flow to get the response from
    suspend fun getConvertedData(
        from: String,
        to: String,
        amount: Double
    ): Flow<Resource<CurrencyConvertedResultModel>> {
        return flow {
            emit(safeApiCall { apiDataSource.getConvertedRate( from, to, amount) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getHistoryRates(
        startDate: String,
        endDate: String,
        base: String,
        symbols: String
    ): Flow<Resource<HistoryResponse>> {
        return flow {
            emit(safeApiCall { apiDataSource.getHistoryRates(startDate, endDate, base, symbols) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getLatestRates(
        base: String
    ): Flow<Resource<LatestResponse>> {
        return flow {
            emit(safeApiCall { apiDataSource.getLatestRates(base) })
        }.flowOn(Dispatchers.IO)
    }
}