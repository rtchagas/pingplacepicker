package com.rtchagas.pingplacepicker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.rtchagas.pingplacepicker.PingPlacePicker
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

    // One-shot events triggered when the user taps "select this place" on the map.
    private val _placeByLocation = MutableSharedFlow<Resource<Place?>>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val placeByLocation: SharedFlow<Resource<Place?>> = _placeByLocation.asSharedFlow()

    private var lastLoadedLocation: LatLng? = null

    fun loadNearbyPlaces(location: LatLng) {
        // Avoid charging Google twice for the same location once we already have data.
        if (lastLoadedLocation == location && _places.value.status == Resource.Status.SUCCESS) {
            return
        }
        lastLoadedLocation = location

        viewModelScope.launch {
            _places.value = Resource.loading()
            _places.value = try {
                val result = if (PingPlacePicker.isNearbySearchEnabled) {
                    repository.getNearbyPlaces(location)
                } else {
                    repository.getNearbyPlaces()
                }
                Resource.success(result)
            } catch (t: Throwable) {
                Resource.error(t)
            }
        }
    }

    fun loadPlaceByLocation(location: LatLng) {
        viewModelScope.launch {
            _placeByLocation.emit(Resource.loading())
            val resource = try {
                Resource.success<Place?>(repository.getPlaceByLocation(location))
            } catch (t: Throwable) {
                Resource.error(t)
            }
            _placeByLocation.emit(resource)
        }
    }
}
