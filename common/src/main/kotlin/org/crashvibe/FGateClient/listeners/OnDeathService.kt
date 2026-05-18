package org.crashvibe.FGateClient.listeners

import kotlinx.serialization.Serializable
import org.crashvibe.FGateClient.websocket.WebSocketManager

object OnDeathService {
  @Serializable
  data class DeathInfo(val playerUUID: String, val playerName: String, val deathMessage: String?, val timestamp: Long)

  fun handleDeath(playerUUID: String, playerName: String, deathMessage: String?, timestamp: Long = System.currentTimeMillis()) {
    if (!WebSocketManager.instance.isOpen) {
      return
    }

    val request = DeathInfo(playerUUID, playerName, deathMessage, timestamp)

    WebSocketManager.instance.sendNotice("player.death", request)
  }
}
