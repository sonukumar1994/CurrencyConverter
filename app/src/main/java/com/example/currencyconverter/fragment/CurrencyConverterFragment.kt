package com.example.currencyconverter.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.currencyconverter.R
import com.example.currencyconverter.databinding.FragmentCurrencyConverterBinding


/**
 * Created by Sonu Kumar on 4/7/22
 * A simple [Fragment] subclass.
 * Use the [CurrencyConverterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CurrencyConverterFragment : Fragment() {
    private lateinit var mViewBinding: FragmentCurrencyConverterBinding
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
    }

    private fun setListener() {
        mViewBinding.btnDetails.setOnClickListener {
            findNavController().navigate(R.id.action_currencyConverterFragment_to_detailsFragment)
        }
    }
}