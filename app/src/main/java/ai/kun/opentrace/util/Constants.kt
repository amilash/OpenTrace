package ai.kun.opentrace.util

import java.util.*

object Constants {
    const val PREF_UNIQUE_ID = "ai.kun.opentrace.preferences"
    const val RANGE_ENVIRONMENTAL = 3  // Environmental factor for calculating device range
    const val MANUFACTURE_SUBSTRING = "9b3"
    const val MANUFACTURE_ID = 1023
    var SERVICE_STRING = "0994cd95-6228-4a4c-aae2-4df9b3d03ca5"
    var SERVICE_UUID =
        UUID.fromString(SERVICE_STRING)
    var CHARACTERISTIC_DEVICE_STRING = "9a161ec7-72bb-40d3-b00c-fcf637349b5b"
    var CHARACTERISTIC_DEVICE_UUID =
        UUID.fromString(CHARACTERISTIC_DEVICE_STRING)
    var CHARACTERISTIC_USER_STRING = "57be15c2-4776-4af6-b2af-c24d9c8711c5"
    var CHARACTERISTIC_USER_UUID =
        UUID.fromString(CHARACTERISTIC_USER_STRING)

    var CLIENT_CONFIGURATION_DESCRIPTOR_STRING =
        "d8608ea1-e0df-4807-9676-f59a59bf6dfb"
    var CLIENT_CONFIGURATION_DESCRIPTOR_UUID =
        UUID.fromString(CLIENT_CONFIGURATION_DESCRIPTOR_STRING)
    const val USER_SHORT_ID = "6228"
    const val DEVICE_SHORT_ID = "e0df"
    const val CLIENT_CONFIGURATION_DESCRIPTOR_SHORT_ID = "8ea1"
    const val SCAN_PERIOD: Long = 4000
    const val BROADCAST_PERIOD: Long = 14000
    const val BACKGROUND_TRACE_INTERVAL = 30000
    const val FOREGROUND_TRACE_INTERVAL = 10000

    var CHARACTERISTIC_ECHO_STRING = "0994cd95-6228-4a4c-aae2-4df9b3d03ca5"
    var CHARACTERISTIC_ECHO_UUID =
        UUID.fromString(CHARACTERISTIC_ECHO_STRING)
    var CHARACTERISTIC_TIME_STRING = "0994cd95-6228-4a4c-aae2-4df9b3d03ca5"
    var CHARACTERISTIC_TIME_UUID =
        UUID.fromString(CHARACTERISTIC_TIME_STRING)

}