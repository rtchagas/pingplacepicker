package com.rtchagas.pingplacepicker

import android.app.Activity
import android.app.Application
import android.content.Intent
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.rtchagas.pingplacepicker.inject.PingKoinContext
import com.rtchagas.pingplacepicker.inject.repositoryModule
import com.rtchagas.pingplacepicker.inject.viewModelModule
import com.rtchagas.pingplacepicker.ui.PlacePickerActivity
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.dsl.koinApplication

class PingPlacePicker private constructor() {

    class IntentBuilder {

        private val intent = Intent()

        /**
         * This key will be used to all nearby requests to Google Places API.
         */
        fun setAndroidApiKey(androidKey: String): IntentBuilder {
            androidApiKey = androidKey
            return this
        }

        /**
         * This key will be used to nearby searches and reverse geocoding
         * requests to Google Maps HTTP API.
         */
        fun setMapsApiKey(geoKey: String): IntentBuilder {
            mapsApiKey = geoKey
            return this
        }

        /**
         * The initial location that the map must be pointing to.
         * If this is set, PING will search for places near this location.
         */
        fun setLatLng(location: LatLng): IntentBuilder {
            intent.putExtra(PlacePickerActivity.EXTRA_LOCATION, location)
            return this
        }

        /**
         * Enables URL signing for Google APIs that require it.
         *
         * Currently only Maps Statics API requires signing for some users.
         *
         * More info [here](https://developers.google.com/maps/documentation/maps-static/get-api-key#generating-digital-signatures)
         */
        fun setUrlSigningSecret(secretKey: String): IntentBuilder {
            urlSigningSecret = secretKey
            return this
        }

        @Throws(GooglePlayServicesNotAvailableException::class)
        fun build(activity: Activity): Intent {

            initKoin(activity.application)

            val result: Int =
                    GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity)

            if (ConnectionResult.SUCCESS != result) {
                throw GooglePlayServicesNotAvailableException(result)
            }

            isNearbySearchEnabled = activity.resources.getBoolean(R.bool.enable_nearby_search)

            intent.setClass(activity, PlacePickerActivity::class.java)
            return intent
        }

        /**
         * Initializes the Dependency Injection framework by passing
         * the current application context.
         */
        private fun initKoin(application: Application) {
            PingKoinContext.koinApp = koinApplication {
                androidLogger()
                androidContext(application)
                modules(listOf(
                        repositoryModule,
                        viewModelModule)
                )
            }
        }
    }

    companion object {

        const val EXTRA_PLACE = "extra_place"

        var androidApiKey: String = ""
        var mapsApiKey: String = ""

        var urlSigningSecret = ""

        var isNearbySearchEnabled = false

        @JvmStatic
        fun getPlace(intent: Intent): Place? {
            return intent.getParcelableExtra(EXTRA_PLACE)
        }
    }
}