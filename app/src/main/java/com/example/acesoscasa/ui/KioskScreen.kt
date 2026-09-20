package com.example.acesoscasa.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acesoscasa.viewmodel.AccessViewModel
import com.example.acesoscasa.viewmodel.KioskUiState

@Composable
fun KioskScreen(viewModel: AccessViewModel) {
    val userId by viewModel.userId.collectAsState()
    val kioskState by viewModel.kioskState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "Bienvenido",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Por favor, ingrese su ID para entrar",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value = userId,
                onValueChange = { viewModel.setUserId(it) },
                label = { Text("ID de Usuario") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.headlineMedium.copy(textAlign = TextAlign.Center),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = CircleShape
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.simulateAccess() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = CircleShape
            ) {
                Text("REGISTRAR INGRESO", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Overlay de Estado
        AnimatedVisibility(
            visible = kioskState !is KioskUiState.Idle,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            KioskOverlay(state = kioskState)
        }
    }
}

@Composable
fun KioskOverlay(state: KioskUiState) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.size(300.dp),
            colors = CardDefaults.cardColors(
                containerColor = when (state) {
                    is KioskUiState.Success -> Color(0xFF4CAF50)
                    is KioskUiState.Error -> Color(0xFFF44336)
                    else -> MaterialTheme.colorScheme.surface
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (state) {
                    is KioskUiState.Loading -> {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Procesando...", fontWeight = FontWeight.Bold)
                    }
                    is KioskUiState.Success -> {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(100.dp), tint = Color.White)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("ACCESO CONCEDIDO", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        Text("Bienvenido, ${state.userId}", color = Color.White)
                    }
                    is KioskUiState.Error -> {
                        Icon(Icons.Default.Error, contentDescription = null, modifier = Modifier.size(100.dp), tint = Color.White)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("ACCESO DENEGADO", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        Text(state.message, color = Color.White)
                    }
                    else -> {}
                }
            }
        }
    }
}
