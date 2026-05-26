package com.rtchagas.pingplacepicker.repository.googlemaps

import android.annotation.SuppressLint
import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.rtchagas.pingplacepicker.Config
import com.rtchagas.pingplacepicker.PingPlacePicker
import com.rtchagas.pingplacepicker.model.SearchResult
import com.rtchagas.pingplacepicker.model.SimplePlace
import com.rtchagas.pingplacepicker.repository.PlaceRepository
import kotlinx.coroutines.tasks.await
import java.util.*

internal class GoogleMapsRepository(
    private val googleClient: PlacesClient,
    private val googleMapsAPI: GoogleMapsAPI,
) : PlaceRepository {

    /**
     * Finds all nearby places ranked by likelihood of being the place where the device is.
     *
     * Charged per
     * [Places SDK billing](https://developers.google.com/places/android-sdk/usage-and-billing#find-current-place).
     */
    @SuppressLint("MissingPermission")
    override suspend fun getNearbyPlaces(): List<Place> {
        val request = FindCurrentPlaceRequest.builder(getPlaceFields()).build()
        val response = googleClient.findCurrentPlace(request).await()
        return sortByLikelihood(response.placeLikelihoods).map { it.place }
    }

    /**
     * Finds all nearby places ranked by distance from the requested location.
     *
     * Charged per
     * [Places Web API billing](https://developers.google.com/maps/billing/understanding-cost-of-use#nearby-search).
     */
    override suspend fun getNearbyPlaces(location: LatLng): List<Place> {
        val locationParam = "${location.latitude},${location.longitude}"
        val searchResult = googleMapsAPI.searchNearby(locationParam, PingPlacePicker.mapsApiKey)
        return searchResult.results.map { mapToCustomPlace(it) }
    }

    /**
     * Fetches a photo for the place.
     *
     * Charged per
     * [Places SDK billing](https://developers.google.com/places/android-sdk/usage-and-billing#places-photo).
     */
    override suspend fun getPlacePhoto(photoMetadata: PhotoMetadata): Bitmap {
        val photoRequest = FetchPhotoRequest.builder(photoMetadata)
            .setMaxWidth(Config.PLACE_IMG_WIDTH)
            .setMaxHeight(Config.PLACE_IMG_HEIGHT)
            .build()
        return googleClient.fetchPhoto(photoRequest).await().bitmap
    }

    /**
     * Resolves a place from its latitude/longitude via Google Maps Geocoding API.
     *
     * Charged per
     * [Geocoding API billing](https://developers.google.com/maps/documentation/geocoding/usage-and-billing#pricing-for-the-geocoding-api).
     */
    override suspend fun getPlaceByLocation(location: LatLng): Place {
        val paramLocation = "${location.latitude},${location.longitude}"
        val result: SearchResult = googleMapsAPI.findByLocation(paramLocation, PingPlacePicker.mapsApiKey)
        return if ("OK" == result.status && result.results.isNotEmpty()) {
            mapToCustomPlace(result.results[0])
        } else {
            PlaceFromCoordinates(location.latitude, location.longitude)
        }
    }

    /**
     * Free Place.Field set.
     * https://developers.google.com/places/android-sdk/usage-and-billing#basic-data
     */
    private fun getPlaceFields(): List<Place.Field> = listOf(
        Place.Field.ID,
        Place.Field.NAME,
        Place.Field.ADDRESS,
        Place.Field.LAT_LNG,
        Place.Field.TYPES,
        Place.Field.PHOTO_METADATAS,
    )

    private fun mapToCustomPlace(place: SimplePlace): CustomPlace {
        val photoList = place.photos.mapTo(mutableListOf()) {
            PhotoMetadata.builder(it.photoReference)
                .setAttributions(it.htmlAttributions.toString())
                .setHeight(it.height)
                .setWidth(it.width)
                .build()
        }

        val typeList = place.types.mapTo(mutableListOf()) { simpleType ->
            Place.Type.values()
                .find { it.name == simpleType.uppercase(Locale.US) } ?: Place.Type.OTHER
        }

        val latLng = LatLng(place.geometry.location.lat, place.geometry.location.lng)
        val address: String = place.formattedAddress.ifEmpty { place.vicinity }
        val name: String = buildPlaceName(place.name, address)

        return CustomPlace(place.placeId, name, photoList, address, typeList, latLng)
    }

    private fun buildPlaceName(originalName: String, address: String): String {
        if (originalName.isNotEmpty()) return originalName
        return address.split(",").first()
    }

    /** Returns place likelihoods sorted with the best ranked first. */
    private fun sortByLikelihood(placeLikelihoods: List<PlaceLikelihood>): List<PlaceLikelihood> =
        placeLikelihoods.sortedByDescending { it.likelihood }
}
