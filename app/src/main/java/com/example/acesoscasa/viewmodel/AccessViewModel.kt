package com.example.acesoscasa.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acesoscasa.data.AccessEventDto
import com.example.acesoscasa.network.AmqManager
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class LogEntry(
    val timestamp: String,
    val message: String,
    val isError: Boolean = false
)

sealed class KioskUiState {
    object Idle : KioskUiState()
    object Loading : KioskUiState()
    data class Success(val userId: String) : KioskUiState()
    data class Error(val message: String) : KioskUiState()
}

class AccessViewModel : ViewModel() {

    private val _userId = MutableStateFlow("")
    val userId = _userId.asStateFlow()

    private val _deviceId = MutableStateFlow("Torniquete_Entrada_Principal")
    val deviceId = _deviceId.asStateFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending = _isSending.asStateFlow()

    private val _kioskState = MutableStateFlow<KioskUiState>(KioskUiState.Idle)
    val kioskState = _kioskState.asStateFlow()

    val logs = mutableStateListOf<LogEntry>()

    fun setUserId(id: String) {
        _userId.value = id
    }

    fun setDeviceId(id: String) {
        _deviceId.value = id
    }

    fun simulateAccess() {
        if (_userId.value.isBlank()) {
            addLog("Error: El ID de usuario no puede estar vacío", true)
            return
        }

        viewModelScope.launch {
            _kioskState.value = KioskUiState.Loading
            val success = sendAccessRequest(_deviceId.value, _userId.value)
            if (success) {
                _kioskState.value = KioskUiState.Success(_userId.value)
                delay(3000) // Mostrar mensaje de éxito por 3 segundos
                _kioskState.value = KioskUiState.Idle
            } else {
                _kioskState.value = KioskUiState.Error("Acceso Denegado")
                delay(3000)
                _kioskState.value = KioskUiState.Idle
            }
        }
    }

    fun resetKiosk() {
        _kioskState.value = KioskUiState.Idle
    }

    fun generateBurst() {
        if (_userId.value.isBlank()) {
            addLog("Error: El ID de usuario no puede estar vacío para la ráfaga", true)
            return
        }

        viewModelScope.launch {
            addLog("Iniciando ráfaga automática de 10 registros...")
            repeat(10) { index ->
                sendAccessRequest(_deviceId.value, "${_userId.value}-$index")
                delay(1000)
            }
            addLog("Ráfaga automática completada.")
        }
    }

    private suspend fun sendAccessRequest(deviceId: String, userId: String): Boolean {
        _isSending.value = true
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        val event = AccessEventDto(deviceId, userId, timestamp)
        val payload = Gson().toJson(event)

        return try {
            val success = AmqManager.publish("torniquete.acceso", payload)
            if (success) {
                addLog("Éxito (AMQ/AMQP): Mensaje enviado para $userId")
                true
            } else {
                addLog("Error (AMQ/AMQP): No se pudo enviar el mensaje", true)
                false
            }
        } catch (e: Exception) {
            addLog("Error de Red AMQ: ${e.localizedMessage}", true)
            false
        } finally {
            _isSending.value = false
        }
    }

    private fun addLog(message: String, isError: Boolean = false) {
        val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        logs.add(0, LogEntry(time, message, isError))
    }
}
