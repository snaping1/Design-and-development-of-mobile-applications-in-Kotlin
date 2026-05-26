package com.example.heartrate.ble

import java.util.UUID

object BleConstants {

    val HEART_RATE_SERVICE: UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")

    val HEART_RATE_MEASUREMENT: UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")

    val BODY_SENSOR_LOCATION: UUID = UUID.fromString("00002a38-0000-1000-8000-00805f9b34fb")

    val CCCD: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    val ENABLE_NOTIFICATION: ByteArray = byteArrayOf(0x01, 0x00)
    val DISABLE_NOTIFICATION: ByteArray = byteArrayOf(0x00, 0x00)
}