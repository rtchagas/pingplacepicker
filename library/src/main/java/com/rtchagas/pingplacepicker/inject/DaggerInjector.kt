package com.rtchagas.pingplacepicker.inject

import android.app.Application

class DaggerInjector private constructor() {

    companion object {

        private var injector: PingComponent? = null

        private fun buildComponent(application: Application): PingComponent {
            return DaggerPingComponent.builder()
                    .androidModule(AndroidModule(application)).build()
        }

        fun getInjector(application: Application): PingComponent {
            return injector ?: buildComponent(application)
        }
    }
}