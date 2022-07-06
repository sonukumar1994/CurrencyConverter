package com.example.currencyconverter.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyconverter.R
import com.example.currencyconverter.adapter.HistoryRecyclerAdapter
import com.example.currencyconverter.databinding.FragmentDetailsBinding
import com.example.currencyconverter.helper.Resource
import com.example.currencyconverter.helper.Utility
import com.example.currencyconverter.model.HistoryResponse
import com.example.currencyconverter.model.Rates
import com.example.currencyconverter.viewmodel.MainViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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
        ratesChartSetup()
        getHistoryRates()
    }


    private fun getDate() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val calendar = Calendar.getInstance()
        endDate = dateFormat.format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, -3)
        startDate = dateFormat.format(calendar.time)
    }

    private fun getHistoryRates() {
        // Rates against selected from and to currency
        viewModel.getHistoryRates(startDate, endDate, fromCurrency!!, toCurrency!!)

        // Other Currency
        viewModel.getLatestRates(fromCurrency!!)
    }

    private fun setObserver() {
        activity?.let { activit ->
            viewModel.historyRates.observe(activit) {
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        val result = it.data
                        if (result?.success == true) {
                            setHistoryRecyclerAdapter(parseDataToList(result.rates))
                            showHistoryRates(result)
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
            viewModel.otherCountryCurrencyRates.observe(activit) {
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        val result = it.data
                        if (result?.success == true) {
                            setOtherCountryHistoryRecyclerAdapter(result.rates)
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
        val historyAdapter = HistoryRecyclerAdapter(list,false)
        mViewBinding.historyRecycler.layoutManager = LinearLayoutManager(activity)
        mViewBinding.historyRecycler.adapter = historyAdapter
    }

    private fun setOtherCountryHistoryRecyclerAdapter(ratesMap: Map<String, Double>) {
        val ratesList = ArrayList<Rates>()
        ratesMap.keys.forEach { key ->
           val rate= Rates(key, ratesMap[key])
            rate.countryName=Utility.countryWithCurrencyCodeMap[key]
            ratesList.add(rate)
        }
        val historyAdapter = HistoryRecyclerAdapter(ratesList,true)
        mViewBinding.otherCurrencyHistoryList.layoutManager = LinearLayoutManager(activity)
        mViewBinding.otherCurrencyHistoryList.adapter = historyAdapter
    }

    private fun showHistoryRates(response: HistoryResponse) {
        val historyRatesResponse = response.rates.toSortedMap()
        val historyRates = historyRatesResponse.values

        val values = arrayListOf<Entry>()
        repeat(historyRates.size) { i ->
            values.add(Entry(i.toFloat(), historyRates.elementAt(i)[toCurrency]!!.toFloat()))
        }

        val historyRatesDates = arrayListOf<String>()
        repeat(historyRatesResponse.keys.size) { i ->
            historyRatesDates.add(historyRatesResponse.keys.elementAt(i))
        }

        val lineDataSet = LineDataSet(values, "$fromCurrency Rates with base $toCurrency")
        lineDataSet.fillAlpha = 110
        lineDataSet.color = Color.RED
        lineDataSet.valueTextColor = ContextCompat.getColor(activity!!, R.color.purple_200)
        lineDataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        lineDataSet.setDrawFilled(true)
        lineDataSet.fillColor = Color.GRAY

        val dataSet = arrayListOf<ILineDataSet>()
        dataSet.add(lineDataSet)

        val xAxis = mViewBinding.ratesChart.xAxis
        xAxis.valueFormatter = XAxisValueFormatter(historyRatesDates)

        val lineData = LineData(dataSet)
        lineData.setDrawValues(true)

        mViewBinding.ratesChart.data = lineData
        mViewBinding.ratesChart.axisRight.isEnabled = false
        mViewBinding.ratesChart.invalidate()
    }

    private fun ratesChartSetup() {
        mViewBinding.ratesChart.isDragEnabled = true
        mViewBinding.ratesChart.setScaleEnabled(true)
        mViewBinding.ratesChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        mViewBinding.ratesChart.axisLeft.textColor = Color.BLACK
        mViewBinding.ratesChart.axisRight.textColor = Color.BLACK
        mViewBinding.ratesChart.xAxis.textColor = Color.BLACK
        mViewBinding.ratesChart.xAxis.labelRotationAngle = -45f
        mViewBinding.ratesChart.xAxis.labelCount = 3
        mViewBinding.ratesChart.legend.textColor =
            ContextCompat.getColor(activity!!, R.color.purple_700)
    }

    private class XAxisValueFormatter(private val values: List<String>) : ValueFormatter() {

        override fun getFormattedValue(value: Float): String {
            return values.elementAt(value.toInt())
        }
    }
}