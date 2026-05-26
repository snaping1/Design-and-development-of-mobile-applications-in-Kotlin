package com.example.pr3_module5.presentation.gallery

import android.app.Application
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pr3_module5.di.GalleryModule
import com.example.pr3_module5.domain.model.PhotoEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    private val getAllPhotosUseCase = GalleryModule.provideGetAllPhotosUseCase(application)
    private val savePhotoUseCase = GalleryModule.provideSavePhotoUseCase(application)
    private val exportPhotoUseCase = GalleryModule.provideExportPhotoUseCase(application)

    private val _photos = MutableStateFlow<List<PhotoEntry>>(emptyList())
    val photos: StateFlow<List<PhotoEntry>> = _photos

    private val _exportResult = MutableStateFlow<Boolean?>(null)
    val exportResult: StateFlow<Boolean?> = _exportResult

    private var pendingPhotoFile: File? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _photos.value = getAllPhotosUseCase()
        }
    }

    fun createCameraUri(): Uri {
        val file = savePhotoUseCase.createFile()
        pendingPhotoFile = file
        return FileProvider.getUriForFile(
            getApplication(),
            "${getApplication<Application>().packageName}.provider",
            file
        )
    }

    fun onPhotoCaptured(success: Boolean) {
        if (!success) {
            pendingPhotoFile = null
            return
        }
        val file = pendingPhotoFile ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val entry = savePhotoUseCase.addPhoto(file)
            withContext(Dispatchers.Main) {
                _photos.value = listOf(entry) + _photos.value
            }
        }
        pendingPhotoFile = null
    }

    fun exportPhoto(photoEntry: PhotoEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = exportPhotoUseCase(photoEntry)
            withContext(Dispatchers.Main) {
                _exportResult.value = result
            }
        }
    }

    fun clearExportResult() {
        _exportResult.value = null
    }
}