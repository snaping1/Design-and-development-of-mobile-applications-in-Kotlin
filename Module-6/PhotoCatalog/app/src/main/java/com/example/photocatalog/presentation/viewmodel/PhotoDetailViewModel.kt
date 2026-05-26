package com.example.photocatalog.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photocatalog.data.downloader.ImageDownloader
import com.example.photocatalog.domain.model.PhotoEntity
import com.example.photocatalog.domain.usecase.GetPhotoByIdUseCase
import com.example.photocatalog.presentation.common.UiState
import com.example.photocatalog.presentation.navigation.NavArgs
import com.example.photocatalog.util.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPhotoByIdUseCase: GetPhotoByIdUseCase,
    private val imageDownloader: ImageDownloader
) : ViewModel() {

    private val photoId: String =
        checkNotNull(savedStateHandle[NavArgs.PHOTO_ID]) { "photoId required" }

    private val _state = MutableStateFlow<UiState<PhotoEntity>>(UiState.Loading)
    val state: StateFlow<UiState<PhotoEntity>> = _state.asStateFlow()

    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState: StateFlow<DownloadState> = _downloadState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            runCatching { getPhotoByIdUseCase(photoId) }
                .onSuccess { photo ->
                    _state.value = photo?.let { UiState.Success(it) }
                        ?: UiState.Error("Фото не найдено в кэше. Вернитесь к списку и попробуйте снова.")
                }
                .onFailure { e -> _state.value = UiState.Error(e.toUserMessage()) }
        }
    }

    fun downloadTo(uri: Uri) {
        val photo = (_state.value as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            _downloadState.value = DownloadState.InProgress
            imageDownloader.downloadTo(photo.downloadUrl, uri)
                .onSuccess { _downloadState.value = DownloadState.Done }
                .onFailure { e -> _downloadState.value = DownloadState.Failed(e.toUserMessage()) }
        }
    }

    fun consumeDownloadEvent() {
        _downloadState.update { DownloadState.Idle }
    }

    sealed interface DownloadState {
        data object Idle : DownloadState
        data object InProgress : DownloadState
        data object Done : DownloadState
        data class Failed(val message: String) : DownloadState
    }
}
