package com.example.currencyconverter.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.currencyconverter.R
import com.example.currencyconverter.databinding.FragmentCurrencyConverterBinding
import com.example.currencyconverter.helper.Constant
import com.example.currencyconverter.helper.Resource
import com.example.currencyconverter.helper.Utility
import com.example.currencyconverter.model.DataInfo
import com.example.currencyconverter.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Sonu Kumar on 4/7/22
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class CurrencyConverterFragment : Fragment() {
    private lateinit var mViewBinding: FragmentCurrencyConverterBinding
    private val viewModel: MainViewModel by viewModels()
    val allLanguages = getAllCountries()

    private var fromSelectedItem: String? = "AFN"
    private var toSelectedItem: String? = "AFN"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mViewBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_currency_converter,
            container,
            false
        )
        return mViewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setListener()
        setObserver()
        setSpinnerItems()
        setTextWatcher()
    }

    private fun setTextWatcher() {
        mViewBinding.fromEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                checkAmount()
            }

        })
    }

    private fun checkAmount() {
        val amount = mViewBinding.fromEdittext.text.toString()
        if (amount.isEmpty() || amount == "0") {

        }
        //check if internet is available
        else if (!Utility.isNetworkAvailable(activity)) {
            Snackbar.make(
                mViewBinding.llMain,
                "You are not connected to the internet",
                Snackbar.LENGTH_LONG
            )
//                .withColor(ContextCompat.getColor(activity, R.color.dark_red))
//                .setTextColor(ContextCompat.getColor(activity, R.color.white))
                .show()
        }

        //carry on and convert the value
        else {
            convertAmount()
        }
    }

    private fun setListener() {
        mViewBinding.btnDetails.setOnClickListener {
            val dataInfo = DataInfo(fromSelectedItem, toSelectedItem)
            val action =
                CurrencyConverterFragmentDirections.actionCurrencyConverterFragmentToDetailsFragment(dataInfo)
            findNavController().navigate(action)
        }

        mViewBinding.fromSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    println("From Selected Language ${allLanguages.get(position)}")
                    val countryCode = getCountryCode(allLanguages.get(position))
                    val currencySymbol = getSymbol(countryCode)
                    fromSelectedItem = currencySymbol
                    checkAmount()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        mViewBinding.toSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    println("To Selected Language ${allLanguages.get(position)}")
                    val countryCode = getCountryCode(allLanguages.get(position))
                    val currencySymbol = getSymbol(countryCode)
                    toSelectedItem = currencySymbol
                    checkAmount()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }

    }

    private fun setSpinnerItems() {
        val adapter =
            activity?.let {
                ArrayAdapter(
                    it,
                    android.R.layout.simple_spinner_dropdown_item,
                    allLanguages
                )
            }
        mViewBinding.fromSpinner.adapter = adapter
        mViewBinding.toSpinner.adapter = adapter
    }

    private fun getAllCountries(): ArrayList<String> {

        val locales = Locale.getAvailableLocales()
        val countries = ArrayList<String>()
        for (locale in locales) {
            val country = locale.displayCountry
            if (country.trim { it <= ' ' }.isNotEmpty() && !countries.contains(country)) {
                countries.add(country)
            }
        }
        countries.sort()

        return countries
    }

    private fun getCountryCode(countryName: String) =
        Locale.getISOCountries().find { Locale("", it).displayCountry == countryName }

    private fun getSymbol(countryCode: String?): String? {
        val availableLocales = Locale.getAvailableLocales()
        for (i in availableLocales.indices) {
            if (availableLocales[i].country == countryCode
            ) return Currency.getInstance(availableLocales[i]).currencyCode
        }
        return ""
    }

    private fun setObserver() {
        activity?.let { activity1 ->
            viewModel.convertedData.observe(activity1) {
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        val result = it.data
                        if (result?.success == true) {
                            val formattedString = String.format("%,.2f", result.result)
                            mViewBinding.toTextview.text = formattedString
                        }
                    }
                    Resource.Status.ERROR -> {
                        Snackbar.make(
                            mViewBinding.llMain,
                            "Ooops! something went wrong, Try again",
                            Snackbar.LENGTH_LONG
                        )
                            // .withColor(ContextCompat.getColor(this, R.color.dark_red))
                            // .setTextColor(ContextCompat.getColor(this, R.color.white))
                            .show()
                    }
                    Resource.Status.LOADING -> {

                    }
                }
            }
        }
    }

    private fun convertAmount() {
        val from = fromSelectedItem.toString()
        val to = toSelectedItem.toString()
        val amount = mViewBinding.fromEdittext.text.toString().toDouble()

        //do the conversion
        viewModel.getConvertedCurrency( from, to, amount)
    }

}