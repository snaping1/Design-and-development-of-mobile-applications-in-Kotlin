package com.example.nobellaureatesclient.presentation.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nobellaureatesclient.domain.model.Laureate
import com.example.nobellaureatesclient.domain.model.NobelPrize
import com.example.nobellaureatesclient.presentation.common.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrizeDetailsScreen(
    onBack: () -> Unit,
    viewModel: PrizeDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()

    val currentPrize = (state as? UiState.Success)?.data
    val isFavorite = currentPrize?.id?.let { it in favoriteIds } == true

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Детали премии",
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    if (currentPrize != null) {
                        IconButton(onClick = viewModel::toggleFavorite) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                contentDescription = if (isFavorite) "Убрать из избранного" else "В избранное",
                                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when (val s = state) {
                is UiState.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }

                is UiState.Error -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Ошибка: ${s.message}",
                            color = MaterialTheme.colorScheme.error,
                        )
                        Spacer(Modifier.height(12.dp))
                        FilledTonalButton(
                            onClick = viewModel::retry,
                            shape = RoundedCornerShape(14.dp),
                        ) { Text("Повторить") }
                    }
                }

                is UiState.Success -> PrizeContent(prize = s.data)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrizeContent(prize: NobelPrize) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            text = prize.year.toString(),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary,
        )
        AssistChip(
            onClick = {},
            enabled = false,
            label = { Text(prize.category.displayName) },
            colors = AssistChipDefaults.assistChipColors(
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
            ),
        )

        prize.motivation?.takeIf { it.isNotBlank() }?.let {
            ElevatedCard(
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(18.dp),
                )
            }
        }

        Spacer(Modifier.height(4.dp))
        Text(
            text = "Лауреаты",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        if (prize.laureates.isEmpty()) {
            Text(
                text = "Лауреаты не указаны",
                style = MaterialTheme.typography.bodyMedium,
            )
        } else {
            prize.laureates.forEachIndexed { index, laureate ->
                LaureateBlock(laureate = laureate)
                if (index != prize.laureates.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                }
            }
        }
    }
}

@Composable
private fun LaureateBlock(laureate: Laureate) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = laureate.fullName.firstOrNull()?.uppercase() ?: "?",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = laureate.fullName,
                style = MaterialTheme.typography.titleMedium,
            )
            val years = buildString {
                laureate.birthDate?.let { append("род. $it") }
                laureate.deathDate?.let {
                    if (isNotEmpty()) append(" · ")
                    append("ум. $it")
                }
            }
            if (years.isNotBlank()) {
                Text(
                    text = years,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            laureate.affiliation?.takeIf { it.isNotBlank() }?.let {
                Text(text = "Аффилиация: $it", style = MaterialTheme.typography.bodyMedium)
            }
            laureate.share?.let {
                Text(text = "Доля: 1/$it", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
