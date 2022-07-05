package com.example.currencyconverter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyconverter.R
import com.example.currencyconverter.adapter.HistoryRecyclerAdapter
import com.example.currencyconverter.databinding.FragmentDetailsBinding
import com.example.currencyconverter.helper.Resource
import com.example.currencyconverter.model.Rates
import com.example.currencyconverter.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DetailsFragment : Fragment() {
    private lateinit var mViewBinding: FragmentDetailsBinding
    val args: DetailsFragmentArgs by navArgs()
    private val viewModel: MainViewModel by viewModels()

    private var fromCurrency: String? = null
    private var toCurrency: String? = null
    private lateinit var startDate: String
    private lateinit var endDate: String
    val inDateFormat = SimpleDateFormat("yyyy-MM-dd")
    val dateFormat = SimpleDateFormat("dd MMM yy")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mViewBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_details,
            container,
            false
        )
        return mViewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fromCurrency = args.dataInfo.fromCurrency
        toCurrency = args.dataInfo.toCurrency
        getDate()
        setObserver()
        getTimeSeriesData()
    }


    private fun getDate() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val calendar = Calendar.getInstance()
        endDate = dateFormat.format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, -3)
        startDate = dateFormat.format(calendar.time)
    }

    private fun getTimeSeriesData() {
        // Rates against selected from and to currency
        viewModel.getSeriesData(startDate, endDate, fromCurrency!!, toCurrency!!)

        // Other Currency
        val otherCurrencyCodes = "EUR,JPY,GBP,AUD,CAD,CHF,HKD,NZD,INR,USD,KRW"
        viewModel.getOtherCountrySeriesData(startDate, endDate, fromCurrency!!, otherCurrencyCodes)
    }

    private fun setObserver() {
        activity?.let { activit ->
            viewModel.timeSeriesData.observe(activit) {
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        val result = it.data
                        if (result?.success == true) {
                            setHistoryRecyclerAdapter(parseDataToList(result.rates))
                        }
                    }
                    Resource.Status.ERROR -> {

                    }
                    Resource.Status.LOADING -> {

                    }
                }
            }
        }

        activity?.let { activit ->
            viewModel.otherCountrySeriesData.observe(activit) {
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        val result = it.data
                        if (result?.success == true) {
                            setOtherCountryHistoryRecyclerAdapter(parseDataToList(result.rates))
                        }
                    }
                    Resource.Status.ERROR -> {

                    }
                    Resource.Status.LOADING -> {

                    }
                }
            }
        }
    }

    fun parseDataToList(map: HashMap<String, HashMap<String, Double>>): List<Rates> {
        val list = ArrayList<Rates>()
        map.keys.forEach { key ->
            val result1 = map[key]
            result1?.keys?.forEach { key2 ->
                val rate = result1[key2]
                val newDate: Date = inDateFormat.parse(key)
                val newFormatDate = dateFormat.format(newDate)
                list.add(Rates(newFormatDate, rate))
            }
        }
        return list
    }


    private fun setHistoryRecyclerAdapter(list: List<Rates>) {
        val historyAdapter = HistoryRecyclerAdapter(list)
        mViewBinding.historyRecycler.layoutManager = LinearLayoutManager(activity)
        mViewBinding.historyRecycler.adapter = historyAdapter
    }

    private fun setOtherCountryHistoryRecyclerAdapter(list: List<Rates>) {
        val historyAdapter = HistoryRecyclerAdapter(list)
        mViewBinding.otherCurrencyHistoryList.layoutManager = LinearLayoutManager(activity)
        mViewBinding.otherCurrencyHistoryList.adapter = historyAdapter
    }
}