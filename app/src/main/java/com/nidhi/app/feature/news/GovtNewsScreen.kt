package com.nidhi.app.feature.news

import android.speech.tts.TextToSpeech
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import java.util.Locale

// ── Data model ────────────────────────────────────────────────────────────────
data class GovtNewsItem(
    val id: String,
    val title: String,
    val summary: String,
    val category: String,
    val url: String,
    val date: String,
    val isImportant: Boolean = false
)

// ── Curated government scheme news/updates ────────────────────────────────────
private val GOVT_NEWS = listOf(
    GovtNewsItem("1", "PM Awas Yojana 2.0 – 3 Crore New Homes",
        "The government has approved construction of 3 crore additional affordable houses under PMAY 2.0. Urban families with income below ₹9 lakh/year can apply.",
        "Housing", "https://pmay-urban.gov.in", "Jun 2025", true),
    GovtNewsItem("2", "Ayushman Bharat Cover Raised to ₹10 Lakh",
        "PMJAY health cover doubled from ₹5 lakh to ₹10 lakh per family per year. All existing beneficiaries automatically upgraded.",
        "Health", "https://pmjay.gov.in", "May 2025", true),
    GovtNewsItem("3", "PM-KISAN 19th Installment Released",
        "₹2,000 installment credited to 9.5 crore eligible farmer families. Check your status at pmkisan.gov.in or Aadhaar-linked bank account.",
        "Agriculture", "https://pmkisan.gov.in", "Jun 2025"),
    GovtNewsItem("4", "Sukanya Samriddhi Yojana Interest Rate 8.2%",
        "Government maintains 8.2% interest rate for SSY for Q2 2025-26. Tax-free returns on investment up to ₹1.5 lakh/year for girl child education.",
        "Savings", "https://nsiindia.gov.in", "Apr 2025"),
    GovtNewsItem("5", "Mudra Loan Limit Increased to ₹20 Lakh",
        "PM MUDRA Yojana Tarun category loan limit raised from ₹10 lakh to ₹20 lakh for small businesses. Apply through any bank or MUDRA portal.",
        "Business", "https://mudra.org.in", "Mar 2025", true),
    GovtNewsItem("6", "Free Ration Extended – PMGKAY Until Dec 2028",
        "Government extends free ration scheme for 81 crore beneficiaries under National Food Security Act through December 2028.",
        "Food Security", "https://dfpd.gov.in", "Jan 2025"),
    GovtNewsItem("7", "Aadhaar Face Authentication Now Available",
        "UIDAI launches face authentication for Aadhaar-based services. No fingerprint required — use phone camera for KYC and banking services.",
        "Digital", "https://uidai.gov.in", "May 2025"),
    GovtNewsItem("8", "EPF Interest Rate 8.25% for 2024-25",
        "EPFO announces 8.25% interest rate for Employee Provident Fund for FY 2024-25. Amount to be credited to 6.7 crore subscriber accounts.",
        "Finance", "https://epfindia.gov.in", "Apr 2025"),
    GovtNewsItem("9", "New Income Tax Regime – Zero Tax Till ₹12 Lakh",
        "Under New Tax Regime, individuals earning up to ₹12 lakh annually pay zero income tax with standard deduction. Applicable from FY 2025-26.",
        "Tax", "https://incometax.gov.in", "Feb 2025", true),
    GovtNewsItem("10", "Agniveer Pension Scheme Announced",
        "Government announces pension benefits for Agniveer soldiers after 4-year service. One-time corpus of ₹11.71 lakh plus pension for selected candidates.",
        "Defence", "https://joinindianarmy.nic.in", "Mar 2025"),
    GovtNewsItem("11", "PM Vishwakarma Scheme – ₹3 Lakh Loan for Artisans",
        "Traditional craftsmen and artisans can get collateral-free loans up to ₹3 lakh at 5% interest. Covers 18 trade categories including carpenter, blacksmith, potter.",
        "Artisans", "https://pmvishwakarma.gov.in", "Apr 2025"),
    GovtNewsItem("12", "Digital Rupee (e₹) Retail Pilot Expanded",
        "RBI expands e-Rupee digital currency pilot to 1 lakh users across 13 cities. Supported by SBI, HDFC, ICICI, and other major banks.",
        "Digital", "https://rbi.org.in", "May 2025"),
    GovtNewsItem("13", "National Pension System – Partial Withdrawal Rules Eased",
        "PFRDA allows NPS subscribers to withdraw 25% for marriage, education, medical treatment and home purchase without waiting period.",
        "Pension", "https://npscra.nsdl.co.in", "Jun 2025"),
    GovtNewsItem("14", "Jal Jeevan Mission – 14 Crore Tap Connections",
        "Government reports 14 crore rural households now have functional tap water connections. Target of 19 crore connections by end of 2025.",
        "Infrastructure", "https://jaljeevanmission.gov.in", "Jun 2025"),
    GovtNewsItem("15", "PAN 2.0 – QR-Coded Smart PAN Card",
        "Income Tax Dept launches PAN 2.0 with QR code for instant verification. Existing PAN holders get upgraded card free via online request.",
        "Tax", "https://incometax.gov.in", "Jan 2025", true),
)

