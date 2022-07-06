package com.example.currencyconverter.model

data class Rates(val key: String, val rate: Double?) {
    var countryName: String? = ""
}