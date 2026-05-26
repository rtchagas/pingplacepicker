package com.rtchagas.pingplacepicker.repository

import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place

/**
 * Abstraction over the place data source so the picker can later swap in a
 * cached or non-Google backend without touching the UI layer.
 */
internal interface PlaceRepository {

    suspend fun searchNearby(location: LatLng, radiusMeters: Double): List<Place>

    suspend fun fetchPhotoUri(photoMetadata: PhotoMetadata): Uri

    suspend fun autocomplete(
        query: String,
        bias: LatLng,
        radiusMeters: Double,
        sessionToken: AutocompleteSessionToken,
    ): List<AutocompletePrediction>

    suspend fun fetchPlace(placeId: String, sessionToken: AutocompleteSessionToken): Place
}
