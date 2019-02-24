package com.rtchagas.pingplacepicker.inject

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class AndroidModule(private val application: Application) {

    @Singleton
    @Provides
    fun provideContext(): Context {
        return application
    }
}