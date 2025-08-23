package org.crashvibe.FGateClient.listeners

import kotlinx.serialization.Serializable
import org.crashvibe.FGateClient.websocket.WebSocketManager

object OnDeathService {
  @Serializable
  data class DeathInfo(val playerName: String, val deathMessage: String?)

  fun handleDeath(playerName: String, deathMessage: String?) {
    if (!WebSocketManager.instance.isOpen) {
      return
    }

    val request = DeathInfo(playerName, deathMessage)

    WebSocketManager.instance.sendNotice("player.death", request)
  }
}
