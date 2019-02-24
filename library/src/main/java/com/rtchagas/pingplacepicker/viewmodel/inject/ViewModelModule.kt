package com.rtchagas.pingplacepicker.viewmodel.inject

import androidx.lifecycle.ViewModel
import com.rtchagas.pingplacepicker.viewmodel.PlaceConfirmDialogViewModel
import com.rtchagas.pingplacepicker.viewmodel.PlacePickerViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(PlacePickerViewModel::class)
    abstract fun bindPlacePickerViewModel(viewViewModel: PlacePickerViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PlaceConfirmDialogViewModel::class)
    abstract fun bindPlaceConfirmDialogViewModel(viewViewModel: PlaceConfirmDialogViewModel): ViewModel
}
