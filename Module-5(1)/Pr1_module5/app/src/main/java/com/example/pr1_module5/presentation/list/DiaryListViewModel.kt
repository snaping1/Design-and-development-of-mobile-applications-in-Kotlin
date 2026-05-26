package com.example.pr1_module5.presentation.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pr1_module5.di.AppModule
import com.example.pr1_module5.domain.model.DiaryEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DiaryListViewModel(application: Application) : AndroidViewModel(application) {

    private val getAllEntries = AppModule.provideGetAllEntriesUseCase(application)
    private val deleteEntry = AppModule.provideDeleteEntryUseCase(application)

    private val _entries = MutableStateFlow<List<DiaryEntry>>(emptyList())
    val entries: StateFlow<List<DiaryEntry>> = _entries

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _entries.value = getAllEntries()
        }
    }

    fun addEntry(entry: DiaryEntry) {
        _entries.value = listOf(entry) + _entries.value
    }

    fun updateEntry(entry: DiaryEntry) {
        _entries.value = _entries.value.map {
            if (it.fileName == entry.fileName) entry else it
        }
    }

    fun deleteEntry(fileName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteEntry.invoke(fileName)
            _entries.value = _entries.value.filter { it.fileName != fileName }
        }
    }
}