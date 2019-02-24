package com.rtchagas.pingplacepicker.viewmodel.inject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

class PingViewModelFactory @Inject constructor(
        private val mViewModels: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        var factoryProvider: Provider<ViewModel>? = null

        for ((key, value) in mViewModels) {
            if (modelClass.isAssignableFrom(key)) {
                factoryProvider = value
                break
            }
        }

        factoryProvider?.let {
            return it.get() as T
        }

        throw IllegalArgumentException("Unknown ViewModel class name: $modelClass")
    }
}
