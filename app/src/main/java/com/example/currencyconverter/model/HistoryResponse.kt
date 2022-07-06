package com.example.currencyconverter.model

import com.google.gson.annotations.SerializedName

data class HistoryResponse(
    val success: Boolean,
    @SerializedName("base")
    val base: String,
    @SerializedName("rates")
    val rates: HashMap<String, HashMap<String, Double>>
)