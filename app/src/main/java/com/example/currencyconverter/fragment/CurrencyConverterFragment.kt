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

    private var fromSelectedCountry: String? = "United States"
    private var toSelectedCountry: String? = "India"

    private var fromSelectedCurrency: String? = "USD"
    private var toSelectedCurrency: String? = "INR"

    //  private var isApiCalled: Boolean = false

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
        // setTextWatcher()
    }

    /*  private fun setTextWatcher() {
          mViewBinding.fromEdittext.addTextChangedListener(object : TextWatcher {
              override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

              }

              override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
              }

              override fun afterTextChanged(s: Editable?) {
                  checkAmount()
              }

          })
      }*/

    private fun checkValidationAmount() {
        val amount = mViewBinding.fromEdittext.text.toString()
        if (amount.isEmpty() || amount == "0") {

        }
        //check if internet is available
        else if (!Utility.isNetworkAvailable(activity)) {
            Snackbar.make(
                mViewBinding.llMain,
                getString(R.string.network_error),
                Snackbar.LENGTH_LONG
            ).show()
        } else {
            callConvertAmountApi()
        }
    }

    private fun setListener() {
        mViewBinding.btnHistory.setOnClickListener {
            if (!Utility.isNetworkAvailable(activity)) {
                Snackbar.make(
                    mViewBinding.llMain,
                    getString(R.string.network_error),
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                val dataInfo = DataInfo(fromSelectedCurrency, toSelectedCurrency)
                val action =
                    CurrencyConverterFragmentDirections.actionCurrencyConverterFragmentToDetailsFragment(
                        dataInfo
                    )
                findNavController().navigate(action)
            }
        }

        mViewBinding.btnConvert.setOnClickListener {
            checkValidationAmount()
        }

        mViewBinding.ivChange.setOnClickListener {
            exChangeFromAndTo()
        }

        mViewBinding.fromSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    println("From Selected Language ${allLanguages[position]}")
                    fromSelectedCountry = allLanguages[position]
                    val countryCode = getCountryCode(allLanguages[position])
                    val currencySymbol = getSymbol(countryCode)
                    fromSelectedCurrency = currencySymbol
//                    if (isApiCalled)
//                        checkAmount()
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
                    toSelectedCountry = allLanguages[position]
                    val countryCode = getCountryCode(allLanguages[position])
                    val currencySymbol = getSymbol(countryCode)
                    toSelectedCurrency = currencySymbol
//                    checkAmount()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
    }

    private fun exChangeFromAndTo() {
        var temp = fromSelectedCountry
        fromSelectedCountry = toSelectedCountry
        toSelectedCountry = temp
        mViewBinding.fromSpinner.setSelection(getCountryPosition(fromSelectedCountry))
        mViewBinding.toSpinner.setSelection(getCountryPosition(toSelectedCountry))
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

        mViewBinding.fromSpinner.setSelection(getCountryPosition(fromSelectedCountry))
        mViewBinding.toSpinner.setSelection(getCountryPosition(toSelectedCountry))

    }

    private fun getCountryPosition(countryName: String?): Int {
        countryName?.let {
            for (i in allLanguages.indices) {
                if (allLanguages[i].equals(it, true)) return i
            }
        }
        return 0
    }

    private fun getAllCountries(): ArrayList<String> {
        val locales = Locale.getAvailableLocales()
        val countries = ArrayList<String>()
        for (locale in locales) {
            val country = locale.displayCountry
            if (country.trim { it <= ' ' }.isNotEmpty() && !countries.contains(country)) {
                countries.add(country)
                getSymbol(getCountryCode(country))?.let {
                    Utility.countryWithCurrencyCodeMap.put(
                        it,
                        country
                    )
                }
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
                            val formattedString =
                                String.format(getString(R.string.generic_text), result.result)
                            mViewBinding.toTextview.text = formattedString
                        } else {
                            Snackbar.make(
                                mViewBinding.llMain,
                                getString(R.string.error_message),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                        mViewBinding.progressBar.visibility = View.GONE
                        mViewBinding.btnConvert.visibility = View.VISIBLE
                    }
                    Resource.Status.ERROR -> {
                        Snackbar.make(
                            mViewBinding.llMain,
                            getString(R.string.error_message),
                            Snackbar.LENGTH_LONG
                        ).show()
                        mViewBinding.progressBar.visibility = View.GONE
                        mViewBinding.btnConvert.visibility = View.VISIBLE
                    }
                    Resource.Status.LOADING -> {
                        mViewBinding.progressBar.visibility = View.VISIBLE
                        mViewBinding.btnConvert.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun callConvertAmountApi() {
        mViewBinding.progressBar.visibility = View.VISIBLE
        mViewBinding.btnConvert.visibility = View.GONE
        val from = fromSelectedCurrency.toString()
        val to = toSelectedCurrency.toString()
        val amount = mViewBinding.fromEdittext.text.toString().toDouble()

        //do the conversion
        viewModel.getConvertedCurrency(from, to, amount)
    }

}