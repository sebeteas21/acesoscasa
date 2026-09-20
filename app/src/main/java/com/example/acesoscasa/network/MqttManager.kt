package com.example.acesoscasa.network

import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishResult
import kotlinx.coroutines.future.await
import java.util.UUID

object MqttManager {
    private const val BROKER_URL = "10.0.2.2" // IP para el host desde el emulador
    private const val CLIENT_ID = "android_torniquete_emulator"

    private val client = Mqtt5Client.builder()
        .identifier(CLIENT_ID + "_" + UUID.randomUUID().toString().take(8))
        .serverHost(BROKER_URL)
        .serverPort(1883)
        .buildAsync()

    suspend fun connect() {
        try {
            if (!client.state.isConnected) {
                client.connect().await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun publish(topic: String, payload: String): Boolean {
        return try {
            connect()
            val result: Mqtt5PublishResult = client.publishWith()
                .topic(topic)
                .payload(payload.toByteArray())
                .send()
                .await()
            !result.error.isPresent
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun disconnect() {
        if (client.state.isConnected) {
            client.disconnect()
        }
    }
}
