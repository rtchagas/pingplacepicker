package com.rtchagas.pingplacepicker

import android.app.Activity
import android.content.Intent
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.rtchagas.pingplacepicker.inject.PingKoinContext
import com.rtchagas.pingplacepicker.ui.PlacePickerActivity

object PingPlacePicker {

    internal var androidApiKey: String = ""

    internal var mapsApiKey: String = ""

    internal var urlSigningSecret = ""

    internal var isNearbySearchEnabled = false

    internal var onPlaceSelectedListener: OnPlaceSelectedListener? = null

    class Builder {

        private val intent = Intent()

        /**
         * This key will be used to all nearby requests to Google Places API.
         */
        fun setAndroidApiKey(androidKey: String): Builder {
            androidApiKey = androidKey
            return this
        }

        /**
         * This key will be used to nearby searches and reverse geocoding
         * requests to Google Maps HTTP API.
         */
        fun setMapsApiKey(geoKey: String): Builder {
            mapsApiKey = geoKey
            return this
        }

        /**
         * The initial location that the map must be pointing to.
         * If this is set, PING will search for places near this location.
         */
        fun setLatLng(location: LatLng): Builder {
            intent.putExtra(PlacePickerActivity.EXTRA_LOCATION, location)
            return this
        }

        /**
         * Sets the listener to be called when a place is selected.
         */
        fun setOnPlaceSelectedListener(listener: OnPlaceSelectedListener): Builder {
            onPlaceSelectedListener = listener
            return this
        }

        /**
         * Enables URL signing for Google APIs that require it.
         *
         * Currently only Maps Statics API requires signing for some users.
         *
         * More info [here](https://developers.google.com/maps/documentation/maps-static/get-api-key#generating-digital-signatures)
         */
        fun setUrlSigningSecret(secretKey: String): Builder {
            urlSigningSecret = secretKey
            return this
        }

        /**
         * Set whether the library should return the place coordinate retrieved from GooglePlace
         * or the actual selected location from google map
         */
        fun setShouldReturnActualLatLng(shouldReturnActualLatLng: Boolean): Builder {
            intent.putExtra(
                PlacePickerActivity.EXTRA_RETURN_ACTUAL_LATLNG,
                shouldReturnActualLatLng
            )
            return this
        }

        @Throws(GooglePlayServicesNotAvailableException::class)
        fun build(activity: Activity): Intent {

            PingKoinContext.init(activity.application)

            val result: Int =
                GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity)

            if (ConnectionResult.SUCCESS != result) {
                throw GooglePlayServicesNotAvailableException(result)
            }

            isNearbySearchEnabled = activity.resources.getBoolean(R.bool.enable_nearby_search)

            intent.setClass(activity, PlacePickerActivity::class.java)
            return intent
        }
    }

    /**
     * Listener to be called when PING returns a selected place.
     */
    interface OnPlaceSelectedListener {

        /**
         * Called when PING returns a place selected by the user.
         * @param place the selected place.
         * @param latLng the selected latitude/longitude in the map.
         */
        fun onPlaceSelected(place: Place, latLng: LatLng)

    }
}
