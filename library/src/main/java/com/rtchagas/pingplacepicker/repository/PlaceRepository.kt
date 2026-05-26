package com.rtchagas.pingplacepicker.repository

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place

/**
 * Abstraction over the place data source so the picker can later swap in a
 * cached or non-Google backend without touching the UI layer.
 */
internal interface PlaceRepository {

    suspend fun getNearbyPlaces(): List<Place>

    suspend fun getNearbyPlaces(location: LatLng): List<Place>

    suspend fun getPlacePhoto(photoMetadata: PhotoMetadata): Bitmap

    suspend fun getPlaceByLocation(location: LatLng): Place
}
