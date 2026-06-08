package com.nidhi.app.feature.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TimelineEvent(
    val year: String,
    val month: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val tag: String,
    val isFuture: Boolean = false
)

private val DEMO_TIMELINE = listOf(
    TimelineEvent("2026","Jun","LIC Premium Due","Policy 345678901 — ₹18,500 annual premium",
        Icons.Default.Payment, Color(0xFFBF5700), "Finance"),
    TimelineEvent("2026","Aug","Passport Expiry","Renew before international travel",
        Icons.Default.Warning, Color(0xFFBA1A1A), "Urgent", isFuture = true),
    TimelineEvent("2026","Dec","FD Maturity","SBI FD — ₹2,50,000 matures",
        Icons.Default.AccountBalance, Color(0xFF2D6A4F), "Finance", isFuture = true),
    TimelineEvent("2025","Nov","PMJAY Renewal","Ayushman Bharat card renewed",
        Icons.Default.HealthAndSafety, Color(0xFF1565C0), "Benefits"),
    TimelineEvent("2024","Mar","Home Loan EMI","HDFC Home Loan — 180 EMIs remaining",
        Icons.Default.Home, Color(0xFF5C3317), "Finance"),
    TimelineEvent("2023","Sep","Property Registered","Flat 4B, Koramangala — Registered",
        Icons.Default.House, Color(0xFF2D6A4F), "Property"),
    TimelineEvent("2023","Jan","Daughter's School","Aaradhya admitted — DPS Bangalore",
        Icons.Default.School, Color(0xFF6A1B4D), "Family"),
    TimelineEvent("2022","Apr","Health Insurance","Family Floater ₹10L — Star Health",
        Icons.Default.Favorite, Color(0xFFBA1A1A), "Insurance"),
    TimelineEvent("2020","Jun","Son's Birth","Arjun born — Birth certificate uploaded",
        Icons.Default.ChildCare, Color(0xFF1565C0), "Family"),
    TimelineEvent("2019","Feb","Home Loan Started","HDFC ₹45L — 20 year tenure",
        Icons.Default.AccountBalance, Color(0xFF5C3317), "Finance"),
    TimelineEvent("2018","Nov","LIC Purchased","Endowment Plan ₹10L — 20 year",
        Icons.Default.HealthAndSafety, Color(0xFF2D6A4F), "Insurance"),
    TimelineEvent("2017","Jun","Marriage","Registered at Sub-Registrar Office, Bangalore",
        Icons.Default.Favorite, Color(0xFF880E4F), "Family"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyTimelineScreen(onBack: () -> Unit) {
    val upcomingEvents = DEMO_TIMELINE.filter { it.isFuture }
    val pastEvents     = DEMO_TIMELINE.filter { !it.isFuture }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Family Timeline")
                        Text("Your family's financial journey",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Upcoming section
            if (upcomingEvents.isNotEmpty()) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 12.dp)) {
                        Icon(Icons.Default.Upcoming, null,
                            tint = MaterialTheme.colorScheme.error)
                        Text("Upcoming", style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error)
                    }
                }
                items(upcomingEvents) { event ->
                    TimelineCard(event, isLast = event == upcomingEvents.last())
                }
                item { Spacer(Modifier.height(16.dp)) }
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 12.dp)) {
                    Icon(Icons.Default.History, null,
                        tint = MaterialTheme.colorScheme.primary)
                    Text("History", style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold)
                }
            }
            items(pastEvents) { event ->
                TimelineCard(event, isLast = event == pastEvents.last())
            }
        }
    }
}

@Composable
private fun TimelineCard(event: TimelineEvent, isLast: Boolean) {
    Row(Modifier.fillMaxWidth()) {
        // Timeline line and dot
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(56.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(event.month, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline, fontSize = 10.sp)
                Text(event.year, style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(4.dp))
            Box(
                Modifier.size(32.dp).clip(CircleShape)
                    .background(event.color.copy(0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(event.icon, null, Modifier.size(16.dp), tint = event.color)
            }
            if (!isLast) {
                Box(
                    Modifier.width(2.dp).height(40.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(0.3f))
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        // Event card
        Card(
            modifier = Modifier.weight(1f).padding(bottom = if (isLast) 0.dp else 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (event.isFuture)
                    MaterialTheme.colorScheme.errorContainer.copy(0.3f)
                else MaterialTheme.colorScheme.surface
            )
        ) {
            Column(Modifier.padding(12.dp)) {
                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(event.title, style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    Surface(color = event.color.copy(0.15f), shape = RoundedCornerShape(6.dp)) {
                        Text(event.tag, Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall, color = event.color)
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(event.description, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
            }
        }
    }
}
