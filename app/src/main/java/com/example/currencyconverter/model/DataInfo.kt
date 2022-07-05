package com.example.currencyconverter.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataInfo(val fromCurrency: String?, val toCurrency:String?) : Parcelable