package com.rtchagas.pingplacepicker

import android.app.Activity
import android.app.Application
import android.content.Intent
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
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
         * This key will be used to all reverse geocoding request to Google Maps API.
         */
        @Deprecated("This function will be removed in a future release.",
                ReplaceWith("setGeocodingApiKey(geoKey)"))
        fun setGeolocationApiKey(geoKey: String): IntentBuilder {
            return setGeocodingApiKey(geoKey)
        }

        /**
         * This key will be used to all reverse geocoding request to Google Maps API.
         */
        fun setGeocodingApiKey(geoKey: String): IntentBuilder {
            geoLocationApiKey = geoKey
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
                modules(
                        repositoryModule,
                        viewModelModule
                )
            }
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