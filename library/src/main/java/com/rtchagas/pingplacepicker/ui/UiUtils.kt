package com.rtchagas.pingplacepicker.ui

import android.content.Context
import android.content.res.Configuration
import android.util.TypedValue
import androidx.annotation.ColorInt
import com.google.android.libraries.places.api.model.Place
import com.rtchagas.pingplacepicker.R
import org.jetbrains.anko.configuration
import java.util.*


object UiUtils {

    /**
     * Gets the place drawable resource according to its type
     */
    fun getPlaceDrawableRes(context: Context, place: Place): Int {

        val defType = "drawable"
        val defPackage = context.packageName

        place.types?.let {
            for (type: Place.Type in it) {
                val name = type.name.toLowerCase(Locale.ENGLISH)
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
    fun isNightModeEnabled(context: Context): Boolean {

        val nightMode = (context.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK)

        return nightMode == Configuration.UI_MODE_NIGHT_YES
    }

    @ColorInt
    fun getColorAttr(context: Context, colorAttr: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(colorAttr, typedValue, true)
        return typedValue.data
    }
}