package com.example.acesoscasa.network

import org.apache.qpid.jms.JmsConnectionFactory
import jakarta.jms.Connection
import jakarta.jms.DeliveryMode
import jakarta.jms.MessageProducer
import jakarta.jms.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AmqManager {
    // Conexión AMQP estándar (ej. Apache ActiveMQ / Artemis en puerto 5672)
    // 10.0.2.2 es la IP loopback del emulador hacia la máquina anfitriona
    private const val BROKER_URL = "amqp://10.0.2.2:5672"
    
    private var connection: Connection? = null

    suspend fun connect() = withContext(Dispatchers.IO) {
        try {
            if (connection == null) {
                val factory = JmsConnectionFactory("admin", "admin", BROKER_URL)
                connection = factory.createConnection().apply {
                    start()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun publish(destinationName: String, payload: String): Boolean = withContext(Dispatchers.IO) {
        var session: Session? = null
        var producer: MessageProducer? = null
        try {
            connect()
            val conn = connection ?: return@withContext false
            session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE)
            val destination = session.createQueue(destinationName)
            producer = session.createProducer(destination).apply {
                deliveryMode = DeliveryMode.NON_PERSISTENT
            }
            val message = session.createTextMessage(payload)
            producer.send(message)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            try {
                producer?.close()
                session?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun disconnect() {
        try {
            connection?.close()
            connection = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
