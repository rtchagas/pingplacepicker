package com.rtchagas.pingsample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.places.api.model.Place
import com.rtchagas.pingplacepicker.PingPlacePicker
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    private val pingActivityRequestCode = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnOpenPlacePicker.setOnClickListener {
            showPlacePicker()
        }
    }

    private fun showPlacePicker() {

        val pingBuilder = PingPlacePicker.IntentBuilder()

        pingBuilder.setAndroidApiKey(getString(R.string.key_google_apis_android))
        pingBuilder.setGeolocationApiKey(getString(R.string.key_google_apis_geolocation))

        try {
            val placeIntent = pingBuilder.build(this)
            startActivityForResult(placeIntent, pingActivityRequestCode)
        }
        catch (ex: Exception) {
            toast("Google Play Services is not Available")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ((requestCode == pingActivityRequestCode) && (resultCode == Activity.RESULT_OK)) {

            val place: Place? = PingPlacePicker.getPlace(data!!)

            toast("You selected: ${place?.name}")
        }
    }
}
