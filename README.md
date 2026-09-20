# Simulador de Acceso IoT - Torniquete (Android)

Esta aplicación simula el comportamiento de un torniquete de acceso físico que envía eventos a un ecosistema IoT.

## Ejecución del Proyecto

1. **Requisitos**: Android Studio Ladybug o superior, JDK 17+.
2. **Configuración del Broker**: La aplicación está configurada por defecto para conectarse a un broker MQTT en `10.0.2.2` (localhost desde el emulador de Android) en el puerto `1883`.
3. **Instalación**:
   - Clona el repositorio.
   - Sincroniza Gradle.
   - Ejecuta en un emulador con API 26 o superior.

## 🔌 Conexión con el Backend (Laravel + MQTT)

Para que el backend reciba los datos, debe estar escuchando el broker MQTT al que la aplicación publica los eventos.

### Datos de Integración:
- **Protocolo**: MQTT v5
- **Broker (Local)**: `10.0.2.2:1883`
- **Topic de Publicación**: `torniquete/acceso`
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
