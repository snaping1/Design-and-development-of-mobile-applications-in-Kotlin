package com.example.pr1_module5.presentation.edit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pr1_module5.di.AppModule
import com.example.pr1_module5.domain.model.DiaryEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DiaryEditViewModel(application: Application) : AndroidViewModel(application) {

    private val saveEntry = AppModule.provideSaveEntryUseCase(application)
    private val getEntry = AppModule.provideGetEntryUseCase(application)

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title

    private val _text = MutableStateFlow("")
    val text: StateFlow<String> = _text

    private var currentFileName: String? = null

    fun loadEntry(fileName: String) {
        currentFileName = fileName
        viewModelScope.launch(Dispatchers.IO) {
            val entry = getEntry(fileName)
            entry?.let {
                _title.value = it.title
                _text.value = it.fullText
            }
        }
    }

    fun onTitleChange(value: String) { _title.value = value }
    fun onTextChange(value: String) { _text.value = value }

    fun save(onDone: (DiaryEntry) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val entry = saveEntry(_title.value, _text.value, currentFileName)
            withContext(Dispatchers.Main) {
                onDone(entry)
            }
        }
    }
}