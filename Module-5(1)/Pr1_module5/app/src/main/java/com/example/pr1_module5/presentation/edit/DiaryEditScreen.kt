package com.example.pr1_module5.presentation.edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pr1_module5.domain.model.DiaryEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryEditScreen(
    fileName: String? = null,
    onBack: () -> Unit,
    onSaved: (DiaryEntry) -> Unit,
    viewModel: DiaryEditViewModel = viewModel()
) {
    val title by viewModel.title.collectAsState()
    val text by viewModel.text.collectAsState()

    // Загружаем запись если открываем существующую
    LaunchedEffect(fileName) {
        if (fileName != null) viewModel.loadEntry(fileName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (fileName == null) "Новая запись" else "Редактировать") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Заголовок (необязательно)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = text,
                onValueChange = viewModel::onTextChange,
                label = { Text("Текст заметки") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 250.dp),
                minLines = 8
            )

            Button(
                onClick = {
                    viewModel.save { entry -> onSaved(entry) }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = text.isNotBlank()
            ) {
                Text("Сохранить")
            }

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Назад без сохранения")
            }
        }
    }
}