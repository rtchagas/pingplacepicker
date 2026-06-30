package com.rtchagas.pingplacepicker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.rtchagas.pingplacepicker.repository.PlaceRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class PlacePickerViewModel(private val repository: PlaceRepository) : ViewModel() {

    private val _places = MutableStateFlow<Resource<List<Place>>>(Resource.noData())
    val places: StateFlow<Resource<List<Place>>> = _places.asStateFlow()

    private val _selectedPlace = MutableSharedFlow<Resource<Place>>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val selectedPlace: SharedFlow<Resource<Place>> = _selectedPlace.asSharedFlow()

    private var lastLoadedLocation: LatLng? = null

    fun loadNearbyPlaces(location: LatLng, radiusMeters: Double) {
        // Avoid charging Google twice for the same location once we already have data.
        if (lastLoadedLocation == location && _places.value.status == Resource.Status.SUCCESS) {
            return
        }
        lastLoadedLocation = location

        viewModelScope.launch {
            _places.value = Resource.loading()
            runCatching { repository.searchNearby(location, radiusMeters) }
                .onSuccess { _places.value = Resource.success(it) }
                .onFailure { _places.value = Resource.error(it) }
        }
    }

    /**
     * Fetches the full details for a place selected through the Places
     * Autocomplete widget. The [token] returned by the widget is reused so the
     * autocomplete + details calls bill as a single session.
     */
    fun fetchPlace(placeId: String, token: AutocompleteSessionToken) {
        viewModelScope.launch {
            _selectedPlace.emit(Resource.loading())
            runCatching { repository.fetchPlace(placeId, token) }
                .onSuccess { _selectedPlace.emit(Resource.success(it)) }
                .onFailure { _selectedPlace.emit(Resource.error(it)) }
        }
    }
}
