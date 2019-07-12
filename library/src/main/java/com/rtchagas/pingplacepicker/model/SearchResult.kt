package com.rtchagas.pingplacepicker.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResult(
        val results: List<SimplePlace>,
        val status: String
)