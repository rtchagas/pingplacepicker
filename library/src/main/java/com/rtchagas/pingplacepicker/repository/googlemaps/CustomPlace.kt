package com.rtchagas.pingplacepicker.repository.googlemaps

import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.model.*
import kotlinx.android.parcel.Parcelize

@Parcelize
class CustomPlace(
    var placeId: String,
    var placeName: String,
    var placePhotos: MutableList<PhotoMetadata>,
    var placeAddress: String,
    var placeTypes: MutableList<Type>,
    var placeLatLng: LatLng
) : Place() {

    override fun getUserRatingsTotal(): Int? {
        return null
    }

    override fun getName(): String? {
        return placeName
    }

    override fun getOpeningHours(): OpeningHours? {
        return null
    }

    override fun getId(): String? {
        return placeId
    }

    override fun getPhotoMetadatas(): MutableList<PhotoMetadata> {
        return placePhotos
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
        return placeAddress
    }

    override fun getPlusCode(): PlusCode? {
        return null
    }

    override fun getUtcOffsetMinutes(): Int? {
        return null
    }

    override fun getTypes(): MutableList<Type> {
        return placeTypes
    }

    override fun getViewport(): LatLngBounds? {
        return null
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun getLatLng(): LatLng? {
        return placeLatLng
    }
}
