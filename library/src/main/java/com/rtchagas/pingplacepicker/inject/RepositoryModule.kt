package com.rtchagas.pingplacepicker.inject

import com.google.android.libraries.places.api.Places
import com.rtchagas.pingplacepicker.PingPlacePicker
import com.rtchagas.pingplacepicker.repository.PlaceRepository
import com.rtchagas.pingplacepicker.repository.googlemaps.GoogleMapsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module

internal val repositoryModule = module {

    single {
        // Places SDK 5.x: opt into the "new" Places API for SearchNearby + new fields.
        Places.initializeWithNewPlacesApiEnabled(androidContext(), PingPlacePicker.androidApiKey)
        Places.createClient(androidContext())
    }

    single {
        GoogleMapsRepository(placesClient = get())
    } bind PlaceRepository::class
}
