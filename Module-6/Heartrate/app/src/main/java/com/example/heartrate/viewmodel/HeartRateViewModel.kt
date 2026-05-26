package com.example.heartrate.viewmodel

import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.heartrate.ble.BleState
import com.example.heartrate.repository.HeartRateRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HeartRateUiState(
    val isScanning: Boolean = false,
    val isConnected: Boolean = false,
    val heartRate: Int? = null,
    val deviceName: String? = null,
    val statusMessage: String = "Нажмите «Начать» для поиска",
    val isError: Boolean = false
)

class HeartRateViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HeartRateRepository(application)

    private val _uiState = MutableStateFlow(HeartRateUiState())

    val uiState: StateFlow<HeartRateUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.bleState.collect { bleState ->
                _uiState.update { current ->
                    mapBleStateToUiState(bleState, current)
                }
            }
        }
    }

    private fun mapBleStateToUiState(
        bleState: BleState,
        current: HeartRateUiState
    ): HeartRateUiState = when (bleState) {
        is BleState.Idle -> HeartRateUiState(
            statusMessage = "Нажмите «Начать» для поиска"
        )

        is BleState.Scanning -> current.copy(
            isScanning = true,
            statusMessage = "🔍 Поиск устройств...",
            isError = false
        )

        is BleState.DeviceFound -> {
            val name = try {
                bleState.device.name ?: bleState.device.address
            } catch (e: SecurityException) {
                "Неизвестное устройство"
            }
            current.copy(
                isScanning = false,
                deviceName = name,
                statusMessage = "Найдено: $name"
            )
        }

        is BleState.Connecting -> current.copy(
            statusMessage = "Подключение к ${current.deviceName ?: "устройству"}..."
        )

        is BleState.Connected -> current.copy(
            isConnected = true,
            statusMessage = "Подключено — ожидание данных..."
        )

        is BleState.HeartRateReceived -> current.copy(
            isConnected = true,
            heartRate = bleState.bpm,
            statusMessage = "Данные получены"
        )

        is BleState.Disconnected -> HeartRateUiState(
            statusMessage = "Отключено. Нажмите «Начать» снова"
        )

        is BleState.Error -> HeartRateUiState(
            statusMessage = bleState.message,
            isError = true
        )
    }

    private fun hasRequiredPermissions(): Boolean {
        val ctx = getApplication<Application>()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.BLUETOOTH_SCAN) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.BLUETOOTH_CONNECT) ==
                    PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
        }
    }

    fun startScanning() {
        if (!hasRequiredPermissions()) {
            _uiState.update { it.copy(statusMessage = "Нет разрешений для BLE", isError = true) }
            return
        }
        try {
            repository.startScan()
        } catch (e: SecurityException) {
            _uiState.update { it.copy(statusMessage = "Ошибка доступа: ${e.message}", isError = true) }
        }
    }

    fun stopAndDisconnect() {
        if (!hasRequiredPermissions()) {
            _uiState.update { it.copy(statusMessage = "Нет разрешений для BLE", isError = true) }
            return
        }
        try {
            repository.disconnect()
        } catch (e: SecurityException) {
            _uiState.update { it.copy(statusMessage = "Ошибка доступа: ${e.message}", isError = true) }
        }
    }

    override fun onCleared() {
        super.onCleared()

        try {
            repository.disconnect()
        } catch (e: SecurityException) {}
    }
}