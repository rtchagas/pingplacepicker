package com.rtchagas.pingplacepicker.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.rtchagas.pingplacepicker.repository.PlaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class PlaceConfirmDialogViewModel(
    private val repository: PlaceRepository,
) : ViewModel() {

    private val _placePhotoUri = MutableStateFlow<Resource<Uri>>(Resource.noData())
    val placePhotoUri: StateFlow<Resource<Uri>> = _placePhotoUri.asStateFlow()

    fun loadPlacePhoto(photoMetadata: PhotoMetadata) {
        // Don't refetch (and recharge) if we already have a result.
        if (_placePhotoUri.value.status == Resource.Status.SUCCESS) return

        viewModelScope.launch {
            _placePhotoUri.value = Resource.loading()
            runCatching {
                repository.fetchPhotoUri(photoMetadata)
            }.onSuccess {
                _placePhotoUri.value = Resource.success(it)
            }.onFailure {
                _placePhotoUri.value = Resource.error(it)
            }
        }
    }
}
