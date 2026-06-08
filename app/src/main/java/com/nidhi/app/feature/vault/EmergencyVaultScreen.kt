package com.nidhi.app.feature.vault

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class VaultCategory(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val items: List<VaultItem>
)

data class VaultItem(val label: String, val value: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyVaultScreen(onBack: () -> Unit) {
    var accessGranted by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    val vaultData = listOf(
        VaultCategory("Insurance Policies", Icons.Default.HealthAndSafety, Color(0xFF2D6A4F), listOf(
            VaultItem("LIC Policy", "345678901 — Endowment Plan — ₹10L"),
            VaultItem("Health Insurance", "HIN2023456 — Family Floater ₹5L"),
            VaultItem("Term Insurance", "HDFC Term — ₹1 Crore — till 2045"),
        )),
        VaultCategory("Nominees", Icons.Default.People, Color(0xFF1565C0), listOf(
            VaultItem("LIC Nominee", "Priya Sharma (Spouse)"),
            VaultItem("Bank Nominee", "Rahul Sharma (Son)"),
            VaultItem("PF Nominee", "Priya Sharma (Spouse)"),
        )),
        VaultCategory("Medical Info", Icons.Default.LocalHospital, Color(0xFFB54A00), listOf(
            VaultItem("Blood Group", "B+"),
            VaultItem("Allergies", "Penicillin, Dust"),
            VaultItem("Chronic Conditions", "Type 2 Diabetes — controlled"),
            VaultItem("Primary Doctor", "Dr. Ramesh Kumar — +91 98765 43210"),
        )),
        VaultCategory("Bank Details", Icons.Default.AccountBalance, Color(0xFF5C3317), listOf(
            VaultItem("SBI Account", "XXXX XXXX 4521 — Branch: MG Road"),
            VaultItem("HDFC Account", "XXXX XXXX 7832 — Savings"),
            VaultItem("Safe Deposit Box", "SBI Locker B-42 — MG Road Branch"),
        )),
        VaultCategory("Property Documents", Icons.Default.Home, listOf(
            VaultItem("House — Koramangala", "Registered 2018 — Sale Deed uploaded"),
            VaultItem("Plot — Mysore Road", "7.5 cents — Khata No. 452"),
        ).let { Color(0xFF6A1B4D) }, listOf(
            VaultItem("House — Koramangala", "Registered 2018 — Sale Deed uploaded"),
            VaultItem("Plot — Mysore Road", "7.5 cents — Khata No. 452"),
        )),
        VaultCategory("Emergency Contacts", Icons.Default.ContactPhone, Color(0xFF880000), listOf(
            VaultItem("Spouse", "Priya Sharma — +91 98765 43210"),
            VaultItem("Son", "Rahul Sharma — +91 87654 32109"),
            VaultItem("Doctor", "Dr. Ramesh Kumar — +91 98765 43210"),
            VaultItem("Lawyer", "Adv. Suresh Iyer — +91 76543 21098"),
        )),
    )

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            icon = { Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Emergency Access") },
            text = { Text("This will show all sensitive family documents and financial information. Only use in genuine emergency situations.") },
            confirmButton = {
                Button(
                    onClick = { accessGranted = true; showConfirm = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Access Emergency Vault") }
            },
            dismissButton = { TextButton(onClick = { showConfirm = false }) { Text("Cancel") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Security, null,
                            tint = MaterialTheme.colorScheme.error)
                        Column {
                            Text("Emergency Vault")
                            Text("Critical family information",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                        }
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
        if (!accessGranted) {
            // Lock screen
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Box(
                        Modifier.size(100.dp).background(
                            Brush.radialGradient(listOf(
                                MaterialTheme.colorScheme.errorContainer,
                                MaterialTheme.colorScheme.error.copy(0.3f)
                            )), RoundedCornerShape(50.dp)
                        ), contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Lock, null,
                            Modifier.size(50.dp), tint = MaterialTheme.colorScheme.error)
                    }

                    Text("Emergency Family Vault",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold)

                    Text(
                        "Contains sensitive information:\n" +
                        "Insurance policies, bank details,\n" +
                        "medical records, nominees & more.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.7f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Button(
                        onClick = { showConfirm = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.LockOpen, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Emergency Access Mode")
                    }

                    Text("Use only in genuine emergencies",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, null,
                                tint = MaterialTheme.colorScheme.error)
                            Text("Emergency Access Active — All critical information visible",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                }

                items(vaultData) { category ->
                    VaultCategoryCard(category)
                }

                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun VaultCategoryCard(category: VaultCategory) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(Modifier.size(36.dp), RoundedCornerShape(8.dp),
                    color = category.color.copy(0.15f)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(category.icon, null,
                            Modifier.size(20.dp), tint = category.color)
                    }
                }
                Text(category.title, style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(10.dp))
            category.items.forEach { item ->
                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(item.label, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
                        modifier = Modifier.weight(0.4f))
                    Text(item.value, style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(0.6f))
                }
                if (item != category.items.last())
                    HorizontalDivider(Modifier.padding(vertical = 2.dp))
            }
        }
    }
}
