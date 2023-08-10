package com.rtchagas.pingplacepicker.repository.googlemaps

import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.model.*
import kotlinx.parcelize.Parcelize
import com.google.android.libraries.places.api.model.Place.BooleanPlaceAttributeValue.UNKNOWN

@Parcelize
internal class CustomPlace(
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

    /**
     * Default value only.
     * Clients shouldn't rely on this.
     */
    override fun getBusinessStatus(): BusinessStatus {
        return BusinessStatus.OPERATIONAL
    }

    override fun getName(): String {
        return placeName
    }

    override fun getOpeningHours(): OpeningHours? {
        return null
    }

    override fun getCurbsidePickup(): BooleanPlaceAttributeValue {
        return UNKNOWN
    }

    override fun getDelivery(): BooleanPlaceAttributeValue {
        return UNKNOWN
    }

    override fun getDineIn(): BooleanPlaceAttributeValue {
        return UNKNOWN
    }

    override fun getReservable(): BooleanPlaceAttributeValue {
        return UNKNOWN
    }

    override fun getServesBeer(): BooleanPlaceAttributeValue {
        return UNKNOWN
    }

    override fun getServesBreakfast(): BooleanPlaceAttributeValue {
        return UNKNOWN
    }

    override fun getServesBrunch(): BooleanPlaceAttributeValue {
        return UNKNOWN
    }

    override fun getServesDinner(): BooleanPlaceAttributeValue {
        return UNKNOWN
    }

    override fun getServesLunch(): BooleanPlaceAttributeValue {
        return UNKNOWN
    }

    override fun getServesVegetarianFood(): BooleanPlaceAttributeValue {
        return UNKNOWN
    }

    override fun getServesWine(): BooleanPlaceAttributeValue {
        return UNKNOWN
    }

    override fun getTakeout(): BooleanPlaceAttributeValue {
        return UNKNOWN
    }

    override fun getWheelchairAccessibleEntrance(): BooleanPlaceAttributeValue {
        return UNKNOWN
    }

    override fun getId(): String {
        return placeId
    }

    override fun getPhotoMetadatas(): MutableList<PhotoMetadata> {
        return placePhotos
    }

    override fun getSecondaryOpeningHours(): MutableList<OpeningHours>? {
        return null
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

    override fun getIconBackgroundColor(): Int? {
        return null
    }

    override fun getPriceLevel(): Int? {
        return null
    }

    override fun getAddressComponents(): AddressComponents? {
        return null
    }

    override fun getCurrentOpeningHours(): OpeningHours? {
        return null
    }

    override fun getAttributions(): MutableList<String> {
        return mutableListOf()
    }

    override fun getAddress(): String {
        return placeAddress
    }

    override fun getIconUrl(): String? {
        return null
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

    override fun getLatLng(): LatLng {
        return placeLatLng
    }
}
