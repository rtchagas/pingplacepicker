package com.rtchagas.pingsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.rtchagas.pingplacepicker.PingPlacePicker
import com.rtchagas.pingplacepicker.ui.toast
import kotlinx.android.synthetic.main.activity_main.btnOpenPlacePicker

class MainActivity : AppCompatActivity(), PingPlacePicker.OnPlaceSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnOpenPlacePicker.setOnClickListener {
            showPlacePicker()
        }
    }

    private fun showPlacePicker() {

        val builder = PingPlacePicker.Builder()

        builder.setAndroidApiKey(getString(R.string.key_google_apis_android))
            .setMapsApiKey(getString(R.string.key_google_apis_maps))
            .setOnPlaceSelectedListener(this)

        // If you want to set a initial location
        // rather then the current device location.
        // pingBuilder.setLatLng(LatLng(37.4219999, -122.0862462))

        try {
            val pingIntent = builder.build(this)
            startActivity(pingIntent)
        } catch (ex: Exception) {
            toast("Google Play Services is not Available")
        }
    }

    override fun onPlaceSelected(place: Place, latLng: LatLng) {
        toast("You selected: ${place.name}\n Map location: $latLng")
    }
}
