package com.nidhi.app.feature.benefits

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import com.nidhi.app.domain.model.Benefit
import com.nidhi.app.domain.model.BenefitStatus
import com.nidhi.app.ui.components.CardSkeleton
import com.nidhi.app.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BenefitsScreen(
    onBenefitClick: (String) -> Unit,
    viewModel: BenefitsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val filtered by viewModel.filteredBenefits.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Government Benefits") },
                actions = {
                    IconButton(onClick = { viewModel.checkEligibility() }) {
                        Icon(Icons.Default.Refresh, "Check Eligibility")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(BenefitFilter.values()) { filter ->
                    FilterChip(
                        selected = uiState.selectedFilter == filter,
                        onClick = { viewModel.setFilter(filter) },
                        label = { Text(filter.label) }
                    )
                }
            }

            if (uiState.isLoading) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(5) { CardSkeleton(Modifier.fillMaxWidth().height(100.dp)) }
                }
            } else if (filtered.isEmpty()) {
                // Eligibility filter empty state (Req 16.5)
                val isEligibilityFilter = uiState.selectedFilter == BenefitFilter.ELIGIBLE
                if (isEligibilityFilter) {
                    EmptyState(
                        icon = Icons.Default.SearchOff,
                        message = "No eligible schemes found. Add family members and documents to improve your score."
                    )
                } else {
                    EmptyState(
                        icon = Icons.Default.SearchOff,
                        message = "No benefits in this category"
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered, key = { it.id }) { benefit ->
                        BenefitCard(benefit = benefit, onClick = { onBenefitClick(benefit.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun BenefitCard(benefit: Benefit, onClick: () -> Unit) {
    val (statusColor, statusLabel, statusIcon) = when (benefit.status) {
        BenefitStatus.ELIGIBLE -> Triple(
            Color(0xFF2D6A4F), "Eligible", Icons.Default.CheckCircle
        )
        BenefitStatus.MISSING_DOCS -> Triple(
            Color(0xFFE76F51), "Missing Docs", Icons.Default.Description
        )
        BenefitStatus.INELIGIBLE -> Triple(
            MaterialTheme.colorScheme.outline, "Ineligible", Icons.Default.Cancel
        )
        BenefitStatus.UNKNOWN -> Triple(
            MaterialTheme.colorScheme.primary, "Check Eligibility", Icons.Default.HelpOutline
        )
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(benefit.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold)
                    Text(benefit.category.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary)
                }

                Surface(
                    color = statusColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(statusIcon, null, Modifier.size(14.dp), tint = statusColor)
                        Text(statusLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = statusColor)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(benefit.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 2)

            if (benefit.requiredDocTypes.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    benefit.requiredDocTypes.take(3).forEach { doc ->
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                doc,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                    if (benefit.requiredDocTypes.size > 3) {
                        Text("+${benefit.requiredDocTypes.size - 3} more",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }
}
