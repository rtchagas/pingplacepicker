package com.rtchagas.pingplacepicker.repository

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import io.reactivex.Single

/**
 * We decided to interface the Places repository as there's a lot of
 * room to improve the place search and retrieval.
 * We could have different repositories to fetch places locally from
 * a cached database or from other providers than Google.
 */
interface PlaceRepository {

    fun getNearbyPlaces(): Single<List<Place>>

    fun getNearbyPlaces(location: LatLng): Single<List<Place>>

    fun getPlacePhoto(photoMetadata: PhotoMetadata): Single<Bitmap>

    fun getPlaceByLocation(location: LatLng): Single<Place>
}