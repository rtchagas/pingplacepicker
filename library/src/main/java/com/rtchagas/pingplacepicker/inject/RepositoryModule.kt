package com.rtchagas.pingplacepicker.inject

import com.google.android.libraries.places.api.Places
import com.rtchagas.pingplacepicker.PingPlacePicker
import com.rtchagas.pingplacepicker.repository.PlaceRepository
import com.rtchagas.pingplacepicker.repository.googlemaps.GoogleMapsAPI
import com.rtchagas.pingplacepicker.repository.googlemaps.GoogleMapsRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

internal val repositoryModule = module {

    single {
        Places.initialize(androidContext(), PingPlacePicker.androidApiKey)
        Places.createClient(androidContext())
    }

    single(createdAtStart = true) {
        val interceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.NONE }
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
            .create(GoogleMapsAPI::class.java)
    }

    single {
        GoogleMapsRepository(googleClient = get(), googleMapsAPI = get())
    } bind PlaceRepository::class
}
