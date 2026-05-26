package com.example.heartrate.repository

import android.content.Context
import com.example.heartrate.ble.BleManager
import com.example.heartrate.ble.BleState
import kotlinx.coroutines.flow.StateFlow

class HeartRateRepository(context: Context) {

    private val bleManager = BleManager(context)

    val bleState: StateFlow<BleState> = bleManager.state

    fun startScan() = bleManager.startScan()

    fun stopScan() = bleManager.stopScan()

    fun disconnect() = bleManager.disconnect()
}