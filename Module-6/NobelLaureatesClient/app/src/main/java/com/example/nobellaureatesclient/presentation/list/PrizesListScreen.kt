package com.example.nobellaureatesclient.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nobellaureatesclient.domain.model.NobelCategory
import com.example.nobellaureatesclient.domain.model.NobelPrize
import com.example.nobellaureatesclient.presentation.common.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrizesListScreen(
    onPrizeClick: (NobelPrize) -> Unit,
    onFavoritesClick: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: PrizesListViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Нобелевские лауреаты",
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    IconButton(onClick = onFavoritesClick) {
                        Icon(Icons.Filled.Bookmark, contentDescription = "Избранное")
                    }
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Filled.Person, contentDescription = "Профиль")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            FiltersPanel(
                year = state.yearInput,
                category = state.selectedCategory,
                onYearChange = viewModel::onYearChange,
                onCategoryChange = viewModel::onCategoryChange,
                onApply = viewModel::applyFilters,
            )

            Box(modifier = Modifier.fillMaxSize()) {
                when (val s = state.prizes) {
                    is UiState.Loading -> LoadingView()
                    is UiState.Error -> ErrorView(message = s.message, onRetry = viewModel::retry)
                    is UiState.Success -> PrizesList(
                        prizes = s.data,
                        favoriteIds = favoriteIds,
                        onPrizeClick = onPrizeClick,
                        onToggleFavorite = viewModel::toggleFavorite,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FiltersPanel(
    year: String,
    category: NobelCategory,
    onYearChange: (String) -> Unit,
    onCategoryChange: (NobelCategory) -> Unit,
    onApply: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = year,
                    onValueChange = onYearChange,
                    label = { Text("Год") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(120.dp),
                )
                Spacer(Modifier.width(12.dp))

                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.weight(1f),
                ) {
                    OutlinedTextField(
                        value = category.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Категория") },
                        shape = RoundedCornerShape(14.dp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        NobelCategory.entries.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item.displayName) },
                                onClick = {
                                    onCategoryChange(item)
                                    expanded = false
                                },
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            FilledTonalButton(
                onClick = onApply,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.align(Alignment.End),
            ) {
                Text("Применить")
            }
        }
    }
}

@Composable
private fun PrizesList(
    prizes: List<NobelPrize>,
    favoriteIds: Set<Int>,
    onPrizeClick: (NobelPrize) -> Unit,
    onToggleFavorite: (NobelPrize) -> Unit,
) {
    if (prizes.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text("Ничего не найдено", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        itemsIndexed(
            items = prizes,
            key = { _, prize -> prize.id },
        ) { _, prize ->
            PrizeCard(
                prize = prize,
                isFavorite = prize.id in favoriteIds,
                onClick = { onPrizeClick(prize) },
                onToggleFavorite = { onToggleFavorite(prize) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrizeCard(
    prize: NobelPrize,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, end = 6.dp, top = 14.dp, bottom = 14.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = prize.year.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.width(10.dp))
                    AssistChip(
                        onClick = {},
                        enabled = false,
                        label = { Text(prize.category.displayName) },
                        colors = AssistChipDefaults.assistChipColors(
                            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            disabledLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        ),
                    )
                }
                val motivation = prize.motivation.orEmpty()
                if (motivation.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    val preview = motivation.take(160).let {
                        if (motivation.length > 160) "$it…" else it
                    }
                    Text(
                        text = preview,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                    contentDescription = if (isFavorite) "Убрать из избранного" else "В избранное",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Ошибка: $message",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
            )
            Spacer(Modifier.height(12.dp))
            FilledTonalButton(
                onClick = onRetry,
                shape = RoundedCornerShape(14.dp),
            ) { Text("Повторить") }
        }
    }
}
