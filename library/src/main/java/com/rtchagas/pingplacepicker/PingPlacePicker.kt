package com.rtchagas.pingplacepicker

import android.app.Activity
import android.content.Intent
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.libraries.places.api.model.Place
import com.rtchagas.pingplacepicker.ui.PlacePickerActivity

class PingPlacePicker private constructor() {

    class IntentBuilder {

        private val intent = Intent()

        /**
         * This key will be used to all nearby requests to Google Places API.
         */
        fun setAndroidApiKey(androidKey: String): IntentBuilder {
            PingPlacePicker.androidApiKey = androidKey
            return this
        }

        /**
         * This key will be used to all reverse geolocation request to Google Maps API.
         */
        fun setGeolocationApiKey(geoKey: String): IntentBuilder {
            PingPlacePicker.geoLocationApiKey = geoKey
            return this
        }

        @Throws(GooglePlayServicesNotAvailableException::class)
        fun build(activity: Activity): Intent {

            val result: Int =
                    GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity)

            if (ConnectionResult.SUCCESS != result) {
                throw GooglePlayServicesNotAvailableException(result)
            }

            intent.setClass(activity, PlacePickerActivity::class.java)
            return intent
        }
    }

    companion object {

        const val EXTRA_PLACE = "extra_place"

        var androidApiKey: String = ""
        var geoLocationApiKey: String = ""

        fun getPlace(intent: Intent): Place? {
            return intent.getParcelableExtra(EXTRA_PLACE)
        }
    }
}