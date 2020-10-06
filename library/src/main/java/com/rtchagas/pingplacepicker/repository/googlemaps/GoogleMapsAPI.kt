package com.rtchagas.pingplacepicker.repository.googlemaps

import com.rtchagas.pingplacepicker.model.SearchResult
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

interface GoogleMapsAPI {
    @GET("place/nearbysearch/json?rankby=distance")
    fun searchNearby(@Query("location") location: String,
                     @Query("key") apiKey: String,
                     @Query("language") language: String = Locale.getDefault().language)
            : Single<SearchResult>

    @GET("geocode/json")
    fun findByLocation(@Query("latlng") location: String,
                       @Query("key") apiKey: String,
                       @Query("language") language: String = Locale.getDefault().language)
            : Single<SearchResult>
}