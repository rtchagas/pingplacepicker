package com.rtchagas.pingplacepicker.inject

import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.KoinComponent

object PingKoinContext {

    var koinApp: KoinApplication? = null

}

interface PingKoinComponent : KoinComponent {

    override fun getKoin(): Koin = PingKoinContext.koinApp?.koin!!

}