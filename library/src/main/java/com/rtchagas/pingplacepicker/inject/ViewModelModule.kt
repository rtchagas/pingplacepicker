package com.rtchagas.pingplacepicker.inject

import com.rtchagas.pingplacepicker.viewmodel.PlaceConfirmDialogViewModel
import com.rtchagas.pingplacepicker.viewmodel.PlacePickerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val viewModelModule = module {

    viewModel { PlacePickerViewModel(get()) }

    viewModel { PlaceConfirmDialogViewModel(get()) }

}