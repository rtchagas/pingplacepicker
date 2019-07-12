package com.rtchagas.pingplacepicker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.rtchagas.pingplacepicker.PingPlacePicker
import com.rtchagas.pingplacepicker.repository.PlaceRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class PlacePickerViewModel constructor(private var repository: PlaceRepository)
    : BaseViewModel() {

    // Keep the place list in this view model state
    private val placeList: MutableLiveData<Resource<List<Place>>> = MutableLiveData()

    private var lastLocation: LatLng = LatLng(0.0, 0.0)

    fun getNearbyPlaces(location: LatLng): LiveData<Resource<List<Place>>> {

        // If we already loaded the places for this location, return the same live data
        // instead of fetching (and charging) again.
        placeList.value?.run {
            if (lastLocation == location) return placeList
        }

        // Update the last fetched location
        lastLocation = location

        val placeQuery =
                if (PingPlacePicker.isNearbySearchEnabled)
                    repository.getNearbyPlaces(location)
                else
                    repository.getNearbyPlaces()

        val disposable: Disposable = placeQuery
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { placeList.value = Resource.loading() }
                .subscribe(
                        { result: List<Place> -> placeList.value = Resource.success(result) },
                        { error: Throwable -> placeList.value = Resource.error(error) }
                )

        // Keep track of this disposable during the ViewModel lifecycle
        addDisposable(disposable)

        return placeList
    }

    fun getPlaceByLocation(location: LatLng): LiveData<Resource<Place?>> {

        val liveData = MutableLiveData<Resource<Place?>>()

        val disposable: Disposable = repository.getPlaceByLocation(location)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { liveData.value = Resource.loading() }
                .subscribe(
                        { result: Place? -> liveData.value = Resource.success(result) },
                        { error: Throwable -> liveData.value = Resource.error(error) }
                )

        // Keep track of this disposable during the ViewModel lifecycle
        addDisposable(disposable)

        return liveData
    }
}