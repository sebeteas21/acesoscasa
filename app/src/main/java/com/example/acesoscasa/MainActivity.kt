package com.example.acesoscasa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.acesoscasa.ui.AccessScreen
import com.example.acesoscasa.ui.KioskScreen
import com.example.acesoscasa.ui.theme.AcesoscasaTheme
import com.example.acesoscasa.viewmodel.AccessViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AcesoscasaTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val viewModel: AccessViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Simulador") },
                    label = { Text("Simulador") },
                    selected = currentRoute == "simulator",
                    onClick = {
                        if (currentRoute != "simulator") {
                            navController.navigate("simulator") {
                                popUpTo("simulator") { inclusive = true }
                            }
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Cast, contentDescription = "Kiosco") },
                    label = { Text("Kiosco") },
                    selected = currentRoute == "kiosk",
                    onClick = {
                        if (currentRoute != "kiosk") {
                            navController.navigate("kiosk")
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "simulator",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("simulator") {
                AccessScreen(viewModel)
            }
            composable("kiosk") {
                KioskScreen(viewModel)
            }
        }
    }
}
