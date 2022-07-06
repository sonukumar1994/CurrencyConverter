package com.example.currencyconverter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconverter.databinding.RowHistoryBinding
import com.example.currencyconverter.model.Rates

class HistoryRecyclerAdapter(private val list: List<Rates>, private val isOtherCountryCaller: Boolean) :
    RecyclerView.Adapter<HistoryRecyclerAdapter.HistoryViewModel>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewModel {
        val binding = RowHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewModel(binding,isOtherCountryCaller)
    }

    override fun onBindViewHolder(holder: HistoryViewModel, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class HistoryViewModel(val binding: RowHistoryBinding, private val isOtherCountryCaller: Boolean) : RecyclerView.ViewHolder(binding.root) {
        fun bind(rates: Rates) {
            binding.data = rates
            binding.isOtherCountryCaller=isOtherCountryCaller
        }
    }
}