package com.rtchagas.pingplacepicker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.rtchagas.pingplacepicker.repository.PlaceRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
internal class AutocompleteViewModel(
    private val repository: PlaceRepository,
) : ViewModel() {

    private val sessionToken: AutocompleteSessionToken = AutocompleteSessionToken.newInstance()

    private var bias: LatLng = LatLng(0.0, 0.0)
    private var radiusMeters: Double = DEFAULT_RADIUS_M

    private val _query = MutableStateFlow("")

    private val _predictions =
        MutableStateFlow<Resource<List<AutocompletePrediction>>>(Resource.noData())
    val predictions: StateFlow<Resource<List<AutocompletePrediction>>> = _predictions.asStateFlow()

    private val _selectedPlace = MutableSharedFlow<Resource<Place>>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val selectedPlace: SharedFlow<Resource<Place>> = _selectedPlace.asSharedFlow()

    init {
        _query
            .debounce(QUERY_DEBOUNCE_MS)
            .distinctUntilChanged()
            .onEach { runQuery(it) }
            .launchIn(viewModelScope)
    }

    fun configure(bias: LatLng, radiusMeters: Double) {
        this.bias = bias
        this.radiusMeters = radiusMeters
    }

    fun setQuery(query: String) {
        _query.value = query
    }

    fun selectPrediction(prediction: AutocompletePrediction) {
        viewModelScope.launch {
            _selectedPlace.emit(Resource.loading())
            runCatching {
                repository.fetchPlace(prediction.placeId, sessionToken)
            }.onSuccess {
                _selectedPlace.emit(Resource.success(it))
            }.onFailure {
                _selectedPlace.emit(Resource.error(it))
            }
        }
    }

    private suspend fun runQuery(query: String) {
        if (query.isBlank()) {
            _predictions.value = Resource.noData()
            return
        }
        _predictions.value = Resource.loading()
        runCatching {
            repository.autocomplete(query, bias, radiusMeters, sessionToken)
        }.onSuccess {
            _predictions.value = Resource.success(it)
        }.onFailure {
            _predictions.value = Resource.error(it)
        }
    }

    private companion object {
        const val QUERY_DEBOUNCE_MS = 300L
        const val DEFAULT_RADIUS_M = 5000.0
    }
}
