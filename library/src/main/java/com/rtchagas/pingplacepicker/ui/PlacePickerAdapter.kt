package com.rtchagas.pingplacepicker.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.Place
import com.rtchagas.pingplacepicker.R
import kotlinx.android.synthetic.main.item_place.view.*

class PlacePickerAdapter(private var placeList: List<Place>, private val clickListener: (Place) -> Unit)
    : RecyclerView.Adapter<PlacePickerAdapter.PlaceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {

        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_place, parent, false)

        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.bind(placeList[position], clickListener)
    }

    override fun getItemCount(): Int {
        return placeList.size
    }

    fun swapData(newPlaceList: List<Place>) {
        placeList = newPlaceList
        notifyDataSetChanged()
    }

    inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(place: Place, listener: (Place) -> Unit) {

            with(itemView) {
                setOnClickListener { listener(place) }
                ivPlaceType.setImageResource(UiUtils.getPlaceDrawableRes(itemView.context, place))
                tvPlaceName.text = place.name
                tvPlaceAddress.text = place.address
            }
        }
    }
}

