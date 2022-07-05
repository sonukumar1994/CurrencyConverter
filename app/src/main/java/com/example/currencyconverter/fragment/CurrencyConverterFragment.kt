package com.example.currencyconverter.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.currencyconverter.R
import com.example.currencyconverter.databinding.FragmentCurrencyConverterBinding
import com.example.currencyconverter.viewmodel.MainViewModel
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
        getAllCountries().forEach() {
            println("Countries name $it")
        }

    }

    private fun setListener() {
        mViewBinding.btnDetails.setOnClickListener {
            findNavController().navigate(R.id.action_currencyConverterFragment_to_detailsFragment)
        }
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

    private fun setObserver() {
        activity?.let {
            viewModel.data.observe(it) {
                Log.d("CurrencyConverterFrag", it.message.toString())
            }
        }
    }

}