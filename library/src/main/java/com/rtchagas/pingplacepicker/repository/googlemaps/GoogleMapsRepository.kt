package com.rtchagas.pingplacepicker.repository.googlemaps

import android.annotation.SuppressLint
import android.graphics.Bitmap
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
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.model.PlusCode
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.rtchagas.pingplacepicker.Config
import com.rtchagas.pingplacepicker.PingPlacePicker
import com.rtchagas.pingplacepicker.model.SearchResult
import com.rtchagas.pingplacepicker.repository.PlaceRepository
import io.reactivex.Single
import kotlin.math.absoluteValue


class GoogleMapsRepository constructor(
        private val googleClient: PlacesClient,
        private val googleMapsAPI: GoogleMapsAPI)
    : PlaceRepository {


    /**
     * Finds all nearby places ranked by likelihood of being the place where the device is.
     *
     * This call will be charged according to
     * [Places SDK for Android Usage and
       Billing](https://developers.google.com/places/android-sdk/usage-and-billing#find-current-place)
     */
    @SuppressLint("MissingPermission")
    override fun getNearbyPlaces(): Single<List<Place>> {

        // Create request
        val request = FindCurrentPlaceRequest.builder(getPlaceFields()).build()

        return Single.create { emitter ->
            googleClient.findCurrentPlace(request).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.let {
                        val placeList = sortByLikelihood(it.placeLikelihoods)
                        emitter.onSuccess(placeList.map { likelihood -> likelihood.place })
                    }
                    // Empty result
                    emitter.onSuccess(listOf())
                }
                else {
                    emitter.onError(task.exception ?: Exception("No places for you..."))
                }
            }
        }
    }

    /** Finds all nearby places ranked by distance from the requested location.
     *
     * This call will be charged according to
     * [Places SDK WEB API Usage and
    Billing](https://developers.google.com/maps/billing/understanding-cost-of-use#nearby-search)
     */
    override fun getNearbyPlaces(location: LatLng): Single<List<Place>> {

        val locationParam = "${location.latitude},${location.longitude}"

        return googleMapsAPI.searchNearby(locationParam, PingPlacePicker.mapsApiKey)
                .flatMap { searchResult ->

                    val singles = mutableListOf<Single<Place>>()

                    searchResult.results.forEach {
                        singles.add(getPlaceById(it.placeId))
                    }

                    return@flatMap Single.zip(singles) { listOfResults ->
                        val places = mutableListOf<Place>()
                        listOfResults.forEach {
                            places.add(it as Place)
                        }
                        return@zip places
                    }
                }
    }

    /**
     * Fetches a photo for the place.
     *
     * This call will be charged according to
     * [Places SDK for Android Usage and
       Billing](https://developers.google.com/places/android-sdk/usage-and-billing#places-photo)
     */
    override fun getPlacePhoto(photoMetadata: PhotoMetadata): Single<Bitmap> {

        // Create a FetchPhotoRequest.
        val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                .setMaxWidth(Config.PLACE_IMG_WIDTH)
                .setMaxHeight(Config.PLACE_IMG_HEIGHT)
                .build()

        return Single.create { emitter ->
            googleClient.fetchPhoto(photoRequest).addOnSuccessListener {
                val bitmap = it.bitmap
                emitter.onSuccess(bitmap)
            }.addOnFailureListener {
                emitter.onError(it)
            }
        }
    }

    /**
     * Uses Google Maps GeoLocation API to retrieve a place by its latitude and longitude.
     * This call will be charged according to
     * [Places SDK for Android Usage and
       Billing](https://developers.google.com/maps/documentation/geocoding/usage-and-billing#pricing-for-the-geocoding-api)
     */
    override fun getPlaceByLocation(location: LatLng): Single<Place?> {

        val paramLocation = "${location.latitude},${location.longitude}"

        return googleMapsAPI.findByLocation(paramLocation, PingPlacePicker.mapsApiKey)
                .flatMap { result: SearchResult ->
                    if (("OK" == result.status) && result.results.isNotEmpty()) {
                        return@flatMap getPlaceById(result.results[0].placeId)
                    }
                    return@flatMap Single.just(PlaceFromCoordinates(location.latitude, location.longitude))
                }
    }

    /**
     * Place without any additional info. Just latitude and longitude.
     */
    private class PlaceFromCoordinates(private val latitude: Double, private val longitude: Double) : Place() {
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

    /**
     * This call to Google Places API is totally free :)
     */
    private fun getPlaceById(placeId: String): Single<Place> {

        // Create the request
        val request = FetchPlaceRequest.builder(placeId, getPlaceFields()).build()

        return Single.create { emitter ->
            googleClient.fetchPlace(request)
                    .addOnSuccessListener {
                        emitter.onSuccess(it.place)
                    }
                    .addOnFailureListener {
                        emitter.onError(it)
                    }
        }
    }

    /**
     * These fields are not charged by Google.
     * https://developers.google.com/places/android-sdk/usage-and-billing#basic-data
     */
    private fun getPlaceFields(): List<Place.Field> {

        return listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG,
                Place.Field.TYPES,
                Place.Field.PHOTO_METADATAS)
    }

    /**
     * Sorts the list by Likelihood. The best ranked places come first.
     */
    private fun sortByLikelihood(placeLikelihoods: List<PlaceLikelihood>): List<PlaceLikelihood> {

        val mutableList = placeLikelihoods.toMutableList()

        mutableList.sortByDescending { it.likelihood }

        return mutableList
    }
}