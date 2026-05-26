package com.rtchagas.pingplacepicker.repository.googlemaps

import com.rtchagas.pingplacepicker.model.SearchResult
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

internal interface GoogleMapsAPI {

    @GET("place/nearbysearch/json?rankby=distance")
    suspend fun searchNearby(
        @Query("location") location: String,
        @Query("key") apiKey: String,
        @Query("language") language: String = Locale.getDefault().language,
    ): SearchResult

    @GET("geocode/json")
    suspend fun findByLocation(
        @Query("latlng") location: String,
        @Query("key") apiKey: String,
        @Query("language") language: String = Locale.getDefault().language,
    ): SearchResult
}
