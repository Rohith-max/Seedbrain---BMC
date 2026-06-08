package com.nidhi.app.feature.lifeevent

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class LifeEvent(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val description: String,
    val checklist: List<ChecklistItem>
)

data class ChecklistItem(
    val id: String,
    val task: String,
    val category: String,
    val priority: String = "medium",
    var done: Boolean = false
)

private val LIFE_EVENTS = listOf(
    LifeEvent("marriage", "Getting Married", Icons.Default.Favorite,
        "Update all documents and accounts after marriage",
        listOf(
            ChecklistItem("m1", "Update Aadhaar with new address", "Documents", "high"),
            ChecklistItem("m2", "Update PAN card — name change if applicable", "Documents", "high"),
            ChecklistItem("m3", "Update bank account nominee", "Finance", "high"),
            ChecklistItem("m4", "Update LIC/insurance nominee", "Insurance", "high"),
            ChecklistItem("m5", "Update EPF nominee", "Finance", "high"),
            ChecklistItem("m6", "Joint bank account opening", "Finance", "medium"),
            ChecklistItem("m7", "Health insurance — add spouse", "Insurance", "medium"),
            ChecklistItem("m8", "Marriage certificate registration", "Legal", "high"),
            ChecklistItem("m9", "Update passport if name changed", "Documents", "medium"),
            ChecklistItem("m10", "Will / nomination update", "Legal", "medium"),
        )),
    LifeEvent("baby", "Having a Baby", Icons.Default.ChildCare,
        "Register and document your newborn",
        listOf(
            ChecklistItem("b1", "Birth certificate registration (within 21 days)", "Legal", "high"),
            ChecklistItem("b2", "Apply for Aadhaar for child", "Documents", "high"),
            ChecklistItem("b3", "Add child to health insurance", "Insurance", "high"),
            ChecklistItem("b4", "Sukanya Samriddhi Yojana (for girl child)", "Finance", "high"),
            ChecklistItem("b5", "Open PPF account for child's future", "Finance", "medium"),
            ChecklistItem("b6", "Update LIC nominee — include child", "Insurance", "medium"),
            ChecklistItem("b7", "Register for PMJAY — add family member", "Benefits", "medium"),
            ChecklistItem("b8", "School admission preparation — age 3+", "Education", "low"),
        )),
    LifeEvent("house", "Buying a House", Icons.Default.Home,
        "Complete checklist for home purchase",
        listOf(
            ChecklistItem("h1", "Verify property title — check encumbrances", "Legal", "high"),
            ChecklistItem("h2", "Check RERA registration of builder", "Legal", "high"),
            ChecklistItem("h3", "Home loan pre-approval from bank", "Finance", "high"),
            ChecklistItem("h4", "Sale deed registration at sub-registrar office", "Legal", "high"),
            ChecklistItem("h5", "Khata / mutation in your name", "Legal", "high"),
            ChecklistItem("h6", "Property tax transfer to your name", "Legal", "medium"),
            ChecklistItem("h7", "Home insurance policy", "Insurance", "medium"),
            ChecklistItem("h8", "Tax benefit claim — 80C and 24(b)", "Finance", "medium"),
            ChecklistItem("h9", "Upload sale deed and khata to NIDHI", "Documents", "medium"),
            ChecklistItem("h10", "PMAY application if eligible", "Benefits", "high"),
        )),
    LifeEvent("retirement", "Planning Retirement", Icons.Default.Weekend,
        "Ensure financial security post-retirement",
        listOf(
            ChecklistItem("r1", "Calculate pension corpus needed", "Finance", "high"),
            ChecklistItem("r2", "EPF withdrawal or pension claim", "Finance", "high"),
            ChecklistItem("r3", "NPS annuity selection", "Finance", "high"),
            ChecklistItem("r4", "Senior citizen savings scheme (SCSS)", "Finance", "high"),
            ChecklistItem("r5", "Health insurance — increase coverage", "Insurance", "high"),
            ChecklistItem("r6", "Make a will / digital will", "Legal", "high"),
            ChecklistItem("r7", "Nominee update across all assets", "Legal", "high"),
            ChecklistItem("r8", "Senior citizen benefits — income tax", "Finance", "medium"),
            ChecklistItem("r9", "Varishtha Pension Bima Yojana check", "Benefits", "medium"),
        )),
    LifeEvent("death", "Loss of Family Member", Icons.Default.HeartBroken,
        "Emergency checklist when a family member passes",
        listOf(
            ChecklistItem("d1", "Death certificate registration (within 21 days)", "Legal", "high"),
            ChecklistItem("d2", "Claim LIC/insurance as nominee", "Insurance", "high"),
            ChecklistItem("d3", "Bank account — survivor / nominee claim", "Finance", "high"),
            ChecklistItem("d4", "EPF / EPS pension claim", "Finance", "high"),
            ChecklistItem("d5", "Property mutation in survivor's name", "Legal", "high"),
            ChecklistItem("d6", "PAN card cancellation of deceased", "Documents", "medium"),
            ChecklistItem("d7", "Aadhaar — notify UIDAI", "Documents", "medium"),
            ChecklistItem("d8", "Succession certificate if no will", "Legal", "high"),
        )),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifeEventScreen(onBack: () -> Unit) {
    var selectedEvent by remember { mutableStateOf<LifeEvent?>(null) }
    val checkedItems = remember { mutableStateMapOf<String, Boolean>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(selectedEvent?.title ?: "Life Event Engine")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedEvent != null) selectedEvent = null else onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (selectedEvent == null) {
            LazyColumn(
                Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("What life event are you planning?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold)
                    Text("NIDHI generates a personalised checklist with all the documents and actions you need.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.65f))
                    Spacer(Modifier.height(8.dp))
                }
                items(LIFE_EVENTS) { event ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { selectedEvent = event }
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Surface(Modifier.size(48.dp), RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(event.icon, null,
                                        Modifier.size(26.dp), tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                            Column(Modifier.weight(1f)) {
                                Text(event.title, style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold)
                                Text(event.description, style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(0.65f))
                                Text("${event.checklist.size} tasks",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary)
                            }
                            Icon(Icons.Default.ChevronRight, null,
                                tint = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
        } else {
            val event = selectedEvent!!
            val doneCount = event.checklist.count { checkedItems[it.id] == true }

            LazyColumn(
                Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    LinearProgressIndicator(
                        progress = { doneCount.toFloat() / event.checklist.size },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("$doneCount / ${event.checklist.size} tasks completed",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(8.dp))
                }

                val byCategory = event.checklist.groupBy { it.category }
                byCategory.forEach { (cat, items) ->
                    item {
                        Text(cat.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
                    }
                    items(items) { task ->
                        val checked = checkedItems[task.id] ?: false
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Checkbox(
                                    checked = checked,
                                    onCheckedChange = { checkedItems[task.id] = it }
                                )
                                Column(Modifier.weight(1f)) {
                                    Text(task.task,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (checked)
                                            MaterialTheme.colorScheme.onSurface.copy(0.4f)
                                        else MaterialTheme.colorScheme.onSurface)
                                }
                                Surface(
                                    color = when (task.priority) {
                                        "high" -> MaterialTheme.colorScheme.errorContainer
                                        "medium" -> MaterialTheme.colorScheme.tertiaryContainer
                                        else -> MaterialTheme.colorScheme.secondaryContainer
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(task.priority,
                                        Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = when (task.priority) {
                                            "high" -> MaterialTheme.colorScheme.error
                                            else   -> MaterialTheme.colorScheme.onSecondaryContainer
                                        })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
