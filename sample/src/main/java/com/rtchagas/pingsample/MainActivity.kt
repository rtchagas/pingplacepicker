package com.rtchagas.pingsample

import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.rtchagas.pingplacepicker.PingPlacePicker
import com.rtchagas.pingplacepicker.ui.activity.BaseActivity
import com.rtchagas.pingplacepicker.ui.toast
import com.rtchagas.pingsample.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private val placePickerLauncher = registerForActivityResult(PingPlacePicker.Contract()) { result ->
        result ?: return@registerForActivityResult
        toast("You selected: ${result.place.name}\n Map location: ${result.latLng}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyEdgeToEdgeInsets()
        binding.btnOpenPlacePicker.setOnClickListener { showPlacePicker() }
    }

    private fun applyEdgeToEdgeInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
            )
            view.updatePadding(bars.left, bars.top, bars.right, bars.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun showPlacePicker() {
        try {
            placePickerLauncher.launch(
                PingPlacePicker.Request(
                    androidApiKey = getString(R.string.key_google_apis_android),
                    mapsApiKey = getString(R.string.key_google_apis_maps),
                ),
            )
        } catch (ex: Exception) {
            toast("Google Play Services is not Available")
        }
    }
}
