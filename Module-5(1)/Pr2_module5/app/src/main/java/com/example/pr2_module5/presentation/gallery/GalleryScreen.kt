package com.example.pr2_module5.presentation.gallery

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pr2_module5.domain.model.PhotoEntry
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun GalleryScreen(viewModel: GalleryViewModel = viewModel()) {
    val context = LocalContext.current
    val photos by viewModel.photos.collectAsState()
    val exportResult by viewModel.exportResult.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var photoUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var photoToExport by remember { mutableStateOf<PhotoEntry?>(null) }

    // Показ Snackbar после экспорта
    LaunchedEffect(exportResult) {
        if (exportResult != null) {
            snackbarHostState.showSnackbar(
                if (exportResult == true) "Фото добавлено в галерею"
                else "Ошибка экспорта"
            )
            viewModel.clearExportResult()
        }
    }

    // Лаунчер камеры
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        viewModel.onPhotoCaptured(success)
    }

    // Лаунчер разрешения камеры
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = viewModel.createCameraUri()
            photoUri = uri
            cameraLauncher.launch(uri)
        }
    }

    // Диалог экспорта
    photoToExport?.let { entry ->
        AlertDialog(
            onDismissRequest = { photoToExport = null },
            title = { Text("Экспорт фото") },
            text = { Text("Добавить фото в общую галерею устройства?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.exportPhoto(entry)
                    photoToExport = null
                }) { Text("Экспортировать") }
            },
            dismissButton = {
                TextButton(onClick = { photoToExport = null }) { Text("Отмена") }
            }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Моя галерея") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }) {
                Icon(Icons.Default.AddAPhoto, contentDescription = "Сделать фото")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        if (photos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("У вас пока нет фото", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }) {
                        Text("Сделать первое фото")
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(photos, key = { it.fileName }) { photo ->
                    AsyncImage(
                        model = File(photo.filePath),
                        contentDescription = photo.fileName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .combinedClickable(
                                onClick = {},
                                onLongClick = { photoToExport = photo }
                            )
                    )
                }
            }
        }
    }
}