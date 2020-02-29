package com.rtchagas.pingplacepicker.ui

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import com.google.android.libraries.places.api.model.Place
import com.rtchagas.pingplacepicker.R


object UiUtils {

    /**
     * Gets the place drawable resource according to its type
     */
    fun getPlaceDrawableRes(context: Context, place: Place): Int {

        val defType = "drawable"
        val defPackage = context.packageName

        place.types?.let {
            for (type: Place.Type in it) {
                val name = type.name.toLowerCase()
                val id: Int = context.resources
                    .getIdentifier("ic_places_$name", defType, defPackage)
                if (id > 0) return id
            }
        }

        // Default resource
        return R.drawable.ic_map_marker_black_24dp
    }

    /**
     * Returns whether the current selected theme is night mode or not
     */
    fun isNightModeEnabled(): Boolean {

        val currentNightMode =
            (Resources.getSystem().configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK)

        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }
}