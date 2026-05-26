package com.rtchagas.pingplacepicker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.rtchagas.pingplacepicker.repository.PlaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class PlacePickerViewModel(private val repository: PlaceRepository) : ViewModel() {

    private val _places = MutableStateFlow<Resource<List<Place>>>(Resource.noData())
    val places: StateFlow<Resource<List<Place>>> = _places.asStateFlow()

    private var lastLoadedLocation: LatLng? = null

    fun loadNearbyPlaces(location: LatLng, radiusMeters: Double) {
        // Avoid charging Google twice for the same location once we already have data.
        if (lastLoadedLocation == location && _places.value.status == Resource.Status.SUCCESS) {
            return
        }
        lastLoadedLocation = location

        viewModelScope.launch {
            _places.value = Resource.loading()
            _places.value = try {
                Resource.success(repository.searchNearby(location, radiusMeters))
            } catch (t: Throwable) {
                Resource.error(t)
            }
        }
    }
}
