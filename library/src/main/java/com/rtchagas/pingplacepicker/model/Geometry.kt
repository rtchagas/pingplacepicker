package com.rtchagas.pingplacepicker.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Geometry(
    @Json(name = "location")
    val location: Location
)
