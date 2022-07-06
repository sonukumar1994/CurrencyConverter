package com.example.currencyconverter.model

import com.google.gson.annotations.SerializedName

data class LatestResponse(
    val success: Boolean,
    @SerializedName("base")
    val base: String,

    @SerializedName("rates")
    val rates: Map<String, Double>
)

