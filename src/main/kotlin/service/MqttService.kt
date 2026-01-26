package edu.mci.service

import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.slf4j.LoggerFactory

class MqttService(
    private val brokerUrl: String,
    private val clientId: String
) {
    private val logger = LoggerFactory.getLogger(MqttService::class.java)
    private var client: MqttClient? = null

    init {
        connect()
    }

    private fun connect() {
        try {
            client = MqttClient(brokerUrl, clientId, MemoryPersistence())
            val options = MqttConnectOptions()
            options.isCleanSession = true
            options.connectionTimeout = 10
            client?.connect(options)
            logger.info("Connected to MQTT broker at $brokerUrl")
        } catch (e: Exception) {
            logger.error("Failed to connect to MQTT broker: ${e.message}")
        }
    }

    fun publishRoomCode(roomId: Int, code: String) {
        val topic = "room/$roomId/code"
        try {
            if (client == null || !client!!.isConnected) {
                logger.warn("MQTT client not connected, attempting to (re)connect...")
                connect()
            }
            if (client?.isConnected == true) {
                val message = org.eclipse.paho.client.mqttv3.MqttMessage(code.toByteArray())
                message.qos = 1
                message.isRetained = true // Retain so the screen gets it immediately on connection
                client?.publish(topic, message)
                logger.info("Published code to $topic")
            } else {
                logger.error("Could not publish to $topic: Client not connected")
            }
        } catch (e: Exception) {
            logger.error("Error publishing to $topic", e)
        }
    }

    fun publishUnlockDoor(roomId: Int) {
        val topic = "room/$roomId/door"
        publishMessage(topic, "UNLOCK")
    }

    fun publishTurnOnLight(roomId: Int) {
        val topic = "room/$roomId/light"
        publishMessage(topic, "ON")
    }

    fun publishTurnOnHVAC(roomId: Int) {
        val topic = "room/$roomId/hvac"
        publishMessage(topic, "ON")
    }

    private fun publishMessage(topic: String, payload: String) {
        try {
            if (client == null || !client!!.isConnected) {
                logger.warn("MQTT client not connected, attempting to (re)connect...")
                connect()
            }
            if (client?.isConnected == true) {
                val message = org.eclipse.paho.client.mqttv3.MqttMessage(payload.toByteArray())
                message.qos = 1
                message.isRetained = false
                client?.publish(topic, message)
                logger.info("Published $payload to $topic")
            } else {
                logger.error("Could not publish to $topic: Client not connected")
            }
        } catch (e: Exception) {
            logger.error("Error publishing to $topic", e)
        }



}
}
