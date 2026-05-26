package com.rtchagas.pingplacepicker.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.util.TypedValue
import androidx.annotation.ColorInt
import com.google.android.libraries.places.api.model.Place
import com.rtchagas.pingplacepicker.R

internal object UiUtils {

    /**
     * Gets the place drawable resource according to its type. Place types in
     * Places SDK 5.x are strings (e.g. "restaurant", "cafe"), so we look up
     * `ic_places_<type>` drawables directly.
     */
    @SuppressLint("DiscouragedApi")
    fun getPlaceDrawableRes(context: Context, place: Place): Int {
        val defType = "drawable"
        val defPackage = context.packageName

        for (type in place.placeTypes.orEmpty()) {
            val id = context.resources.getIdentifier("ic_places_$type", defType, defPackage)
            if (id > 0) return id
        }

        return R.drawable.ic_map_marker_black_24dp
    }

    fun isNightModeEnabled(context: Context): Boolean {
        val nightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightMode == Configuration.UI_MODE_NIGHT_YES
    }

    @ColorInt
    fun getColorAttr(context: Context, colorAttr: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(colorAttr, typedValue, true)
        return typedValue.data
    }
}
