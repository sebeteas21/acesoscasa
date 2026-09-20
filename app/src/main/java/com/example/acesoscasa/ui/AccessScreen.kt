package com.example.acesoscasa.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.acesoscasa.viewmodel.AccessViewModel
import com.example.acesoscasa.viewmodel.LogEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessScreen(viewModel: AccessViewModel = viewModel()) {
    val userId by viewModel.userId.collectAsState()
    val deviceId by viewModel.deviceId.collectAsState()
    val isSending by viewModel.isSending.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Simulador Torniquete IoT") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AccessForm(
                userId = userId,
                deviceId = deviceId,
                isSending = isSending,
                onUserIdChange = viewModel::setUserId,
                onDeviceIdChange = viewModel::setDeviceId,
                onSimulate = viewModel::simulateAccess,
                onGenerateBurst = viewModel::generateBurst
            )

            HorizontalDivider()

            Text(
                text = "Registro de Eventos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            LogList(logs = viewModel.logs)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessForm(
    userId: String,
    deviceId: String,
    isSending: Boolean,
    onUserIdChange: (String) -> Unit,
    onDeviceIdChange: (String) -> Unit,
    onSimulate: () -> Unit,
    onGenerateBurst: () -> Unit
) {
    val devices = listOf("Torniquete_Entrada_Principal", "Torniquete_Norte", "Torniquete_Estacionamiento")
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = deviceId,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Dispositivo / Torniquete") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true)
                        .fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    devices.forEach { device ->
                        DropdownMenuItem(
                            text = { Text(device) },
                            onClick = {
                                onDeviceIdChange(device)
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = userId,
                onValueChange = onUserIdChange,
                label = { Text("ID de Usuario") },
                placeholder = { Text("ej. USR-1024") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSimulate,
                    enabled = !isSending,
                    modifier = Modifier.weight(1f)
                ) {
                    if (isSending) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Simular Entrada")
                    }
                }

                FilledTonalButton(
                    onClick = onGenerateBurst,
                    enabled = !isSending,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Ráfaga (10)")
                }
            }
        }
    }
}

@Composable
fun LogList(logs: List<LogEntry>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(logs) { entry ->
            LogItem(entry)
        }
    }
}

@Composable
fun LogItem(entry: LogEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (entry.isError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = if (entry.isError) Icons.Default.Warning else Icons.Default.Info,
                contentDescription = null,
                tint = if (entry.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
            Column {
                Text(
                    text = entry.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Text(
                    text = entry.message,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
