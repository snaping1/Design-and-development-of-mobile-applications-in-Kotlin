package com.example.photocatalog.presentation.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.photocatalog.domain.model.PhotoEntity
import com.example.photocatalog.presentation.common.UiState
import com.example.photocatalog.presentation.viewmodel.PhotoDetailViewModel
import com.example.photocatalog.presentation.viewmodel.PhotoDetailViewModel.DownloadState

private val SidePadding = 20.dp
private val GapM = 12.dp
private val GapL = 20.dp
private val CardCorner = 20.dp
private val FabReservedBottom = 96.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailScreen(
    onBack: () -> Unit,
    viewModel: PhotoDetailViewModel = hiltViewModel(),
) {
    val ui by viewModel.state.collectAsStateWithLifecycle()
    val download by viewModel.downloadState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val barScroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(download) {
        val event = download
        if (event is DownloadState.Done) {
            snackbar.showSnackbar("Готово — файл лежит в Downloads")
            viewModel.consumeDownloadEvent()
        } else if (event is DownloadState.Failed) {
            snackbar.showSnackbar("Не получилось сохранить: ${event.message}")
            viewModel.consumeDownloadEvent()
        }
    }

    val photo = (ui as? UiState.Success)?.data
    val saver = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("image/jpeg"),
    ) { uri: Uri? ->
        uri?.let(viewModel::downloadTo)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(barScroll.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Просмотр") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                        )
                    }
                },
                scrollBehavior = barScroll,
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            if (photo != null) {
                SaveToDownloadsFab(
                    inProgress = download is DownloadState.InProgress,
                    onClick = { saver.launch("picsum_${photo.id}.jpg") },
                )
            }
        },
    ) { insets ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets),
            color = MaterialTheme.colorScheme.surface,
        ) {
            when (val state = ui) {
                UiState.Loading -> CenterSpinner()
                is UiState.Error -> ErrorBlock(state.message, viewModel::load)
                is UiState.Success -> PhotoBody(state.data)
            }
        }
    }
}

@Composable
private fun CenterSpinner() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(strokeWidth = 3.dp)
    }
}

@Composable
private fun ErrorBlock(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(SidePadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Filled.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp),
        )
        Spacer(Modifier.height(GapM))
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(GapL))
        FilledTonalButton(onClick = onRetry) {
            Icon(Icons.Filled.Refresh, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Повторить попытку")
        }
    }
}

@Composable
private fun PhotoBody(photo: PhotoEntity) {
    val ratio = if (photo.height <= 0) 1f else photo.width.toFloat() / photo.height.toFloat()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = SidePadding)
            .padding(top = GapM, bottom = FabReservedBottom),
        verticalArrangement = Arrangement.spacedBy(GapL),
    ) {
        ElevatedCard(shape = RoundedCornerShape(CardCorner)) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(photo.downloadUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Фотография автора ${photo.author}",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ratio)
                    .clip(RoundedCornerShape(CardCorner)),
            )
        }

        AuthorRow(author = photo.author)
        DimensionsRow(width = photo.width, height = photo.height)
    }
}

@Composable
private fun AuthorRow(author: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.width(GapM))
        Column {
            Text(
                text = "Автор",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = author,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}

@Composable
private fun DimensionsRow(width: Int, height: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        AssistChip(onClick = {}, label = { Text("Ширина: $width") })
        AssistChip(onClick = {}, label = { Text("Высота: $height") })
    }
}

@Composable
private fun SaveToDownloadsFab(inProgress: Boolean, onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        expanded = !inProgress,
        icon = {
            if (inProgress) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            } else {
                Icon(Icons.Filled.Download, contentDescription = null)
            }
        },
        text = { Text(if (inProgress) "Сохраняем…" else "Сохранить в Downloads") },
    )
}
