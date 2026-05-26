package com.example.heartrate.ble

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "BleManager"

sealed class BleState {
    object Idle : BleState()
    object Scanning : BleState()
    data class DeviceFound(val device: BluetoothDevice) : BleState()
    object Connecting : BleState()
    object Connected : BleState()
    data class HeartRateReceived(val bpm: Int) : BleState()
    data class Error(val message: String) : BleState()
    object Disconnected : BleState()
}

@SuppressLint("MissingPermission")   // разрешения проверяются в ViewModel/UI
class BleManager(private val context: Context) {

    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    private var scanner: BluetoothLeScanner? = null
    private var gatt: BluetoothGatt? = null

    private val _state = MutableStateFlow<BleState>(BleState.Idle)
    val state: StateFlow<BleState> = _state.asStateFlow()

    fun startScan() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            _state.value = BleState.Error("Bluetooth выключен или недоступен")
            return
        }

        scanner = bluetoothAdapter.bluetoothLeScanner
        if (scanner == null) {
            _state.value = BleState.Error("BluetoothLeScanner недоступен")
            return
        }

        // Фильтруем только устройства с Heart Rate Service
        val scanFilter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(BleConstants.HEART_RATE_SERVICE))
            .build()

        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)   // максимальная скорость
            .setCallbackType(ScanSettings.CALLBACK_TYPE_FIRST_MATCH)
            .build()

        _state.value = BleState.Scanning
        Log.d(TAG, "Начинаем сканирование...")

        scanner?.startScan(listOf(scanFilter), scanSettings, scanCallback)
    }

    /** Останавливает активное сканирование */
    fun stopScan() {
        scanner?.stopScan(scanCallback)
        scanner = null
        Log.d(TAG, "Сканирование остановлено")
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val device = result.device
            Log.d(TAG, "Найдено устройство: ${device.name ?: "Unknown"} [${device.address}]")

            stopScan()
            _state.value = BleState.DeviceFound(device)
            connectToDevice(device)
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            val msg = when (errorCode) {
                SCAN_FAILED_ALREADY_STARTED -> "Сканирование уже запущено"
                SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> "Ошибка регистрации приложения"
                SCAN_FAILED_FEATURE_UNSUPPORTED -> "BLE не поддерживается"
                else -> "Ошибка сканирования: $errorCode"
            }
            _state.value = BleState.Error(msg)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GATT подключение
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Подключается к устройству через GATT.
     * autoConnect = false → быстрое прямое подключение.
     */
    private fun connectToDevice(device: BluetoothDevice) {
        Log.d(TAG, "Подключаемся к ${device.address}...")
        _state.value = BleState.Connecting

        // На Android 6+ можно передать TRANSPORT_LE для принудительного BLE
        gatt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            device.connectGatt(
                context,
                /* autoConnect = */ false,
                gattCallback,
                BluetoothDevice.TRANSPORT_LE
            )
        } else {
            device.connectGatt(context, false, gattCallback)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GATT Callback
    // ─────────────────────────────────────────────────────────────────────────

    private val gattCallback = object : BluetoothGattCallback() {

        /** Вызывается при изменении статуса соединения */
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            Log.d(TAG, "onConnectionStateChange: status=$status newState=$newState")
            when {
                newState == BluetoothProfile.STATE_CONNECTED && status == BluetoothGatt.GATT_SUCCESS -> {
                    Log.d(TAG, "✅ Подключено! Запускаем service discovery...")
                    _state.value = BleState.Connected
                    // ОБЯЗАТЕЛЬНО: открываем сервисы перед чтением характеристик
                    gatt.discoverServices()
                }

                newState == BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.d(TAG, "❌ Отключено")
                    _state.value = BleState.Disconnected
                    gatt.close()
                }

                else -> {
                    Log.w(TAG, "GATT error: status=$status")
                    _state.value = BleState.Error("GATT ошибка: статус $status")
                    gatt.close()
                }
            }
        }

        /** Вызывается после завершения discoverServices() */
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                _state.value = BleState.Error("Ошибка обнаружения сервисов: $status")
                return
            }

            Log.d(TAG, "Сервисы обнаружены. Ищем Heart Rate Measurement...")

            val hrCharacteristic = gatt
                .getService(BleConstants.HEART_RATE_SERVICE)
                ?.getCharacteristic(BleConstants.HEART_RATE_MEASUREMENT)

            if (hrCharacteristic == null) {
                _state.value = BleState.Error("Heart Rate Measurement не найдена")
                return
            }

            // Включаем нотификации локально (на уровне Android)
            val notifyEnabled = gatt.setCharacteristicNotification(hrCharacteristic, true)
            Log.d(TAG, "setCharacteristicNotification: $notifyEnabled")

            // Включаем нотификации на устройстве через запись CCCD дескриптора
            val cccd = hrCharacteristic.getDescriptor(BleConstants.CCCD)
            if (cccd == null) {
                _state.value = BleState.Error("CCCD дескриптор не найден")
                return
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // API 33+: новый API для записи дескриптора
                gatt.writeDescriptor(cccd, BleConstants.ENABLE_NOTIFICATION)
            } else {
                @Suppress("DEPRECATION")
                cccd.value = BleConstants.ENABLE_NOTIFICATION
                @Suppress("DEPRECATION")
                gatt.writeDescriptor(cccd)
            }
            Log.d(TAG, "CCCD записан — Notify включён")
        }

        /** Вызывается при получении нотификации (новые данные пульса) — API ≤ 32 */
        @Deprecated("Deprecated in API 33, used for compatibility")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            if (characteristic.uuid == BleConstants.HEART_RATE_MEASUREMENT) {
                @Suppress("DEPRECATION")
                val bpm = parseHeartRate(characteristic.value)
                Log.d(TAG, "💓 Heart Rate: $bpm bpm")
                _state.value = BleState.HeartRateReceived(bpm)
            }
        }

        /** API 33+ версия onCharacteristicChanged */
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            if (characteristic.uuid == BleConstants.HEART_RATE_MEASUREMENT) {
                val bpm = parseHeartRate(value)
                Log.d(TAG, "💓 Heart Rate: $bpm bpm")
                _state.value = BleState.HeartRateReceived(bpm)
            }
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "✅ CCCD успешно записан — ожидаем данные пульса...")
            } else {
                _state.value = BleState.Error("Ошибка записи CCCD: $status")
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Парсинг Heart Rate Measurement (0x2A37)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Парсит пакет характеристики 0x2A37 согласно GATT-спецификации.
     *
     * Формат пакета:
     * Байт 0 — Flags:
     *   bit 0 = 0 → Heart Rate Value в байте [1] (UInt8)
     *   bit 0 = 1 → Heart Rate Value в байтах [1-2] (UInt16, little-endian)
     *
     * Пример пакета [0x00, 0x48]:
     *   Flags=0x00 → UInt8 формат, bpm = 0x48 = 72
     */
    private fun parseHeartRate(data: ByteArray): Int {
        if (data.isEmpty()) return 0

        val flags = data[0].toInt()
        val isUint16Format = (flags and 0x01) != 0   // bit 0 флага

        return if (isUint16Format && data.size >= 3) {
            // UInt16 little-endian: младший байт [1], старший [2]
            (data[1].toInt() and 0xFF) or ((data[2].toInt() and 0xFF) shl 8)
        } else if (data.size >= 2) {
            // UInt8: просто байт [1]
            data[1].toInt() and 0xFF
        } else {
            0
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Очистка
    // ─────────────────────────────────────────────────────────────────────────

    /** Отключается от GATT и освобождает ресурсы */
    fun disconnect() {
        stopScan()
        gatt?.disconnect()
        gatt?.close()
        gatt = null
        _state.value = BleState.Idle
    }
}