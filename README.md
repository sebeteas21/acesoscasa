# Simulador de Acceso IoT - Torniquete (Android)

Esta aplicación simula el comportamiento de un torniquete de acceso físico que envía eventos a un ecosistema IoT.

## Ejecución del Proyecto

1. **Requisitos**: Android Studio Ladybug o superior, JDK 17+.
2. **Configuración del Broker**: La aplicación está configurada por defecto para conectarse a un broker AMQ / AMQP (como ActiveMQ Artemis) en `10.0.2.2` (localhost desde el emulador de Android) en el puerto `5672`.
3. **Instalación**:
   - Clona el repositorio.
   - Sincroniza Gradle.
   - Ejecuta en un emulador con API 26 o superior.

## Conexión con el Backend (Laravel + AMQP / ActiveMQ)

Para que el backend reciba los datos, debe estar escuchando el broker AMQP al que la aplicación envía los eventos mediante JMS.

### Datos de Integración:
- **Protocolo**: AMQP 1.0 (JMS / Qpid JMS)
- **Broker (Local)**: `amqp://10.0.2.2:5672`
- **Destino / Cola (Queue)**: `torniquete.acceso`
- **Formato de Mensaje**: JSON

### Estructura del Payload (JSON):
Al registrar un acceso, la app envía un mensaje con la siguiente estructura:

```json
{
  "device_id": "Torniquete_Entrada_Principal",
  "user_id": "USR-1024",
  "timestamp": "2026-09-20T14:42:20.210",
  "status": "GRANTED"
}
```
