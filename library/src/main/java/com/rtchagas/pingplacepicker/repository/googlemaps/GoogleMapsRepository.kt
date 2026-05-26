package com.rtchagas.pingplacepicker.repository.googlemaps

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import com.rtchagas.pingplacepicker.Config
import com.rtchagas.pingplacepicker.repository.PlaceRepository
import kotlinx.coroutines.tasks.await

internal class GoogleMapsRepository(
    private val placesClient: PlacesClient,
) : PlaceRepository {

    override suspend fun searchNearby(location: LatLng, radiusMeters: Double): List<Place> {
        val bounds = CircularBounds.newInstance(location, radiusMeters)
        val request = SearchNearbyRequest.builder(bounds, NEARBY_FIELDS)
            .setRankPreference(SearchNearbyRequest.RankPreference.DISTANCE)
            .build()
        return placesClient.searchNearby(request).await().places
    }

    override suspend fun fetchPhoto(photoMetadata: PhotoMetadata): Bitmap {
        val request = FetchPhotoRequest.builder(photoMetadata)
            .setMaxWidth(Config.PLACE_IMG_WIDTH)
            .setMaxHeight(Config.PLACE_IMG_HEIGHT)
            .build()
        return placesClient.fetchPhoto(request).await().bitmap
    }

    override suspend fun autocomplete(
        query: String,
        bias: LatLng,
        radiusMeters: Double,
        sessionToken: AutocompleteSessionToken,
    ): List<AutocompletePrediction> {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .setOrigin(bias)
            .setLocationBias(CircularBounds.newInstance(bias, radiusMeters))
            .setSessionToken(sessionToken)
            .build()
        return placesClient.findAutocompletePredictions(request).await().autocompletePredictions
    }

    override suspend fun fetchPlace(placeId: String, sessionToken: AutocompleteSessionToken): Place {
        val request = FetchPlaceRequest.builder(placeId, AUTOCOMPLETE_FIELDS)
            .setSessionToken(sessionToken)
            .build()
        return placesClient.fetchPlace(request).await().place
    }

    private companion object {
        // Field masks — request only what we render to keep billing minimal.
        // https://developers.google.com/maps/documentation/places/android-sdk/data-fields
        val NEARBY_FIELDS = listOf(
            Place.Field.ID,
            Place.Field.DISPLAY_NAME,
            Place.Field.FORMATTED_ADDRESS,
            Place.Field.LOCATION,
            Place.Field.TYPES,
            Place.Field.PHOTO_METADATAS,
        )

        val AUTOCOMPLETE_FIELDS = listOf(
            Place.Field.ID,
            Place.Field.DISPLAY_NAME,
            Place.Field.FORMATTED_ADDRESS,
            Place.Field.LOCATION,
            Place.Field.TYPES,
            Place.Field.PHOTO_METADATAS,
        )
    }
}
