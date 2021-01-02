package com.rtchagas.pingplacepicker.inject

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.Koin
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.dsl.koinApplication

object PingKoinContext {

    private lateinit var appContext: Context

    val koin: Koin by lazy {
        koinApplication {
            androidLogger()
            androidContext(appContext)
            modules(listOf(repositoryModule, viewModelModule))
        }.koin
    }

    /**
     * Initializes the Dependency Injection framework by passing
     * the current application context.
     */
    @Synchronized
    fun init(context: Context) {
        appContext = context.applicationContext
    }
}

@OptIn(KoinApiExtension::class)
interface PingKoinComponent : KoinComponent {

    override fun getKoin(): Koin = PingKoinContext.koin

}
