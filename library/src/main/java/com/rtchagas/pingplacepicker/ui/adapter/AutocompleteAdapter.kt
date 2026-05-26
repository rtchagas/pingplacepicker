package com.rtchagas.pingplacepicker.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.rtchagas.pingplacepicker.databinding.ItemAutocompletePredictionBinding

internal class AutocompleteAdapter(
    private val onClick: (AutocompletePrediction) -> Unit,
) : ListAdapter<AutocompletePrediction, AutocompleteAdapter.PredictionViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PredictionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAutocompletePredictionBinding.inflate(inflater, parent, false)
        return PredictionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PredictionViewHolder, position: Int) {
        holder.bind(getItem(position), onClick)
    }

    class PredictionViewHolder(private val binding: ItemAutocompletePredictionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(prediction: AutocompletePrediction, onClick: (AutocompletePrediction) -> Unit) {
            with(binding) {
                tvPrimary.text = prediction.getPrimaryText(null)
                tvSecondary.text = prediction.getSecondaryText(null)
                root.setOnClickListener { onClick(prediction) }
            }
        }
    }

    private companion object {
        val DIFF = object : DiffUtil.ItemCallback<AutocompletePrediction>() {
            override fun areItemsTheSame(
                oldItem: AutocompletePrediction,
                newItem: AutocompletePrediction,
            ): Boolean = oldItem.placeId == newItem.placeId

            override fun areContentsTheSame(
                oldItem: AutocompletePrediction,
                newItem: AutocompletePrediction,
            ): Boolean = oldItem.placeId == newItem.placeId &&
                oldItem.getFullText(null) == newItem.getFullText(null)
        }
    }
}
