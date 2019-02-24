package com.rtchagas.pingplacepicker.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.rtchagas.pingplacepicker.repository.PlaceRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class PlaceConfirmDialogViewModel @Inject constructor(private var repository: PlaceRepository)
    : BaseViewModel() {

    private val placePhotoLiveData: MutableLiveData<Resource<Bitmap>> = MutableLiveData()

    fun getPlacePhoto(photoMetadata: PhotoMetadata): LiveData<Resource<Bitmap>> {

        // If we already loaded the places for this location, return the same live data
        // instead of fetching (and charging) again.
        placePhotoLiveData.value?.run {
            return placePhotoLiveData
        }

        val disposable: Disposable = repository.getPlacePhoto(photoMetadata)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { placePhotoLiveData.value = Resource.loading() }
                .subscribe(
                        { result: Bitmap -> placePhotoLiveData.value = Resource.success(result) },
                        { error: Throwable -> placePhotoLiveData.value = Resource.error(error) }
                )

        // Keep track of this disposable during the ViewModel lifecycle
        addDisposable(disposable)

        return placePhotoLiveData
    }
}