val NEWS_CATEGORIES = listOf("All", "Housing", "Health", "Finance", "Tax", "Agriculture", "Digital", "Business", "Savings", "Food Security", "Pension")

// ── Screen ────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GovtNewsScreen(
    onBack: () -> Unit,
    onOpenUrl: (url: String, title: String) -> Unit
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var speakingId by remember { mutableStateOf<String?>(null) }

    // Initialize TTS
    DisposableEffect(context) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("en", "IN")
            }
        }
        onDispose { tts?.stop(); tts?.shutdown() }
    }

    val filtered = remember(selectedCategory, searchQuery) {
        GOVT_NEWS.filter { item ->
            (selectedCategory == "All" || item.category == selectedCategory) &&
            (searchQuery.isBlank() ||
             item.title.contains(searchQuery, ignoreCase = true) ||
             item.summary.contains(searchQuery, ignoreCase = true))
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text("Government Updates")
                            Text("Live scheme news & policy updates",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    actions = {
                        // Stop TTS
                        if (speakingId != null) {
                            IconButton(onClick = {
                                tts?.stop()
                                speakingId = null
                            }) {
                                Icon(Icons.Default.StopCircle, "Stop reading",
                                    tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                )
                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search schemes, policies…") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, "Clear")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Category chips
            item {
                ScrollableCategoryRow(
                    categories = NEWS_CATEGORIES,
                    selected = selectedCategory,
                    onSelect = { selectedCategory = it }
                )
            }

            // Important banner
            val important = filtered.filter { it.isImportant }
            if (important.isNotEmpty() && searchQuery.isBlank() && selectedCategory == "All") {
                item {
                    Text("⚡ Important Updates",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                }
                items(important, key = { "imp_${it.id}" }) { item ->
                    NewsCard(
                        item = item,
                        isSpeaking = speakingId == item.id,
                        onSpeak = {
                            if (speakingId == item.id) {
                                tts?.stop(); speakingId = null
                            } else {
                                tts?.speak("${item.title}. ${item.summary}", TextToSpeech.QUEUE_FLUSH, null, item.id)
                                speakingId = item.id
                            }
                        },
                        onOpenUrl = { onOpenUrl(item.url, item.title) },
                        highlighted = true
                    )
                }
                item {
                    Text("All Updates",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                }
            }

            if (filtered.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(64.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.SearchOff, null,
                                Modifier.size(48.dp), tint = MaterialTheme.colorScheme.outline)
                            Text("No results found", color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            } else {
                items(filtered, key = { it.id }) { item ->
                    NewsCard(
                        item = item,
                        isSpeaking = speakingId == item.id,
                        onSpeak = {
                            if (speakingId == item.id) {
                                tts?.stop(); speakingId = null
                            } else {
                                tts?.speak("${item.title}. ${item.summary}", TextToSpeech.QUEUE_FLUSH, null, item.id)
                                speakingId = item.id
                            }
                        },
                        onOpenUrl = { onOpenUrl(item.url, item.title) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ScrollableCategoryRow(
    categories: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    androidx.compose.foundation.lazy.LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { cat ->
            FilterChip(
                selected = cat == selected,
                onClick = { onSelect(cat) },
                label = { Text(cat) }
            )
        }
    }
}

@Composable
private fun NewsCard(
    item: GovtNewsItem,
    isSpeaking: Boolean,
    onSpeak: () -> Unit,
    onOpenUrl: () -> Unit,
    highlighted: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (highlighted)
                MaterialTheme.colorScheme.primaryContainer.copy(0.4f)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(if (highlighted) 4.dp else 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        item.category,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                Spacer(Modifier.weight(1f))
                if (item.isImportant) {
                    Icon(Icons.Default.PriorityHigh, null,
                        Modifier.size(14.dp), tint = MaterialTheme.colorScheme.error)
                }
                Text(item.date, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline)
            }

            Spacer(Modifier.height(8.dp))

            Text(
                item.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { expanded = !expanded }
            )

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        item.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.8f)
                    )
                }
            }

            if (!expanded) {
                Text(
                    item.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.7f),
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // TTS button
                FilledTonalIconButton(
                    onClick = onSpeak,
                    modifier = Modifier.size(32.dp),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = if (isSpeaking)
                            MaterialTheme.colorScheme.errorContainer
                        else
                            MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Icon(
                        if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                        if (isSpeaking) "Stop" else "Read aloud",
                        Modifier.size(16.dp),
                        tint = if (isSpeaking) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                // Expand / collapse
                TextButton(
                    onClick = { expanded = !expanded },
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(if (expanded) "Show less" else "Read more",
                        style = MaterialTheme.typography.labelMedium)
                }

                Spacer(Modifier.weight(1f))

                // Open official site in WebView
                FilledTonalButton(
                    onClick = onOpenUrl,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.OpenInNew, null, Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Official Site", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
