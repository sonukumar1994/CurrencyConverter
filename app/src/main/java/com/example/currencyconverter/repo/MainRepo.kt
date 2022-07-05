package com.example.currencyconverter.repo

import com.example.currencyconverter.helper.Resource
import com.example.currencyconverter.model.CurrencyModel
import com.example.currencyconverter.network.ApiDataSource
import com.example.currencyconverter.network.BaseDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


import javax.inject.Inject

class MainRepo @Inject constructor(private val apiDataSource: ApiDataSource): BaseDataSource() {
    //Using coroutines flow to get the response from
    suspend fun getConvertedData(access_key: String, from: String, to: String, amount: Double): Flow<Resource<CurrencyModel>> {
        return flow {
            emit(safeApiCall { apiDataSource.getConvertedRate(access_key, from, to, amount) })
        }.flowOn(Dispatchers.IO)
    }
}