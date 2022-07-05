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
import com.example.currencyconverter.helper.Utility
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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
        mViewBinding.fromEdittext.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                checkAmount(s.toString())
            }

        })
    }

    private fun checkAmount(amount:String) {

        if(amount.isEmpty() || amount == "0"){
            Snackbar.make(mViewBinding.llMain,"Input a value in the first text field, result will be shown in the second text field", Snackbar.LENGTH_LONG)
                //.view.setBackgroundColor(ContextCompat.getColor(activity, R.color.dark_red))
                .show()
        }

        //check if internet is available
        else if (!Utility.isNetworkAvailable(activity)){
            Snackbar.make(mViewBinding.llMain,"You are not connected to the internet", Snackbar.LENGTH_LONG)
//                .withColor(ContextCompat.getColor(activity, R.color.dark_red))
//                .setTextColor(ContextCompat.getColor(activity, R.color.white))
                .show()
        }

        //carry on and convert the value
        else{
            convertAmount()
        }
    }

    private fun setListener() {
        mViewBinding.btnDetails.setOnClickListener {
            findNavController().navigate(R.id.action_currencyConverterFragment_to_detailsFragment)
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
        activity?.let {
            viewModel.data.observe(it) {
                Log.d("CurrencyConverterFrag", it.message.toString())
            }
        }
    }

    private fun convertAmount() {
        val apiKey = Constant.API_KEY
        val from = fromSelectedItem.toString()
        val to = toSelectedItem.toString()
        val amount = mViewBinding.fromEdittext.text.toString().toDouble()

        //do the conversion
        viewModel.getConvertedCurrency(apiKey, from, to, amount)
    }

}