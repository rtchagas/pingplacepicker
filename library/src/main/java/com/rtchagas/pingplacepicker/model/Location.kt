package com.rtchagas.pingplacepicker.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Location(
    @Json(name = "lat")
    val lat: Double,
    @Json(name = "lng")
    val lng: Double
)
