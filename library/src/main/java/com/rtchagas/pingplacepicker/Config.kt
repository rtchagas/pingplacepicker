package com.rtchagas.pingplacepicker

object Config {

    const val PLACE_IMG_WIDTH = 640
    const val PLACE_IMG_HEIGHT = 320

    const val STATIC_MAP_URL = "https://maps.googleapis.com/maps/api/staticmap?" +
            "size=${PLACE_IMG_WIDTH}x${PLACE_IMG_HEIGHT}" +
            "&markers=color:red|%.6f,%.6f" +
            "&key=%s" +
            "&language=%s"

    const val STATIC_MAP_URL_STYLE_DARK = "&style=element:geometry%7Ccolor:0x242f3e" +
            "&style=element:labels.text.fill%7Ccolor:0x746855" +
            "&style=element:labels.text.stroke%7Ccolor:0x242f3e" +
            "&style=feature:administrative.locality%7Celement:labels.text.fill%7Ccolor:0xd59563" +
            "&style=feature:poi%7Celement:labels.text.fill%7Ccolor:0xd59563" +
            "&style=feature:poi.park%7Celement:geometry%7Ccolor:0x263c3f" +
            "&style=feature:poi.park%7Celement:labels.text.fill%7Ccolor:0x6b9a76" +
            "&style=feature:road%7Celement:geometry%7Ccolor:0x38414e" +
            "&style=feature:road%7Celement:geometry.stroke%7Ccolor:0x212a37" +
            "&style=feature:road%7Celement:labels.text.fill%7Ccolor:0x9ca5b3" +
            "&style=feature:road.highway%7Celement:geometry%7Ccolor:0x746855" +
            "&style=feature:road.highway%7Celement:geometry.stroke%7Ccolor:0x1f2835" +
            "&style=feature:road.highway%7Celement:labels.text.fill%7Ccolor:0xf3d19c" +
            "&style=feature:transit%7Celement:geometry%7Ccolor:0x2f3948" +
            "&style=feature:transit.station%7Celement:labels.text.fill%7Ccolor:0xd59563" +
            "&style=feature:water%7Celement:geometry%7Ccolor:0x17263c" +
            "&style=feature:water%7Celement:labels.text.fill%7Ccolor:0x515c6d" +
            "&style=feature:water%7Celement:labels.text.stroke%7Ccolor:0x17263c"
}