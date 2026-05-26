package com.rtchagas.pingplacepicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.IntentCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.rtchagas.pingplacepicker.inject.PingKoinContext
import com.rtchagas.pingplacepicker.ui.activity.PlacePickerActivity

object PingPlacePicker {

    internal var androidApiKey: String = ""
        private set

    internal var mapsApiKey: String = ""
        private set

    /**
     * Input for [Contract].
     *
     * @param androidApiKey Places SDK key (Android-restricted is fine).
     * @param mapsApiKey Maps Static API key used only to render the preview
     *   image in the confirmation dialog. Leave empty to disable that preview.
     * @param initialLocation If provided, the map opens here; otherwise the
     *   picker uses the device's last known location.
     */
    data class Request(
        val androidApiKey: String,
        val mapsApiKey: String = "",
        val initialLocation: LatLng? = null,
    )

    /**
     * Selection returned from the picker. [latLng] is the map's camera target
     * at the moment of confirmation, which may differ from [place]'s own LatLng.
     */
    data class Result(
        val place: Place,
        val latLng: LatLng,
    )

    /**
     * [ActivityResultContract] for launching the place picker.
     *
     * ```
     * private val picker = registerForActivityResult(PingPlacePicker.Contract()) { result ->
     *     result?.let { /* use it.place, it.latLng */ }
     * }
     * picker.launch(PingPlacePicker.Request(androidApiKey = "..."))
     * ```
     */
    class Contract : ActivityResultContract<Request, Result?>() {

        @Throws(GooglePlayServicesNotAvailableException::class)
        override fun createIntent(context: Context, input: Request): Intent {
            val availability = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(context)
            if (availability != ConnectionResult.SUCCESS) {
                throw GooglePlayServicesNotAvailableException(availability)
            }

            androidApiKey = input.androidApiKey
            mapsApiKey = input.mapsApiKey
            PingKoinContext.init(context.applicationContext)

            return Intent(context, PlacePickerActivity::class.java).apply {
                input.initialLocation?.let { putExtra(PlacePickerActivity.EXTRA_LOCATION, it) }
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Result? {
            if (resultCode != Activity.RESULT_OK || intent == null) return null
            val place = IntentCompat.getParcelableExtra(
                intent, EXTRA_PLACE, Place::class.java,
            ) ?: return null
            val latLng = IntentCompat.getParcelableExtra(
                intent, EXTRA_LAT_LNG, LatLng::class.java,
            ) ?: return null
            return Result(place, latLng)
        }

        internal companion object {
            const val EXTRA_PLACE = "ping_extra_place"
            const val EXTRA_LAT_LNG = "ping_extra_lat_lng"
        }
    }
}
