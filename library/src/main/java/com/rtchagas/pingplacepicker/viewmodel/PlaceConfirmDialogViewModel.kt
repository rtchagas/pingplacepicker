package com.rtchagas.pingplacepicker.viewmodel

import android.graphics.Bitmap
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

    private val _placePhoto = MutableStateFlow<Resource<Bitmap>>(Resource.noData())
    val placePhoto: StateFlow<Resource<Bitmap>> = _placePhoto.asStateFlow()

    fun loadPlacePhoto(photoMetadata: PhotoMetadata) {
        // Don't refetch (and recharge) if we already have a result.
        if (_placePhoto.value.status == Resource.Status.SUCCESS) return

        viewModelScope.launch {
            _placePhoto.value = Resource.loading()
            _placePhoto.value = try {
                Resource.success(repository.fetchPhoto(photoMetadata))
            } catch (t: Throwable) {
                Resource.error(t)
            }
        }
    }
}
