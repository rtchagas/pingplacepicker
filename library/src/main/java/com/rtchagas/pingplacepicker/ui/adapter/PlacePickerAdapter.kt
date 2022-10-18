package com.rtchagas.pingplacepicker.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.Place
import com.rtchagas.pingplacepicker.databinding.ItemPlaceBinding
import com.rtchagas.pingplacepicker.ui.UiUtils

internal class PlacePickerAdapter(
    private var placeList: List<Place>,
    private val clickListener: (Place) -> Unit
) : RecyclerView.Adapter<PlacePickerAdapter.PlaceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPlaceBinding.inflate(inflater, parent, false)
        return PlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.bind(placeList[position], clickListener)
    }

    override fun getItemCount(): Int =
        placeList.size

    fun swapData(newPlaceList: List<Place>) {
        placeList = newPlaceList
        notifyDataSetChanged()
    }

    inner class PlaceViewHolder(private val binding: ItemPlaceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(place: Place, listener: (Place) -> Unit) {

            with(binding) {
                root.setOnClickListener { listener(place) }
                ivPlaceType.setImageResource(UiUtils.getPlaceDrawableRes(itemView.context, place))
                tvPlaceName.text = place.name
                tvPlaceAddress.text = place.address
            }
        }
    }
}
