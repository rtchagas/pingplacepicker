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
         * This key will be used to nearby searches, static maps and
         * reverse geocoding requests to Google Maps APIs.
         *
         * Refer to the [documentation](https://raw.githubusercontent.com/rtchagas/pingplacepicker/master/images/maps_api_key.png)
         * to check how your key must be configured.
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
         * Sets a signature to be used in API calls.
         *
         * Key must be one listed in PingPlacePicker.KEY_SIGNATURE_*
         */
        fun setSignature(key: String, value: String): IntentBuilder {
            signatureMap[key] = value
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

        internal const val EXTRA_PLACE = "extra_place"

        internal var androidApiKey: String = ""
        internal var mapsApiKey: String = ""

        internal var isNearbySearchEnabled = false

        internal val signatureMap = mutableMapOf<String, String>()

        const val KEY_SIGNATURE_MAPS_STATIC_API = "key_maps_static_api"
        const val KEY_SIGNATURE_GEOCODING_API = "key_geocoding_api"

        @JvmStatic
        fun getPlace(intent: Intent): Place? {
            return intent.getParcelableExtra(EXTRA_PLACE)
        }
    }
}