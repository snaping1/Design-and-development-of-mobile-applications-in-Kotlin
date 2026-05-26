package com.example.heartrate.ui.theme

import android.os.Build
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.heartrate.viewmodel.HeartRateViewModel
import com.google.accompanist.permissions.*

private val blePermissions: List<String>
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        listOf(
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT
        )
    } else {
        listOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HeartRateScreen(
    viewModel: HeartRateViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val permissionsState = rememberMultiplePermissionsState(permissions = blePermissions)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(24.dp)
        ) {

            Text(
                text = "Heart Rate Monitor",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            HeartRateDisplay(
                heartRate = uiState.heartRate,
                isConnected = uiState.isConnected
            )

            StatusCard(
                message = uiState.statusMessage,
                isError = uiState.isError,
                deviceName = uiState.deviceName
            )

            if (permissionsState.allPermissionsGranted) {
                BleControls(
                    isScanning = uiState.isScanning,
                    isConnected = uiState.isConnected,
                    onStart = { viewModel.startScanning() },
                    onStop = { viewModel.stopAndDisconnect() }
                )
            } else {
                PermissionsRequest(
                    permissionsState = permissionsState
                )
            }
        }
    }
}

@Composable
private fun HeartRateDisplay(
    heartRate: Int?,
    isConnected: Boolean
) {

    val infiniteTransition = rememberInfiniteTransition(label = "heartbeat")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isConnected && heartRate != null) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (heartRate != null) (60_000 / (heartRate.coerceIn(40, 200))) else 800,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(200.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                color = when {
                    heartRate != null -> MaterialTheme.colorScheme.errorContainer
                    isConnected -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "❤️",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (heartRate != null) "$heartRate" else "--",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "bpm",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatusCard(
    message: String,
    isError: Boolean,
    deviceName: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isError)
                MaterialTheme.colorScheme.errorContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = if (isError)
                    MaterialTheme.colorScheme.onErrorContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (deviceName != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Устройство: $deviceName",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun BleControls(
    isScanning: Boolean,
    isConnected: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    val isActive = isScanning || isConnected

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = onStart,
            enabled = !isActive,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            if (isScanning) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(if (isScanning) "Поиск..." else "▶ Начать")
        }

        OutlinedButton(
            onClick = onStop,
            enabled = isActive,
            modifier = Modifier.weight(1f)
        ) {
            Text("■ Стоп")
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PermissionsRequest(
    permissionsState: MultiplePermissionsState
) {
    val allDenied = permissionsState.permissions.all {
        it.status == PermissionStatus.Denied(shouldShowRationale = false)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (allDenied)
                    "Разрешения отклонены. Откройте Настройки приложения и выдайте разрешения вручную."
                else
                    "Для работы с Bluetooth необходимо предоставить разрешения",
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        if (!allDenied) {
            Button(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
                Text("Предоставить разрешения")
            }
        }
    }
}