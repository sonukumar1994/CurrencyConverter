package com.example.currencyconverter.model

data class SeriesDataModel(
    val success: Boolean,
    val base: String,
    val date: String,
    val rates: HashMap<String, HashMap<String, Double>>
)