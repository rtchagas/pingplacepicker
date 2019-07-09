package com.rtchagas.pingplacepicker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.rtchagas.pingplacepicker.repository.PlaceRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class PlacePickerViewModel constructor(private var repository: PlaceRepository)
    : BaseViewModel() {

    // Keep the place list in this view model state
    private val devicePlaceList: MutableLiveData<Resource<List<Place>>> = MutableLiveData()
    private val markerPlaceList: MutableLiveData<Resource<List<Place>>> = MutableLiveData()

    private var lastDeviceLocation: LatLng = LatLng(0.0, 0.0)
    private var lastMarkerLocation: LatLng = LatLng(0.0, 0.0)

    fun getNearbyDevicePlaces(location: LatLng): LiveData<Resource<List<Place>>> {

        // If we already loaded the places for this location, return the same live data
        // instead of fetching (and charging) again.
        devicePlaceList.value?.let {
            if (lastDeviceLocation == location) return devicePlaceList
        }

        // Update the last fetched location
        lastDeviceLocation = location

        val disposable: Disposable = repository.getNearbyPlaces()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { devicePlaceList.value = Resource.loading() }
                .subscribe(
                        { result: List<Place> -> devicePlaceList.value = Resource.success(result) },
                        { error: Throwable -> devicePlaceList.value = Resource.error(error) }
                )

        // Keep track of this disposable during the ViewModel lifecycle
        addDisposable(disposable)

        return devicePlaceList
    }

    fun getNearbyMarkerPlaces(location: LatLng): LiveData<Resource<List<Place>>> {

        // If we already loaded the places for this location, return the same live data
        // instead of fetching (and charging) again.
        markerPlaceList.value?.let {
            if (lastMarkerLocation == location) return markerPlaceList
        }

        // Update the last fetched location
        lastMarkerLocation = location

        val disposable: Disposable = repository.getPlacesByLocation(location)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { markerPlaceList.value = Resource.loading() }
                .subscribe(
                        { result: List<Place> -> markerPlaceList.value = Resource.success(result) },
                        { error: Throwable -> markerPlaceList.value = Resource.error(error) }
                )

        // Keep track of this disposable during the ViewModel lifecycle
        addDisposable(disposable)

        return markerPlaceList
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