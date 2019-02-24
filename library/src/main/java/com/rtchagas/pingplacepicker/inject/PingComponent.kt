package com.rtchagas.pingplacepicker.inject

import com.rtchagas.pingplacepicker.repository.inject.RepositoryModule
import com.rtchagas.pingplacepicker.ui.PlaceConfirmDialogFragment
import com.rtchagas.pingplacepicker.ui.PlacePickerActivity
import com.rtchagas.pingplacepicker.viewmodel.inject.ViewModelModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            AndroidModule::class,
            ViewModelModule::class,
            RepositoryModule::class
        ]
)
interface PingComponent {

    fun inject(activity: PlacePickerActivity)
    fun inject(fragment: PlaceConfirmDialogFragment)
}