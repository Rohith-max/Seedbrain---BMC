package com.nidhi.app.feature.benefits

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import com.nidhi.app.domain.model.Benefit
import com.nidhi.app.domain.model.BenefitStatus
import com.nidhi.app.ui.components.GlassCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BenefitDetailScreen(
    benefitId: String,
    onBack: () -> Unit,
    onOpenUrl: (url: String, title: String) -> Unit = { _, _ -> },
    viewModel: BenefitsViewModel = koinViewModel()
) {
    val allBenefits by viewModel.uiState.collectAsState()
    val benefit = allBenefits.benefits.find { it.id == benefitId }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(benefit?.name ?: "Benefit") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        benefit?.let { b ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                GlassCard {
                    Column {
                        Text(b.name, style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold)
                        Text(b.category.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(8.dp))
                        Text(b.description, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                // Status
                val statusColor = when (b.status) {
                    BenefitStatus.ELIGIBLE -> MaterialTheme.colorScheme.primary
                    BenefitStatus.MISSING_DOCS -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.outline
                }
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            when (b.status) {
                                BenefitStatus.ELIGIBLE -> Icons.Default.CheckCircle
                                BenefitStatus.MISSING_DOCS -> Icons.Default.Warning
                                else -> Icons.Default.Info
                            },
                            null,
                            tint = statusColor
                        )
                        Text(
                            when (b.status) {
                                BenefitStatus.ELIGIBLE -> "You appear eligible for this scheme"
                                BenefitStatus.MISSING_DOCS -> "Upload missing documents to apply"
                                BenefitStatus.INELIGIBLE -> "You don't meet eligibility criteria"
                                BenefitStatus.UNKNOWN -> "Tap 'Check Eligibility' from the list to evaluate"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = statusColor
                        )
                    }
                }

                // Required documents
                if (b.requiredDocTypes.isNotEmpty()) {
                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Required Documents",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold)
                            b.requiredDocTypes.forEach { doc ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.Description, null,
                                        Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary)
                                    Text(doc, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }

                // Eligibility criteria
                if (b.eligibilityCriteria.isNotEmpty()) {
                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Eligibility Criteria",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold)
                            b.eligibilityCriteria.forEach { criterion ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.CheckBoxOutlineBlank, null,
                                        Modifier.size(16.dp))
                                    Text(criterion.description,
                                        style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }

                // Official website button — opens in-app WebView
                b.officialUrl?.let { url ->
                    Button(
                        onClick = { onOpenUrl(url, b.name) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Language, null)
                        Spacer(Modifier.width(8.dp))
                        Text("View Official Website")
                    }
                    OutlinedButton(
                        onClick = {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.OpenInNew, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Open in Browser")
                    }
                }
            }
        } ?: Box(
            Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
