package com.rtchagas.pingplacepicker.repository.inject

import android.content.Context
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.rtchagas.pingplacepicker.PingPlacePicker
import com.rtchagas.pingplacepicker.repository.PlaceRepository
import com.rtchagas.pingplacepicker.repository.googlemaps.GoogleMapsAPI
import com.rtchagas.pingplacepicker.repository.googlemaps.GoogleMapsRepository
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun providePlaceRepository(placesClient: PlacesClient, googleMapsAPI: GoogleMapsAPI)
            : PlaceRepository {
        return GoogleMapsRepository(placesClient, googleMapsAPI)
    }

    @Singleton
    @Provides
    fun providesGooglePlacesClient(context: Context): PlacesClient {
        Places.initialize(context, PingPlacePicker.androidApiKey)
        return Places.createClient(context)
    }

    @Singleton
    @Provides
    fun provideGoogleMapsAPI(): GoogleMapsAPI {

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.NONE

        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/")
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()

        return retrofit.create(GoogleMapsAPI::class.java)
    }
}