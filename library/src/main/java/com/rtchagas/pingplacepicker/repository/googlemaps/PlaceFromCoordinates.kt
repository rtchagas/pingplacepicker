package com.rtchagas.pingplacepicker.repository.googlemaps

import android.location.Location
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.model.AddressComponents
import com.google.android.libraries.places.api.model.OpeningHours
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlusCode
import kotlin.math.absoluteValue

/**
 * Place without any additional info. Just latitude and longitude.
 */
internal class PlaceFromCoordinates(private val latitude: Double, private val longitude: Double) : Place() {
    constructor(parcel: Parcel) : this(
            parcel.readDouble(),
            parcel.readDouble())

    override fun getUserRatingsTotal(): Int? {
        return null
    }

    override fun getName(): String? {
        return "${formatLatitude(latitude)}, ${formatLongitude(longitude)}"
    }

    override fun getOpeningHours(): OpeningHours? {
        return null
    }

    override fun getId(): String? {
        return null
    }

    override fun getPhotoMetadatas(): MutableList<PhotoMetadata> {
        return mutableListOf()
    }

    override fun getWebsiteUri(): Uri? {
        return null
    }

    override fun getPhoneNumber(): String? {
        return null
    }

    override fun getRating(): Double? {
        return null
    }

    override fun getPriceLevel(): Int? {
        return null
    }

    override fun getAddressComponents(): AddressComponents? {
        return null
    }

    override fun getAttributions(): MutableList<String> {
        return mutableListOf()
    }

    override fun getAddress(): String? {
        return null
    }

    override fun getPlusCode(): PlusCode? {
        return null
    }

    override fun getUtcOffsetMinutes(): Int? {
        return null
    }

    override fun getTypes(): MutableList<Type> {
        return mutableListOf()
    }

    override fun getViewport(): LatLngBounds? {
        return null
    }

    override fun getLatLng(): LatLng? {
        return LatLng(latitude, longitude)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PlaceFromCoordinates> {
        override fun createFromParcel(parcel: Parcel): PlaceFromCoordinates {
            return PlaceFromCoordinates(parcel)
        }

        override fun newArray(size: Int): Array<PlaceFromCoordinates?> {
            return arrayOfNulls(size)
        }
    }

    // formatting methods -----------------------------------------------------------------

    private fun formatLatitude(latitude: Double): String {
        val direction = if (latitude > 0) "N" else "S"
        return "${replaceDelimiters(Location.convert(latitude.absoluteValue,
                Location.FORMAT_SECONDS))} $direction"
    }

    private fun formatLongitude(longitude: Double): String {
        val direction = if (longitude > 0) "W" else "E"
        return "${replaceDelimiters(Location.convert(longitude.absoluteValue,
                Location.FORMAT_SECONDS))} $direction"
    }

    private fun replaceDelimiters(original: String): String {
        val parts = original.split(":")
        val idx = parts[2].indexOfAny(arrayOf(',','.').toCharArray())
        val seconds = parts[2].subSequence(0, idx)
        return "${parts[0]}° ${parts[1]}' $seconds\""
    }
}