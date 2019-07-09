package com.rtchagas.pingsample

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

class App : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}