package com.rtchagas.pingplacepicker

object Config {

    const val PLACE_IMG_WIDTH = 640
    const val PLACE_IMG_HEIGHT = 320

    const val STATIC_MAP_URL = "https://maps.googleapis.com/maps/api/staticmap?" +
            "size=${PLACE_IMG_WIDTH}x$PLACE_IMG_HEIGHT" +
            "&markers=color:red|%.6f,%.6f" +
            "&key=%s"
}